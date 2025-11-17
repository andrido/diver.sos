package com.ufc.diversos.service;

import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.model.statusUsuario;
import com.ufc.diversos.model.tipoDeUsuario;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private List<Usuario> usuarios = new ArrayList<>();
    private int idCounter = 1;

    public Usuario criarUsuario(Usuario usuario){
        usuario.setId(idCounter++);
        if(usuario.getStatus() == null) usuario.setStatus(statusUsuario.ATIVO);
        if(usuario.getTipoDeUsuario() == null) usuario.setTipoDeUsuario(tipoDeUsuario.USUARIO);
        usuarios.add(usuario);
        return usuario;
    }

    public List<Usuario> listarUsuarios(){
        return usuarios;
    }

    public Optional<Usuario> buscarPorId(int id){
        return usuarios.stream().filter(u -> u.getId() == id).findFirst();
    }

    public Optional<Usuario> atualizarUsuario(int id, Usuario usuarioAtualizado){
        Optional<Usuario> usuarioExistente = buscarPorId(id);
        usuarioExistente.ifPresent(u -> {
            u.setNome(usuarioAtualizado.getNome());
            u.setEmail(usuarioAtualizado.getEmail());
            u.setUsername(usuarioAtualizado.getUsername());
            u.setSenha(usuarioAtualizado.getSenha());
            u.setTelefone(usuarioAtualizado.getTelefone());
            u.setCpf(usuarioAtualizado.getCpf());
            u.setEndereco(usuarioAtualizado.getEndereco());
            u.setStatus(usuarioAtualizado.getStatus());
            u.setTipoDeUsuario(usuarioAtualizado.getTipoDeUsuario());
        });
        return usuarioExistente;
    }

    public boolean deletarUsuario(int id){
        return usuarios.removeIf(u -> u.getId() == id);
    }
}
