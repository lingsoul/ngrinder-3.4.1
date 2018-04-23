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
package org.ngrinder.monitor.share.domain;

import java.io.Serializable;
import java.util.Date;

import javax.management.openmbean.CompositeData;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.ngrinder.common.util.DateUtils;

/**
 * System info object to save date collected by monitor.
 *
 * @author Mavlarn
 * @since 2.0
 * @modify lingj
 */
public class SystemInfo extends MonitorInfo implements Serializable {

	private static final long serialVersionUID = -2995334644975166549L;

	/**
	 * Header field of monitor status fields.
	 */
	//add by lingj 新增cpuWait,memUsedPercentage,load,diskUtil字段拼接
	public static final String HEADER = "ip,system,collectTime,freeMemory,"
		+ "totalMemory,cpuUsedPercentage,receivedPerSec,sentPerSec,cpuWait,memUsedPercentage,load,diskUtil,customValues";


	public boolean isParsed() {
		return true;
	}

	/**
	 * Enum for the system type, linux or windows.
	 */
	public enum System {
		LINUX, WINDOW
	}

	private System system;

	protected BandWidth bandWidth;

	private long totalCpuValue;

	private long idleCpuValue;

	private long freeMemory;

	private long totalMemory;

	private float cpuUsedPercentage;

	private String ip;

	protected String customValues;

	//add by lingj
	// load
	private double load;

	//cpu等待率
	private float cpuWait;

	// 磁盘读
	private long read;

	// 磁盘写
	private long write;

	// 磁盘读写率
	private float diskUtil;

	// 内存使用率
	private double memUsedPercentage;

	@Override
	public void parse(CompositeData cd) {
		if (cd == null) {
			return;
		}
		try {
			long collectTime = getLong(cd, "collectTime");
			setCollectTime(collectTime);
			String string = getString(cd, "system");
			this.system = System.valueOf(string);
			this.totalCpuValue = getLong(cd, "totalCpuValue");
			this.idleCpuValue = getLong(cd, "idlecpu");
			this.freeMemory = getLong(cd, "freeMemory");
			this.totalMemory = getLong(cd, "totalMemory");
			this.cpuUsedPercentage = getFloat(cd, "CPUUsedPercentage");

			//add by lingj 新增cpuWait,memUsedPercentage,load,diskUtil等字段定义
			this.load = getDouble(cd, "load");
			this.cpuWait = getFloat(cd, "cpuWait");
			this.read = getLong(cd, "read");
			this.write = getLong(cd, "write");
			this.diskUtil = getFloat(cd, "diskUtil");
			this.memUsedPercentage = getDouble(cd, "memUsedPercentage");
			//-----------------新增end--------------------

			if (containsKey(cd, "bandWidth")) {
				CompositeData bandWidth = (CompositeData) getObject(cd, "bandWidth");
				this.bandWidth = new BandWidth(collectTime);
				long receivedPerSec = getLong(bandWidth, "receivedPerSec");
				long sentPerSec = getLong(bandWidth, "sentPerSec");
				this.bandWidth.setReceivedPerSec(receivedPerSec);
				this.bandWidth.setSentPerSec(sentPerSec);
			}
			if (containsKey(cd, "customValues")) {
				this.setCustomValues(getString(cd, "customValues"));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public System getSystem() {
		return system;
	}

	public void setSystem(System system) {
		this.system = system;
	}

	public void setSystem(String system) {
		this.system = System.valueOf(system);
	}

	public void setCPUUsedPercentage(float cpuUsedPercentage) {
		this.cpuUsedPercentage = cpuUsedPercentage;
	}

	public float getCPUUsedPercentage() {
		return cpuUsedPercentage;
	}

	@SuppressWarnings("UnusedDeclaration")
	public long getIdlecpu() {
		return idleCpuValue;
	}

	@SuppressWarnings("UnusedDeclaration")
	public long getTotalCpuValue() {
		return totalCpuValue;
	}

	public long getFreeMemory() {
		return freeMemory;
	}

	public void setTotalCpuValue(long totalCpuValue) {
		this.totalCpuValue = totalCpuValue;
	}

	public void setIdleCpuValue(long idleCpuValue) {
		this.idleCpuValue = idleCpuValue;
	}

	public void setFreeMemory(long freeMemory) {
		this.freeMemory = freeMemory;
	}

	public long getTotalMemory() {
		return totalMemory;
	}

	public void setTotalMemory(long totalMemory) {
		this.totalMemory = totalMemory;
	}

	public BandWidth getBandWidth() {
		return bandWidth;
	}

	public void setBandWidth(BandWidth bandWidth) {
		this.bandWidth = bandWidth;
	}

	public String getCustomValues() {
		return customValues;
	}

	public void setCustomValues(String customValues) {
		this.customValues = customValues;
	}

	//新增get、set方法
	public double getLoad() {
		return load;
	}

	public void setLoad(double load) {
		this.load = load;
	}

	public float getCpuWait() {
		return cpuWait;
	}

	public void setCpuWait(float cpuWait) {
		this.cpuWait = cpuWait;
	}

	public long getRead() {
		return read;
	}

	public void setRead(long read) {
		this.read = read;
	}

	public long getWrite() {
		return write;
	}

	public void setWrite(long write) {
		this.write = write;
	}

	public float getDiskUtil() {
		return diskUtil;
	}

	public void setDiskUtil(float diskUtil) {
		this.diskUtil = diskUtil;
	}

	public double getMemUsedPercentage() {
		return memUsedPercentage;
	}

	public void setMemUsedPercentage(double memUsedPercentage) {
		this.memUsedPercentage = memUsedPercentage;
	}

	public long getIdleCpuValue() {
		return idleCpuValue;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	/**
	 * Get record string.
	 *
	 * @return record string
	 */
	public String toRecordString() {
		StringBuilder sb = new StringBuilder();
		sb.append(ip).append(",").append(system).append(",");
		sb.append(DateUtils.getCollectTimeInLong(new Date(getCollectTime()))).append(",").append(freeMemory).append(",");
		sb.append(totalMemory).append(",").append(cpuUsedPercentage);

		if (bandWidth != null) {
			sb.append(",").append(bandWidth.getReceivedPerSec()).append(",").append(bandWidth.getSentPerSec());
		}

		//add by lingj 新增cpuWait,memUsedPercentage,load,diskUtil的字段拼接
		sb.append(",").append(cpuWait).append(",").append(memUsedPercentage).append(",").append(load).append(",").append(diskUtil);

		if (customValues != null) {
			sb.append(",").append(customValues);
		}
		return sb.toString();
	}


	public static class NullSystemInfo extends SystemInfo {
		private static final NullSystemInfo instance = new NullSystemInfo();

		public static SystemInfo getNullSystemInfo() {
			return instance;
		}

		/**
		 * Return the empty record string.
		 *
		 * @return null filled record string.
		 * @see #toRecordString()
		 */
		@Override
		public String toRecordString() {
			StringBuilder sb = new StringBuilder();
			//ip、system
			sb.append("null").append(",").append("null").append(",");
			//collectTime,freeMemory
			sb.append("null").append(",").append("null").append(",");
			//totalMemory,cpuUsedPercentage
			sb.append("null").append(",").append("null");
			if (bandWidth != null) {
				sb.append(",").append("null").append(",").append("null");
			}
			//add by lingj 新增cpuWait,memUsedPercentage,load,diskUtil的字段拼接
			sb.append(",").append("null").append(",").append("null").append(",").append("null").append(",").append("null");

			if (customValues != null) {
				int valueCount = StringUtils.countMatches(customValues, ",") + 1;
				for (int i = 0; i < valueCount; i++) {
					sb.append(",").append("null");
				}
			}
			return sb.toString();
		}

		public boolean isParsed() {
			return false;
		}
	}

}
