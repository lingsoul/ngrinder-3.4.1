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
package org.ngrinder.common.util;

import org.junit.Test;

/**
 * Class description.
 * 
 * @author Mavlarn
 * @since
 */
public class ThreadUtilTest {

	/**
	 * Test method for {@link org.ngrinder.common.util.ThreadUtil#sleep(long)}.
	 */
	@Test
	public void testSleep() {
		ThreadUtil.sleep(1000);
	}

	/**
	 * Test method for
	 * {@link org.ngrinder.common.util.ThreadUtil#stopQuietly(java.lang.Thread, java.lang.String)}.
	 */
	@Test
	public void testStopQuetly() {
		Thread newThread = new Thread(new Runnable() {

			@Override
			public void run() {
				int i = 10;
				while (i > 0) {
					ThreadUtil.sleep(200);
					System.out.println("Running...");
				}

			}
		});
		newThread.start();
		ThreadUtil.sleep(500);
		ThreadUtil.stopQuietly(newThread, "STOPPED!");
	}

}