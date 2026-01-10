package com.ufc.diversos.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // --- 1. ROTAS TOTALMENTE PÚBLICAS ---
                        .requestMatchers(HttpMethod.GET, "/imagens/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/habilidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/confirmar").permitAll()
                        // --- 2. ROTAS DO USUÁRIO LOGADO (MEU PERFIL) ---
                        .requestMatchers("/usuarios/me/**").authenticated()

                        // --- 3. REGRAS DE ADMIN / MODERADOR ---

                        // Vagas
                        .requestMatchers(HttpMethod.POST, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.PUT, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Notícias
                        .requestMatchers("/noticias/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Grupos
                        .requestMatchers(HttpMethod.POST, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.PUT, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Habilidades
                        .requestMatchers(HttpMethod.POST, "/habilidades/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Gestão de Usuários (Genérico)
                        // Como a rota /usuarios/me já foi tratada lá em cima, o que sobrar aqui (ex: /usuarios/5)
                        // cai nesta regra de Admin.
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // --- 4. BLOQUEIO PADRÃO ---
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}