package com.example.scaffold.security;

import com.example.scaffold.services.AdminUserDetailsService;
import com.example.scaffold.services.AppUserDetailsService;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
public class SecurityConfig {

	@Configuration
	@Order(1)
	public static class AdminSecurityConfig extends WebSecurityConfigurerAdapter {

		private final AdminUserDetailsService adminUserDetailsService;
		private final AppUserDetailsService appUserDetailsService;
		private final PasswordEncoder passwordEncoder;
		private final UnauthorizedEntryPoint unauthorizedEntryPoint;
		private final AdminJWTTokenFilter adminJWTTokenFilter;

		public AdminSecurityConfig(AdminUserDetailsService adminUserDetailsService,
								   AppUserDetailsService appUserDetailsService,
								   UnauthorizedEntryPoint unauthorizedEntryPoint,
								   AdminJWTTokenFilter adminJWTTokenFilter,
								   PasswordEncoder passwordEncoder) {
			this.adminUserDetailsService = adminUserDetailsService;
			this.appUserDetailsService = appUserDetailsService;
			this.passwordEncoder = passwordEncoder;
			this.adminJWTTokenFilter = adminJWTTokenFilter;
			this.unauthorizedEntryPoint = unauthorizedEntryPoint;
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			DaoAuthenticationProvider adminAuthenticationProvider = new DaoAuthenticationProvider();
			adminAuthenticationProvider.setUserDetailsService(adminUserDetailsService);
			adminAuthenticationProvider.setPasswordEncoder(passwordEncoder);

			auth.authenticationProvider(adminAuthenticationProvider);

			DaoAuthenticationProvider appAuthenticationProvider = new DaoAuthenticationProvider();
			appAuthenticationProvider.setUserDetailsService(appUserDetailsService);
			appAuthenticationProvider.setPasswordEncoder(passwordEncoder);

			auth.authenticationProvider(appAuthenticationProvider);
		}

		@Bean
		@Override
		public AuthenticationManager authenticationManagerBean() throws Exception {
			return super.authenticationManagerBean();
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
					.antMatchers(HttpMethod.POST, "/admin/authentication").permitAll()
					.antMatchers(HttpMethod.POST, "/admin/users/init").permitAll()
					.anyRequest().authenticated();

			http.addFilterBefore(adminJWTTokenFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}

	@Configuration
	public static class AppSecurityConfig extends WebSecurityConfigurerAdapter {
		private final UnauthorizedEntryPoint unauthorizedEntryPoint;
		private final AppJWTTokenFilter appJWTTokenFilter;

		public AppSecurityConfig(UnauthorizedEntryPoint unauthorizedEntryPoint,
								 AppJWTTokenFilter appJWTTokenFilter) {
			this.unauthorizedEntryPoint = unauthorizedEntryPoint;
			this.appJWTTokenFilter = appJWTTokenFilter;
		}

		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
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
					.antMatchers(HttpMethod.POST, "/app/authentication").permitAll()
					.antMatchers(HttpMethod.POST, "/app/users").permitAll()
					.anyRequest().authenticated();

			http.addFilterBefore(appJWTTokenFilter, UsernamePasswordAuthenticationFilter.class);
		}
	}

}
