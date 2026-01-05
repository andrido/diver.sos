package com.ufc.diversos.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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

 @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
 private String senha;

 private String telefone;

 private String cpf;

 private String pronomes;

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

 @ManyToMany(fetch = FetchType.EAGER)
 @JoinTable(
         name = "usuarios_habilidades", // Nome da tabela de ligação
         joinColumns = @JoinColumn(name = "usuario_id"),
         inverseJoinColumns = @JoinColumn(name = "habilidade_id")
 )
 private List<Habilidade> habilidades;

 // ---------- MÉTODOS DO USERDETAILS ----------
 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
  return List.of(() -> "ROLE_" + tipoDeUsuario.name());
 }

 @Override
 @JsonIgnore
 public String getPassword() { return senha; }

 @Override
 public String getUsername() { return email; }

 @Override
 public boolean isAccountNonExpired() { return true; }

 @Override
 public boolean isAccountNonLocked() { return true; }

 @Override
 public boolean isCredentialsNonExpired() { return true; }

 @Override
 public boolean isEnabled() { return true; }
}
