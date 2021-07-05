package me.dodowhat.example.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static me.dodowhat.example.config.security.SwaggerConstants.*;

@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public static PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Configuration
	@Order(1)
	public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

		private final AdministratorUserDetailsService administratorUserDetailsService;
		private final AppUserDetailsService appUserDetailsService;
		private final PasswordEncoder passwordEncoder;
		private final UnauthorizedEntryPoint unauthorizedEntryPoint;
		private final AdminTokenFilter adminTokenFilter;

		public AdminSecurityConfig(AdministratorUserDetailsService administratorUserDetailsService,
								   AppUserDetailsService appUserDetailsService,
								   UnauthorizedEntryPoint unauthorizedEntryPoint,
								   AdminTokenFilter adminTokenFilter,
								   PasswordEncoder passwordEncoder) {
			this.administratorUserDetailsService = administratorUserDetailsService;
			this.appUserDetailsService = appUserDetailsService;
			this.passwordEncoder = passwordEncoder;
			this.adminTokenFilter = adminTokenFilter;
			this.unauthorizedEntryPoint = unauthorizedEntryPoint;
		}

		@Bean
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) {
			DaoAuthenticationProvider adminAuthenticationProvider = new DaoAuthenticationProvider();
			adminAuthenticationProvider.setUserDetailsService(administratorUserDetailsService);
			adminAuthenticationProvider.setPasswordEncoder(passwordEncoder);
			auth.authenticationProvider(adminAuthenticationProvider);

			DaoAuthenticationProvider appAuthenticationProvider = new DaoAuthenticationProvider();
			appAuthenticationProvider.setUserDetailsService(appUserDetailsService);
			appAuthenticationProvider.setPasswordEncoder(passwordEncoder);
			auth.authenticationProvider(appAuthenticationProvider);
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http = http.cors().and().csrf().disable();

			http = http
					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and();

			http = http
					.exceptionHandling()
					.authenticationEntryPoint(unauthorizedEntryPoint)
					.and();

			http.antMatcher("/admin/**")
					.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/admin/auth").permitAll()
					.antMatchers(HttpMethod.PUT, "/admin/auth").permitAll()
					.antMatchers("/admin/**").authenticated()
					.antMatchers("/admin/**").access("@RbacPermissionEvaluator.check(authentication, request)");

			http.addFilterBefore(adminTokenFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}

	@Configuration
	@Order(2)
	public static class SwaggerSecurityConfig extends WebSecurityConfigurerAdapter {
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			String password = GetSwaggerPassword();
			auth.inMemoryAuthentication()
					.withUser(SWAGGER_USERNAME)
					.password(passwordEncoder().encode(password))
					.roles("USER");
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/docs/**")
					.authorizeRequests()
					.anyRequest().authenticated()
					.and()
					.httpBasic();
		}
	}

	@Configuration
	public static class AppSecurityConfig extends WebSecurityConfigurerAdapter {
		private final UnauthorizedEntryPoint unauthorizedEntryPoint;
		private final AppTokenFilter appTokenFilter;

		public AppSecurityConfig(UnauthorizedEntryPoint unauthorizedEntryPoint,
								 AppTokenFilter appTokenFilter) {
			this.unauthorizedEntryPoint = unauthorizedEntryPoint;
			this.appTokenFilter = appTokenFilter;
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) {
		}

		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http = http.cors().and().csrf().disable();

			http = http
					.sessionManagement()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
					.and();

			http = http
					.exceptionHandling()
					.authenticationEntryPoint(unauthorizedEntryPoint)
					.and();

			http.authorizeRequests()
					.antMatchers(HttpMethod.POST, "/auth").permitAll()
					.anyRequest().authenticated();

			http.addFilterBefore(appTokenFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}

}
