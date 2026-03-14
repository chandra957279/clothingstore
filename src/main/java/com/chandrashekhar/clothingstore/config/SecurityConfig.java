package com.chandrashekhar.clothingstore.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	 @Bean
	    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

	        http
	            .csrf(csrf -> csrf.disable())
	            .authorizeHttpRequests(auth -> auth
	                .requestMatchers(
	                        "/",
	                        "/signup",
	                        "/login",
	                        "/forgot-password",
	                        "/reset-password/**",
	                        "/css/**",
	                        "/js/**",
	                        "/images/**",
	                        "/payment/**",
	                        "/create-order",
	                        "/payment/process",
	                        "/payment/cod/**",
	                        "/payment/online/**"
	                ).permitAll()
	                .requestMatchers("/admin/**").hasRole("ADMIN")
	                .anyRequest().authenticated()
	            )
	            .formLogin(login -> login
	                .loginPage("/login")
	                .defaultSuccessUrl("/")
	                .permitAll()
	            )
	            .logout(logout -> logout.permitAll());

	        return http.build();
	    }
	
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	  @Bean
	    public AuthenticationManager authenticationManager(
	            AuthenticationConfiguration config) throws Exception {
	        return config.getAuthenticationManager();
	    }
	 
}
