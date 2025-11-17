package com.ufc.diversos.service;

import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario criarUsuario(Usuario usuario){
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios(){
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(int id){
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> atualizarUsuario(int id, Usuario usuarioAtualizado){
        return usuarioRepository.findById(id).map(u -> {
            u.setNome(usuarioAtualizado.getNome());
            u.setEmail(usuarioAtualizado.getEmail());
            u.setUsername(usuarioAtualizado.getUsername());
            u.setSenha(usuarioAtualizado.getSenha());
            u.setTelefone(usuarioAtualizado.getTelefone());
            u.setCpf(usuarioAtualizado.getCpf());
            u.setEndereco(usuarioAtualizado.getEndereco());
            u.setStatus(usuarioAtualizado.getStatus());
            u.setTipoDeUsuario(usuarioAtualizado.getTipoDeUsuario());
            return usuarioRepository.save(u);
        });
    }

    public boolean deletarUsuario(int id){
        if(usuarioRepository.existsById(id)){
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
