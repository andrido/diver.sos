package com.ufc.diversos.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthFilter(JwtTokenProvider tokenProvider, CustomUserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String header = request.getHeader("Authorization");

        // Verifica se tem cabeçalho de token
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                // 1. Valida a assinatura do token
                if (tokenProvider.tokenValido(token)) {
                    String username = tokenProvider.pegarUsernameDoToken(token);
                    String role = tokenProvider.pegarRoleDoToken(token);

                    // Carrega dados do banco (verifica se usuário ainda existe)
                    var userDetails = userDetailsService.loadUserByUsername(username);

                    // Cria autenticação
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities
                    );

                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Define o usuário como LOGADO no contexto
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {
                // --- PULO DO GATO ---
                // Se o token for inválido, expirado ou usuário não existir mais:
                // NÃO FAZEMOS NADA. Apenas ignoramos o erro.
                // O SecurityContext continua vazio (Anônimo).
                // O SecurityConfig lá em cima vai decidir se Anônimo pode passar.
                // Isso permite que o cadastro (POST /usuarios) funcione mesmo com token "podre".
                System.out.println("Erro na autenticação do token (Ignorado): " + e.getMessage());
            }
        }

        // Continua a requisição para o Controller
        filterChain.doFilter(request, response);
    }
}