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
                // Ativa o CORS com a configuração definida no método abaixo
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // Desativa CSRF (padrão para APIs Stateless/JWT)
                .csrf(csrf -> csrf.disable())
                // Define que não haverá sessão no servidor (Stateless)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Regras de acesso (Quem pode acessar o quê)
                // ... (código anterior igual)

// Regras de acesso (Quem pode acessar o quê)
                .authorizeHttpRequests(auth -> auth

                        // --- 1. ROTAS PÚBLICAS ---
                        .requestMatchers(HttpMethod.GET, "/imagens/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                        .requestMatchers(HttpMethod.GET, "/auth/confirmar").permitAll()
                        .requestMatchers("/auth/esqueci-senha").permitAll()
                        .requestMatchers("/auth/nova-senha").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Leitura pública (Qualquer um, inclusive RH, pode VER notícias e grupos, mas não editar)
                        .requestMatchers(HttpMethod.GET, "/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/habilidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/grupos/**").permitAll()

                        // --- 2. ROTAS DO USUÁRIO LOGADO ---
                        .requestMatchers("/usuarios/me/**").authenticated()

                        // --- 3. REGRAS DO RH (Vagas específicas) ---
                        // RH pode Criar e Editar vagas, mas não pode Deletar (opcional, conforme sua regra)
                        .requestMatchers(HttpMethod.POST, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR", "RH")
                        .requestMatchers(HttpMethod.PUT, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR", "RH")
                        // Deletar vaga costuma ser apenas ADMIN/MODERADOR
                        .requestMatchers(HttpMethod.DELETE, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // --- 4. BLOQUEIOS PARA O RH (Recursos restritos a ADMIN/MODERADOR) ---

                        // Notícias: RH não pode Criar, Editar ou Deletar (POST, PUT, DELETE)
                        .requestMatchers("/noticias/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Grupos: RH não tem acesso à gestão
                        .requestMatchers("/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Habilidades: Gestão apenas para superiores
                        .requestMatchers("/habilidades/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Gestão de Usuários: RH não pode listar nem gerenciar outros usuários
                        .requestMatchers("/usuarios/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // --- 5. BLOQUEIO PADRÃO ---
                        .anyRequest().authenticated()
                )
//
                // Adiciona o nosso filtro de Token antes do filtro padrão do Spring
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // --- CONFIGURAÇÃO DE ORIGENS (Onde o Front mora) ---
        // Usamos addAllowedOriginPattern para aceitar subdomínios e evitar erro de barra no final

        configuration.addAllowedOriginPattern("http://localhost:*");        // Aceita 3000, 5173, etc.
        configuration.addAllowedOriginPattern("https://*.onrender.com");    // Aceita seu front no Render
        configuration.addAllowedOriginPattern("https://*.railway.app");     // Segurança extra

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true); // Permite cookies/credentials se necessário

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}