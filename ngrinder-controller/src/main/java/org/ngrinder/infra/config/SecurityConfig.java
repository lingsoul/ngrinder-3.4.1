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
package org.ngrinder.infra.config;

import com.google.common.collect.Maps;
import org.ngrinder.common.constant.ControllerConstants;
import org.ngrinder.security.*;
import org.ngrinder.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.access.vote.UnanimousBased;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.ReflectionSaltSource;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.*;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.ELRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter{

	@Autowired
	private Config config;

	@Autowired
	UserSwitchPermissionVoter userSwitchPermissionVoter;

	@Autowired
	SvnHttpBasicEntryPoint svnHttpBasicEntryPoint;

	@Autowired
	NGrinderUserDetailsService ngrinderUserDetailsService;

	@Autowired
	NGrinderAuthenticationProvider nGrinderAuthenticationProvider;

	@Autowired
	private UserService userService;

	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	PluggablePreAuthFilter pluggablePreAuthFilter;

	/**
	 * Provide the appropriate shaPasswordEncoder depending on the ngrinder.security.sha256 config.
	 *
	 * @return {@link ShaPasswordEncoder} with 256 if ngrinder.security.sha256=true. Otherwise
	 *         returns default {@link ShaPasswordEncoder}
	 */
	@Bean(name = "shaPasswordEncoder")
	public ShaPasswordEncoder sharPasswordEncoder() {
		boolean useEnhancedEncoding = config.getControllerProperties().getPropertyBoolean(ControllerConstants.PROP_CONTROLLER_USER_PASSWORD_SHA256);
		return useEnhancedEncoding ? new ShaPasswordEncoder(256) : new ShaPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		ReflectionSaltSource reflectionSaltSource = new ReflectionSaltSource();
		reflectionSaltSource.setUserPropertyToUse("username");
		auth
			.authenticationProvider(ngrinderPreAuthProvider())
			.authenticationProvider(nGrinderAuthenticationProvider);
	}

	/**
	 * DefaultMethodSecurityExpressionHandler
	 * @return SecurityExpressionHandler
	 */
	@Bean
	public SecurityExpressionHandler<org.aopalliance.intercept.MethodInvocation> methodSecurityExpressionHandler() {
		DefaultMethodSecurityExpressionHandler defaultMethodSecurityExpressionHandler = new DefaultMethodSecurityExpressionHandler();
		GlobalMethodSecurityConfiguration globalMethodSecurityConfiguration = new GlobalMethodSecurityConfiguration();
		ArrayList<MethodSecurityExpressionHandler> handlers = new ArrayList<MethodSecurityExpressionHandler>();

		defaultMethodSecurityExpressionHandler.setDefaultRolePrefix("");
		handlers.set(0, defaultMethodSecurityExpressionHandler);
		globalMethodSecurityConfiguration.setMethodSecurityExpressionHandler(handlers);
		return defaultMethodSecurityExpressionHandler;
	}

	/**
	 * Generic Web
	 * @return AuthenticatedVoter
	 */
	@Bean
	public AuthenticatedVoter authenticatedVoter() {
		return new AuthenticatedVoter();
	}

	/**
	 * Svn AccessDecisionManager
	 * @return UnanimousBased
	 */
	@Bean
	public UnanimousBased svnAccessDecisionManager() {
		List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<AccessDecisionVoter<?>>();
		decisionVoters.add(authenticatedVoter());
		decisionVoters.add(userSwitchPermissionVoter);
		return new UnanimousBased(decisionVoters);
	}

	@Bean
	public AffirmativeBased accessDecisionManager() {
		List<AccessDecisionVoter<?>> decisionVoters = new ArrayList<AccessDecisionVoter<?>>();
		decisionVoters.add(authenticatedVoter());
		return new AffirmativeBased(decisionVoters);
	}

	@Bean
	public DefaultWebSecurityExpressionHandler webSecurityExpressionHandler() {
		DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
		defaultWebSecurityExpressionHandler.setDefaultRolePrefix("");
		return defaultWebSecurityExpressionHandler;
	}

	@Bean
	public RoleVoter roleVoter() {
		RoleVoter roleVoter = new RoleVoter();
		roleVoter.setRolePrefix("");
		return roleVoter;
	}

	@Bean
	public TokenBasedRememberMeServices rememberMeServices() {
		return new TokenBasedRememberMeServices("ngrinder", ngrinderUserDetailsService);
	}

	/**
	 * SuccessHandler
	 * @return SavedRequestAwareAuthenticationSuccessHandler
	 */
	@Bean
	public SavedRequestAwareAuthenticationSuccessHandler loginLogAuthenticationSuccessHandler() {
		SavedRequestAwareAuthenticationSuccessHandler savedRequestAwareAuthenticationSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
		savedRequestAwareAuthenticationSuccessHandler.setDefaultTargetUrl("/home");
		savedRequestAwareAuthenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(true);
		return savedRequestAwareAuthenticationSuccessHandler;
	}

	/**
	 * FailureHandler
	 * @return SimpleUrlAuthenticationFailureHandler
	 */
	@Bean
	public SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler() {
		SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
		simpleUrlAuthenticationFailureHandler.setDefaultFailureUrl("/login");
		return simpleUrlAuthenticationFailureHandler;
	}

	@Bean
	public UserDetailsByNameServiceWrapper<org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken> userDetailsServiceWrapper() {
		UserDetailsByNameServiceWrapper<org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken> userDetailsByNameServiceWrapper = new UserDetailsByNameServiceWrapper<org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken>();
		userDetailsByNameServiceWrapper.setUserDetailsService(ngrinderUserDetailsService);
		return userDetailsByNameServiceWrapper;
	}

	@Bean
	public NGrinderAuthenticationPreAuthProvider ngrinderPreAuthProvider() {
		NGrinderAuthenticationPreAuthProvider nGrinderAuthenticationPreAuthProvider = new NGrinderAuthenticationPreAuthProvider();
		UserDetailsByNameServiceWrapper<org.springframework.security.core.Authentication> userDetailsByNameServiceWrapper = new UserDetailsByNameServiceWrapper<org.springframework.security.core.Authentication>();
		userDetailsByNameServiceWrapper.setUserDetailsService(ngrinderUserDetailsService);
		nGrinderAuthenticationPreAuthProvider.setPreAuthenticatedUserDetailsService(userDetailsServiceWrapper());
		nGrinderAuthenticationPreAuthProvider.setUserService(userService);
		return nGrinderAuthenticationPreAuthProvider;
	}

	@Bean
	public BasicAuthenticationEntryPoint httpBasicAuthenticationEntryPoint() {
		BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
		basicAuthenticationEntryPoint.setRealmName("ngrinder");
		return basicAuthenticationEntryPoint;
	}

	@Bean
	public LoginUrlAuthenticationEntryPoint authenticationProcessingFilterEntryPoint() {
		return new LoginUrlAuthenticationEntryPoint("/login");
	}

	@Bean
	public BasicAuthenticationFilter basicProcessingFilter() {
		return new BasicAuthenticationFilter(authenticationManager, httpBasicAuthenticationEntryPoint());
	}

	/**
	 * custom-filter extends UsernamePasswordAuthenticationFilter
	 * @return NgrinderUsernamePasswordAuthenticationFilter
	 */
	@Bean
	public NgrinderUsernamePasswordAuthenticationFilter ngrinderUserPasswordAuthenticationFilter() {
		NgrinderUsernamePasswordAuthenticationFilter ngrinderUsernamePasswordAuthenticationFilter = new NgrinderUsernamePasswordAuthenticationFilter();
		ngrinderUsernamePasswordAuthenticationFilter.setRememberMeServices(rememberMeServices());
		ngrinderUsernamePasswordAuthenticationFilter.setFilterProcessesUrl("/form_login");
		ngrinderUsernamePasswordAuthenticationFilter.setAuthenticationSuccessHandler(loginLogAuthenticationSuccessHandler());
		ngrinderUsernamePasswordAuthenticationFilter.setAuthenticationFailureHandler(simpleUrlAuthenticationFailureHandler());
		ngrinderUsernamePasswordAuthenticationFilter.setUsernameParameter("j_username");
		ngrinderUsernamePasswordAuthenticationFilter.setPasswordParameter("j_password");
		ngrinderUsernamePasswordAuthenticationFilter.setAuthenticationManager(authenticationManager);
		return ngrinderUsernamePasswordAuthenticationFilter;
	}

	@Bean
	public DelegatingAuthenticationEntryPoint delegatingAuthenticationEntryPoint() {
		LinkedHashMap<RequestMatcher, AuthenticationEntryPoint> matchers = Maps.newLinkedHashMap();
		matchers.put(new ELRequestMatcher("hasHeader('WWW-Authenticate','Basic')"), httpBasicAuthenticationEntryPoint());
		DelegatingAuthenticationEntryPoint entryPoint = new DelegatingAuthenticationEntryPoint(matchers);
		entryPoint.setDefaultEntryPoint(authenticationProcessingFilterEntryPoint());
		return entryPoint;
	}

	/**
	 * configure static resource and login page
	 * @param web WebSecurity
	 * @throws Exception
	 */
	@Override
	public void configure(WebSecurity web) throws Exception {
		web
			.ignoring()
			.antMatchers(
				"/css/**",
				"/img/**",
				"/js/**",
				"/plugins/**",
				"/login"
			);
	}

	/**
	 * configure SVN, used for shared test report page and accessible from no authorized users
	 * and delegatingAuthenticationEntryPoint
	 * @param http HttpSecurity
	 * @throws Exception
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		//SVN
		http
			.authorizeRequests()
			.antMatchers("/svn/**")
			.hasAnyRole("A", "S", "U")
			.accessDecisionManager(svnAccessDecisionManager())
			.and()
			.httpBasic().authenticationEntryPoint(svnHttpBasicEntryPoint)
			.realmName("svn")
			.and()
			.csrf().disable();

		http
			.authorizeRequests()
			.antMatchers(
				"/perftest/**/report",
				"/perftest/**/monitor",
				"/perftest/**/graph",
				"/check/**",
				"/agent/download/**",
				"/monitor/download/**",
				"/jnlp/**",
				"/sign_up/**")
			.permitAll()
			.anyRequest().authenticated()
			.and()
			.csrf().disable();

		http
			.authorizeRequests()
			.antMatchers(
				"/",
				"/home",
				"/perftest/**",
				"/help**",
				"/script**",
				"/monitor-properties.map",
				"/agent/**",
				"/user/**")
			.authenticated()
			.accessDecisionManager(accessDecisionManager())
			.and()
			.addFilterAt(basicProcessingFilter(), BasicAuthenticationFilter.class)
			.addFilterAt(ngrinderUserPasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
			.addFilterAt(pluggablePreAuthFilter, AbstractPreAuthenticatedProcessingFilter.class)
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("logout"))
				.logoutSuccessUrl("/")
				.deleteCookies("JSESSIONID","switchUser")
				.invalidateHttpSession(true)
			.and()
			.sessionManagement()
				.invalidSessionUrl("/login")
				.sessionFixation().newSession()
			.and()
			.rememberMe()
				.key("ngrinder")
				.userDetailsService(ngrinderUserDetailsService)
			.and()
			.csrf().disable();
	}

}
