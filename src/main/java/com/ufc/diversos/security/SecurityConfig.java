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

                        // --- 1. ROTAS TOTALMENTE PÚBLICAS (Visitante) ---
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()

                        .requestMatchers(HttpMethod.GET, "/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/habilidades/**").permitAll()

                        // --- 2. REGRAS DE ADMIN / MODERADOR (Escrita Sensível) ---

                        // Vagas
                        .requestMatchers(HttpMethod.POST, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.PUT, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Notícias
                        .requestMatchers("/noticias/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Grupos (Edição/Criação/Deleção restritas)
                        .requestMatchers(HttpMethod.POST, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.PUT, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Habilidades (Criar novas techs)
                        .requestMatchers(HttpMethod.POST, "/habilidades/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Gestão de Usuários
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // --- 3. BLOQUEIO PADRÃO ---
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permite o seu Frontend (Vite)
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000")); // Adicionei a porta 3000 por garantia

        // Permite os métodos HTTP necessários
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));

        // Permite Headers (Authorization é essencial pro Token JWT passar)
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));

        // Permite credenciais
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}