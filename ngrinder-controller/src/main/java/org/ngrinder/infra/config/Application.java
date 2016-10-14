package org.ngrinder.infra.config;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import net.sf.ehcache.constructs.web.ShutdownListener;
import org.ngrinder.infra.spring.Redirect404DispatcherServlet;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.orm.jpa.support.OpenEntityManagerInViewFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.HttpRequestHandlerServlet;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.filter.HttpPutFormContentFilter;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@ImportResource(value = {"classpath:applicationContext.xml"})
public class Application extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void onStartup(ServletContext servletContext)
		throws ServletException {
		servletContext.setInitParameter(
			"spring.profiles.default", "production");
		servletContext.addListener(new ContextLoaderListener());
		servletContext.addListener(new HttpSessionEventPublisher());
		servletContext.addListener(new ShutdownListener());
		super.onStartup(servletContext);
	}

	@Bean
	public ServletRegistrationBean appServlet() {
		ServletRegistrationBean redirect404DispatcherServlet = new ServletRegistrationBean(new Redirect404DispatcherServlet(), "/*");
		redirect404DispatcherServlet.setLoadOnStartup(1);
		redirect404DispatcherServlet.setName("appServlert");
		return redirect404DispatcherServlet;
	}

	@Bean
	public ServletRegistrationBean svnDavServlet() {
		ServletRegistrationBean SvnDavServletRegistrationBean = new ServletRegistrationBean(new HttpRequestHandlerServlet(), "/svn/*");
		SvnDavServletRegistrationBean.setLoadOnStartup(1);
		SvnDavServletRegistrationBean.setName("svnDavServlet");
		return SvnDavServletRegistrationBean;
	}

	@Bean
	public FilterRegistrationBean filterRegistrationBean() {
		FilterRegistrationBean registrationBean = new FilterRegistrationBean();
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		registrationBean.setFilter(characterEncodingFilter);
		return registrationBean;
	}

	@Bean
	public FilterRegistrationBean xssEscapeServletFilter() {
		FilterRegistrationBean xssEscapeServletFilter = new FilterRegistrationBean(new XssEscapeServletFilter());
		String[] urlMappings = {"/login/*", "/perftest/*", "/user/*", "/script/*"};
		xssEscapeServletFilter.addUrlPatterns(urlMappings);
		return xssEscapeServletFilter;
	}

	@Bean
	public FilterRegistrationBean httpPutFormContentFilter() {
		FilterRegistrationBean httpPutFormContentFilter = new FilterRegistrationBean(new HttpPutFormContentFilter());
		httpPutFormContentFilter.addUrlPatterns("/*");
		return httpPutFormContentFilter;
	}

	@Bean
	public FilterRegistrationBean springOpenEntityManagerInViewFilter() {
		FilterRegistrationBean springOpenEntityManagerInViewFilter = new FilterRegistrationBean(new OpenEntityManagerInViewFilter());
		springOpenEntityManagerInViewFilter.addUrlPatterns("/*");
		return springOpenEntityManagerInViewFilter;
	}

	@Bean
	public FilterRegistrationBean hiddenHttpMethodFilter() {
		FilterRegistrationBean hiddenHttpMethodFilter = new FilterRegistrationBean(new HiddenHttpMethodFilter());
		hiddenHttpMethodFilter.addUrlPatterns("/*");
		return hiddenHttpMethodFilter;
	}

	@Bean
	public FilterRegistrationBean pluggableServletFilter() {
		FilterRegistrationBean pluggableServletFilter = new FilterRegistrationBean(new DelegatingFilterProxy());
		pluggableServletFilter.addUrlPatterns("/*");
		return pluggableServletFilter;
	}

	@Bean
	public FilterRegistrationBean springSecurityFilterChain() {
		FilterRegistrationBean springSecurityFilterChain = new FilterRegistrationBean(new DelegatingFilterProxy());
		springSecurityFilterChain.addUrlPatterns("/*");
		return springSecurityFilterChain;
	}

	@Bean
	public EmbeddedServletContainerFactory servletContainer() {
		TomcatEmbeddedServletContainerFactory factory = new TomcatEmbeddedServletContainerFactory();
		factory.setSessionTimeout(10, TimeUnit.MINUTES);
		return factory;
	}

}
