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

                // --- REGRAS DE ACESSO ---
                .authorizeHttpRequests(auth -> auth

                        // 1. ROTAS PÚBLICAS (Qualquer um acessa)
                        .requestMatchers(HttpMethod.GET, "/imagens/**").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/usuarios").permitAll() // Cadastro
                        .requestMatchers(HttpMethod.GET, "/auth/confirmar").permitAll()
                        .requestMatchers("/auth/esqueci-senha").permitAll()
                        .requestMatchers("/auth/nova-senha").permitAll()
                        .requestMatchers("/error").permitAll()

                        // Leitura pública de conteúdos (Qualquer um vê, mas não edita)
                        .requestMatchers(HttpMethod.GET, "/vagas/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/noticias/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/habilidades/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/grupos/**").permitAll()

                        // 2. ROTAS DO PRÓPRIO USUÁRIO LOGADO
                        .requestMatchers("/usuarios/me/**").authenticated()

                        // --- CORREÇÃO AQUI ---
                        // Permite que qualquer usuário logado TENTE atualizar ou ver um perfil pelo ID.
                        // A segurança (se o ID é dele mesmo) será feita pelo UsuarioService.
                        .requestMatchers(HttpMethod.PUT, "/usuarios/{id}").authenticated()
                        .requestMatchers(HttpMethod.GET, "/usuarios/{id}").authenticated()
                        // ---------------------

                        // 3. REGRAS DO RH (Vagas específicas)
                        // RH pode Criar e Editar vagas
                        .requestMatchers(HttpMethod.POST, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR", "RH")
                        .requestMatchers(HttpMethod.PUT, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR", "RH")
                        // Deletar vaga apenas ADMIN/MODERADOR
                        .requestMatchers(HttpMethod.DELETE, "/vagas/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // 4. BLOQUEIOS RESTRITOS (Apenas ADMIN e MODERADOR)

                        // Notícias
                        .requestMatchers("/noticias/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        // Grupos
                        .requestMatchers("/grupos/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")
                        // Habilidades
                        .requestMatchers("/habilidades/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // Gestão Geral de Usuários (Listar todos, Deletar outros)
                        // Como colocamos a regra do PUT {id} lá em cima, essa aqui só pega o resto (Delete, ListAll)
                        .requestMatchers("/usuarios/**").hasAnyRole("ADMINISTRADOR", "MODERADOR")

                        // 5. BLOQUEIO PADRÃO (Qualquer outra coisa precisa estar logado)
                        .anyRequest().authenticated()
                )

                // Adiciona o filtro de Token
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("http://localhost:*");
        configuration.addAllowedOriginPattern("https://*.onrender.com");
        configuration.addAllowedOriginPattern("https://*.railway.app");

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}