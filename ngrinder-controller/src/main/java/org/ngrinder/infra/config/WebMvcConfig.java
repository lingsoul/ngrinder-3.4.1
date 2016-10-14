package org.ngrinder.infra.config;

import com.beust.jcommander.internal.Maps;
import org.ngrinder.infra.spring.ApiExceptionHandlerResolver;
import org.ngrinder.infra.spring.RemainedPathMethodArgumentResolver;
import org.ngrinder.infra.spring.UserHandlerMethodArgumentResolver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {

	@Value("${ngrinder.version}")
	private String ngrinderVersion;

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
		argumentResolvers.add(userHandlerMethodArgumentResolver());
		argumentResolvers.add(remainedPathMethodArgumentResolver());
		argumentResolvers.add(pageableHandlerMethodArgumentResolver());
		super.addArgumentResolvers(argumentResolvers);
	}

	@Bean
	public PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver() {
		PageableHandlerMethodArgumentResolver pageableHandlerMethodArgumentResolver = new PageableHandlerMethodArgumentResolver();
		pageableHandlerMethodArgumentResolver.setPrefix("page.");
		return pageableHandlerMethodArgumentResolver;
	}

	@Bean
	public RemainedPathMethodArgumentResolver remainedPathMethodArgumentResolver() {
		return new RemainedPathMethodArgumentResolver();
	}

	@Bean
	public UserHandlerMethodArgumentResolver userHandlerMethodArgumentResolver() {
		return new UserHandlerMethodArgumentResolver();
	}

	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		exceptionResolvers.add(apiExceptionHandlerResolver());
		exceptionResolvers.add(exceptionHandlerExceptionResolver());
	}

	@Bean
	public ApiExceptionHandlerResolver apiExceptionHandlerResolver() {
		ApiExceptionHandlerResolver apiExceptionHandlerResolver = new ApiExceptionHandlerResolver();
		apiExceptionHandlerResolver.setOrder(-1);
		return apiExceptionHandlerResolver;
	}

	@Bean
	public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
		ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver = new ExceptionHandlerExceptionResolver();
		exceptionHandlerExceptionResolver.setOrder(1);
		return exceptionHandlerExceptionResolver;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(localeChangeInterceptor());
	}

	@Bean
	public LocaleChangeInterceptor localeChangeInterceptor() {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		return localeChangeInterceptor;
	}

	@Bean
	public CookieLocaleResolver localeResolver() {
		CookieLocaleResolver localeResolver = new CookieLocaleResolver();
		Locale defaultLocale = new Locale("en");
		localeResolver.setDefaultLocale(defaultLocale);
		localeResolver.setCookieName("ngrinder_lang");
		return localeResolver;
	}

	@Bean
	public MultipartResolver multipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setMaxUploadSize(50000000);
		commonsMultipartResolver.setDefaultEncoding("utf-8");
		return commonsMultipartResolver;
	}

	@Bean
	public FreeMarkerConfigurer freeMarkerConfigurer() {
		FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
		Map<String, Object> map = Maps.newHashMap();
		map.put("nGrinderVersion", ngrinderVersion);

		configurer.setTemplateLoaderPath("/WEB-INF/ftl/");
		configurer.setDefaultEncoding("UTF-8");
		configurer.setFreemarkerVariables(map);
		return configurer;
	}

}
