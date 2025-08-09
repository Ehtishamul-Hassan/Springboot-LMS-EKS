package com.java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutHandler;

import com.java.security.JwtAuthenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig {
	
	private static final String[] WHITE_LIST_URL = {"/api/auth/**",
			"/templates/active_account.html",
			"/api/admin/**",
            "/v2/api-docs",
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui/**",
            "/webjars/**",
            "/swagger-ui.html"};

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;
    private final LogoutHandler logoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(request ->
                request
                    .requestMatchers(WHITE_LIST_URL).permitAll()
                    
//                    .requestMatchers("/api/portfolio/admin/**").hasRole(Role.ADMIN.name())
//                    .requestMatchers(HttpMethod.GET, "/api/portfolio/admin/**").hasAuthority(Permission.ADMIN_READ.name())
//                    .requestMatchers(HttpMethod.POST, "/api/portfolio/admin/**").hasAuthority(Permission.ADMIN_CREATE.name())
//                    .requestMatchers(HttpMethod.PUT, "/api/portfolio/admin/**").hasAuthority(Permission.ADMIN_UPDATE.name())
//                    .requestMatchers(HttpMethod.DELETE, "/api/portfolio/admin/**").hasAuthority(Permission.ADMIN_DELETE.name())
                    
                    
                    
                    
                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
//            .logout(logout ->
//             logout.logoutUrl("/api/auth/logout")
//                    .addLogoutHandler(logoutHandler)
//                    .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())
//    )
            .build();
    }
}
