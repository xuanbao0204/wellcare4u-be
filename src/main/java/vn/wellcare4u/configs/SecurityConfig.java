package vn.wellcare4u.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.utils.JwtUtil;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {

    private final CorsConfigurationSource corsConfigurationSource;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthenticationFilter jwtFilter() {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

    	http.csrf(csrf -> csrf.disable())
    			.cors(cors -> cors.configurationSource(corsConfigurationSource))
    			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        
                        .requestMatchers("/api/v1/account/**").hasAnyRole("ADMIN", "PATIENT", "DOCTOR")
                        .requestMatchers("/api/v1/user/**").hasAnyRole("ADMIN", "PATIENT", "DOCTOR")
                        
                        .requestMatchers("/api/v1/medical-records/**").hasAnyRole("PATIENT", "DOCTOR")
                        
                        .requestMatchers("/api/v1/doctor/schedules/**").hasAnyRole("PATIENT", "DOCTOR")
                        
                        .requestMatchers("/api/v1/list/doctors/**").permitAll()

                        .requestMatchers("/ws/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}