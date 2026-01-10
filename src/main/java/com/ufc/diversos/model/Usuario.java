package com.ufc.diversos.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
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

 @Column(name = "foto_perfil")
 private String fotoPerfil;

 private String telefone;

 @JsonFormat(pattern = "yyyy-MM-dd")
 private LocalDate dataNascimento;

 private String cpf;
 private String pronomes;

 @Embedded
 private Endereco endereco;

 @Enumerated(EnumType.STRING)
 private StatusUsuario status; // Mudou para StatusUsuario (Maiúscula)

 @Enumerated(EnumType.STRING)
 private TipoDeUsuario tipoDeUsuario; // Sugiro mudar TipoDeUsuario também

 @ManyToMany
 @JoinTable(
         name = "usuarios_vagas_salvas",
         joinColumns = @JoinColumn(name = "usuario_id"),
         inverseJoinColumns = @JoinColumn(name = "vaga_id")
 )
 private List<Vaga> vagasSalvas;

 @ManyToMany(fetch = FetchType.LAZY)
 @JoinTable(
         name = "usuarios_grupos_salvos",
         joinColumns = @JoinColumn(name = "usuario_id"),
         inverseJoinColumns = @JoinColumn(name = "grupo_id")
 )
 private List<Grupo> gruposSalvos;

 @ManyToMany(fetch = FetchType.EAGER)
 @JoinTable(
         name = "usuarios_habilidades",
         joinColumns = @JoinColumn(name = "usuario_id"),
         inverseJoinColumns = @JoinColumn(name = "habilidade_id")
 )
 private List<Habilidade> habilidades;


 // ---------- MÉTODOS DO USERDETAILS (A Mágica Acontece Aqui) ----------

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
 public boolean isEnabled() {
  return this.status == StatusUsuario.ATIVO;
 }

 @Override
 public boolean isAccountNonLocked() {
  return this.status != StatusUsuario.BLOQUEADO && this.status != StatusUsuario.SUSPENSO;
 }

 @Override
 public boolean isAccountNonExpired() { return true; }

 @Override
 public boolean isCredentialsNonExpired() { return true; }
}