package com.ufc.diversos.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private int id;

 private String nome;
 private String email;
 private String username;
 private String senha;
 private String telefone;
 private String cpf;

 @Embedded
 private Endereco endereco;

 @Enumerated(EnumType.STRING)
 private statusUsuario status;

 @Enumerated(EnumType.STRING)
 private tipoDeUsuario tipoDeUsuario;

 @ManyToMany
 @JoinTable(
         name = "usuarios_vagas_salvas",
         joinColumns = @JoinColumn(name = "usuario_id"),
         inverseJoinColumns = @JoinColumn(name = "vaga_id")
 )
 private List<Vaga> vagasSalvas;

 // ---------- MÉTODOS DO USERDETAILS ----------
 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
  return List.of(() -> "ROLE_" + tipoDeUsuario.name());
 }

 @Override
 public String getPassword() { return senha; }

 @Override
 public String getUsername() { return username; }

 @Override
 public boolean isAccountNonExpired() { return true; }

 @Override
 public boolean isAccountNonLocked() { return true; }

 @Override
 public boolean isCredentialsNonExpired() { return true; }

 @Override
 public boolean isEnabled() { return true; }
}
