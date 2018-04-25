/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ngrinder.monitor.collector;

import org.apache.commons.io.IOUtils;
import org.hyperic.sigar.*;
import org.ngrinder.common.constants.MonitorConstants;
import org.ngrinder.common.util.NoOp;
import org.ngrinder.monitor.mxbean.SystemMonitoringData;
import org.ngrinder.monitor.share.domain.BandWidth;
import org.ngrinder.monitor.share.domain.DiskBusy;
import org.ngrinder.monitor.share.domain.SystemInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * System data collector class.
 *
 * @author Mavlarn
 * @since 2.0
 * @modify lingj
 */
public class SystemDataCollector extends DataCollector implements MonitorConstants {
	private static final Logger LOGGER = LoggerFactory.getLogger(SystemDataCollector.class);

	private Sigar sigar = null;

	private SystemInfo prev = null;

	private String[] netInterfaces = new String[]{};

	private FileSystem[] fileSystems = new FileSystem[]{};

	private File customDataFile = null;

	/**
	 * Set Agent Home.
	 *
	 * @param agentHomeFile agentHomeFile
	 */
	public void setAgentHome(File agentHomeFile) {
		if (customDataFile == null) {
			customDataFile = new File(agentHomeFile, "monitor" + File.separator + "custom.data");
		}
	}

	@Override
	public synchronized void refresh() {
		initSigar();
	}

	private void initSigar() {
		if (sigar == null) {
			sigar = new Sigar();
			try {
				netInterfaces = sigar.getNetInterfaceList();
				fileSystems = sigar.getFileSystemList();//add by lingj
				prev = new SystemInfo();
				prev.setBandWidth(getNetworkUsage());
				prev.setDiskBusy(getFileSystemList());//add by lingj
			} catch (SigarException e) {
				LOGGER.error("Network usage data retrieval failed.", e);
			}
		}
	}

	@Override
	public void run() {
		initSigar();
		SystemMonitoringData systemMonitoringData = (SystemMonitoringData) getMXBean(SYSTEM);
		systemMonitoringData.setSystemInfo(execute());
	}

	/**
	 * Execute the collector to get the system info model.
	 *
	 * @return SystemInfo in current time
	 */
	public synchronized SystemInfo execute() {
		SystemInfo systemInfo = new SystemInfo();
		systemInfo.setCollectTime(System.currentTimeMillis());
		try {
			BandWidth networkUsage = getNetworkUsage();
			BandWidth bandWidth = networkUsage.adjust(prev.getBandWidth());
			systemInfo.setBandWidth(bandWidth);

			//add by lingj,新增磁盘读写速率
			DiskBusy fileSystemList = getFileSystemList();
			DiskBusy diskBusy = fileSystemList.adjust(prev.getDiskBusy());
			systemInfo.setDiskBusy(diskBusy);
//			systemInfo.setCPUUsedPercentage((float) sigar.getCpuPerc().getCombined() * 100);
			systemInfo.setCPUUsedPercentage((float) (1-sigar.getCpuPerc().getIdle()) * 100); //修改CPU使用率为1-idle%
			systemInfo.setCpuWait((float) (sigar.getCpuPerc().getWait()) * 100); //CPU等待率
			Cpu cpu = sigar.getCpu();
			systemInfo.setTotalCpuValue(cpu.getTotal());
			systemInfo.setIdleCpuValue(cpu.getIdle());
			Mem mem = sigar.getMem();
			systemInfo.setTotalMemory(mem.getTotal() / 1024L);
			systemInfo.setFreeMemory(mem.getActualFree() / 1024L);

			systemInfo.setMemUsedPercentage(mem.getUsedPercent()); //内存使用率
			systemInfo.setSystem(OperatingSystem.IS_WIN32 ? SystemInfo.System.WINDOW : SystemInfo.System.LINUX);
			systemInfo.setCustomValues(getCustomMonitorData());

			if (systemInfo.getSystem() == SystemInfo.System.LINUX) {
				IoUsageCollector iousage = new IoUsageCollector();
				systemInfo.setDiskUtil(iousage.getIoUsage());
			}

			double load = sigar.getLoadAverage()[0];
			systemInfo.setLoad(load);

		} catch (Throwable e) {
			LOGGER.debug("Error while getting system perf data:{}", e);
			LOGGER.debug("Error trace is ", e);
		}
		prev = systemInfo;
		return systemInfo;
	}

	/**
	 * Get the current network usage.
	 *
	 * @return BandWith
	 * @throws SigarException thrown when the underlying lib is not linked
	 */
	public BandWidth getNetworkUsage() throws SigarException {
		BandWidth bandWidth = new BandWidth(System.currentTimeMillis());
		for (String each : netInterfaces) {
			try {
				NetInterfaceStat netInterfaceStat = sigar.getNetInterfaceStat(each);
				bandWidth.setReceived(bandWidth.getReceived() + netInterfaceStat.getRxBytes());
				bandWidth.setSent(bandWidth.getSent() + netInterfaceStat.getTxBytes());
			} catch (Exception e) {
				NoOp.noOp();
			}
		}
		return bandWidth;
	}

	/**
	 * add by lingj
	 * Get the current Disk Read and Write usage.
	 *
	 * @return DiskBusy
	 * @throws SigarException thrown when the underlying lib is not linked
	 */
	public DiskBusy getFileSystemList() throws SigarException {
		DiskBusy diskBusy = new DiskBusy(System.currentTimeMillis());
		// 获取本地文件系统
		List<String> localDevNames = new ArrayList<String>();
		for(FileSystem fileSystem : fileSystems) {
			if(fileSystem.getType() == FileSystem.TYPE_LOCAL_DISK) {
				localDevNames.add(fileSystem.getDevName());
			}
		}
		for (String each : localDevNames) {
			try {
				DiskUsage diskUsage = sigar.getDiskUsage(each);
				diskBusy.setRead(diskBusy.getRead() + diskUsage.getReadBytes());
				diskBusy.setWrite(diskBusy.getWrite() + diskUsage.getWriteBytes());
			} catch (Exception e) {
				NoOp.noOp();
			}
		}
		return diskBusy;
	}

	private String getCustomMonitorData() {
		if (customDataFile != null && customDataFile.exists()) {
			BufferedReader customDataFileReader = null;
			try {
				customDataFileReader = new BufferedReader(new FileReader(customDataFile));
				return customDataFileReader.readLine(); // these data will be parsed at
				// monitor client side.
			} catch (IOException e) {
				// Error here is very natural
				LOGGER.debug("Error to read custom monitor data", e);
			} finally {
				IOUtils.closeQuietly(customDataFileReader);
			}
		}
		return prev.getCustomValues();
	}

}
