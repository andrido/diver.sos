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
                .authorizeHttpRequests(auth -> auth

                        // --- 1. ROTAS PÚBLICAS (Qualquer um acessa) ---
                        .requestMatchers(HttpMethod.GET, "/imagens/**").permitAll() // Fotos de perfil/banners
                        .requestMatchers("/auth/login").permitAll()                 // Login
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()  // Cadastro (Registro)
                        .requestMatchers(HttpMethod.GET, "/auth/confirmar").permitAll() // Confirmação de E-mail
                        .requestMatchers("/error").permitAll()
                        // Leitura de dados públicos
                        .requestMatchers(HttpMethod.GET, "/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/habilidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/grupos/**").permitAll() // Caso queira listar grupos publicamente

                        // --- 2. ROTAS DO USUÁRIO LOGADO (Qualquer perfil) ---
                        .requestMatchers("/usuarios/me/**").authenticated()

                        // --- 3. REGRAS DE ADMIN / MODERADOR (Escrita/Deleção) ---

                        // Vagas (Criar, Editar, Deletar)
                        .requestMatchers(HttpMethod.POST, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.PUT, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Notícias
                        .requestMatchers("/noticias/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Grupos (Criar, Editar, Deletar)
                        .requestMatchers(HttpMethod.POST, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.PUT, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Habilidades
                        .requestMatchers(HttpMethod.POST, "/habilidades/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/habilidades/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Gestão de Usuários (Ex: Listar todos, banir alguém pelo ID)
                        .requestMatchers(HttpMethod.GET, "/usuarios").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // --- 4. BLOQUEIO PADRÃO (Tudo que não foi listado acima exige login) ---
                        .anyRequest().authenticated()
                )
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