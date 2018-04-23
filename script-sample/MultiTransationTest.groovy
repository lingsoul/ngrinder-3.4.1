package script_sample
import static net.grinder.script.Grinder.grinder
import static org.junit.Assert.*
import static org.hamcrest.Matchers.*
import net.grinder.plugin.http.HTTPRequest
import net.grinder.plugin.http.HTTPPluginControl
import net.grinder.script.GTest
import net.grinder.script.Grinder
import net.grinder.scriptengine.groovy.junit.GrinderRunner
import net.grinder.scriptengine.groovy.junit.annotation.BeforeProcess
import net.grinder.scriptengine.groovy.junit.annotation.BeforeThread
import net.grinder.scriptengine.groovy.junit.annotation.Repeat
import net.grinder.scriptengine.groovy.junit.annotation.RunRate
import org.json.JSONObject
import org.junit.Before
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith
import ch.qos.logback.classic.Level
import org.slf4j.LoggerFactory
import static net.grinder.util.GrinderUtils.* // You can use this if you're using nGrinder after 3.2.3
import java.util.Date
import java.util.List
import java.util.ArrayList

import HTTPClient.Cookie
import HTTPClient.CookieModule
import HTTPClient.HTTPResponse
import HTTPClient.NVPair

/**
 * 多事务测试示例，对每个测试方法可采取自定义的权重（交易占比）进行分配； *
 * @author lingj
 */
//@Repeat(10)
@RunWith(GrinderRunner)
class MultiTransationTest {
	
	public static GTest test1
	public static HTTPRequest request
	public static GTest test2
	public static JSONObject obj
	public static NVPair[] headers = []
	public static NVPair[] params = []
	public static Cookie[] cookies = []

	def URL1 = "http://127.0.0.1/xx/1/"
	def URL2 = "http://127.0.0.1/xx/2/"
	@BeforeProcess
	public static void beforeProcess() {
		HTTPPluginControl.getConnectionDefaults().timeout = 120000
		test1 = new GTest(1, "测试1")
		request = new HTTPRequest()
		test2 = new GTest(2, "测试2")
		// Set header datas
		List<NVPair> headerList = new ArrayList<NVPair>()
		headerList.add(new NVPair("Content-Type", "application/x-www-form-urlencoded"))
		headers = headerList.toArray()
		grinder.logger.info("before process.");
	}
	
	@BeforeThread
	public void beforeThread() {
		//只输出错误的日志
		LoggerFactory.getLogger("worker").setLevel(Level.ERROR)
		test1.record(this,"doTransaction1")
		test2.record(this,"doTransaction2")
		grinder.statistics.delayReports=true;
		grinder.logger.info("before thread.");
	}
	
	@Before
	public void before() {
		request.setHeaders(headers)
		cookies.each { CookieModule.addCookie(it, HTTPPluginControl.getThreadHTTPClientContext()) }
		grinder.logger.info("before thread. init headers and cookies");
	}
	
	@RunRate(10)
	@Test
	public void doTransaction1(){
		HTTPResponse result = request.GET(URL1)
	}

	@RunRate(90)
	@Test
	public void doTransaction2(){
		HTTPResponse result = request.GET(URL2)
	}


}
