package com.awbd.airport_manager.config;

import com.awbd.airport_manager.config.security.Auth0JwtConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                    CorsConfigurationSource corsConfigurationSource,
                                                    Auth0JwtConverter auth0JwtConverter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        // read-only: oricine poate vedea zboruri, gates, seats
                        .requestMatchers(HttpMethod.GET, "/flights/**", "/gates/**", "/seats/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/flights/search", "/gates/search", "/seats/search").permitAll()
                        // management avioane — ADMIN si STAFF
                        .requestMatchers("/aircrafts/**").hasAnyRole("ADMIN", "STAFF")
                        // management zboruri/gates/seats — ADMIN sau STAFF
                        .requestMatchers(HttpMethod.POST, "/flights/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/flights/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/flights/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/gates/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/gates/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/gates/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.POST, "/seats/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.PUT, "/seats/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(HttpMethod.DELETE, "/seats/**").hasAnyRole("ADMIN", "STAFF")
                        // contul propriu — orice user autentificat
                        .requestMatchers(HttpMethod.GET, "/accounts/me").authenticated()
                        // conturi — doar ADMIN
                        .requestMatchers("/accounts/**").hasRole("ADMIN")
                        // booking — orice utilizator autentificat
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(auth0JwtConverter))
                )
                .build();
    }
}
