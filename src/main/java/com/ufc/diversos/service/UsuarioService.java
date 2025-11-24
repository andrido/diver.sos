package com.ufc.diversos.service;

import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.repository.UsuarioRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = encoder;
    }

    public Usuario criarUsuario(Usuario usuario){
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public Optional<Usuario> atualizarUsuario(int id, Usuario usuarioAtualizado){
        return usuarioRepository.findById(id).map(u -> {
            u.setNome(usuarioAtualizado.getNome());
            u.setEmail(usuarioAtualizado.getEmail());
            u.setUsername(usuarioAtualizado.getUsername());

            if (!usuarioAtualizado.getSenha().isEmpty()) {
                u.setSenha(passwordEncoder.encode(usuarioAtualizado.getSenha()));
            }

            u.setTelefone(usuarioAtualizado.getTelefone());
            u.setCpf(usuarioAtualizado.getCpf());
            u.setEndereco(usuarioAtualizado.getEndereco());
            u.setStatus(usuarioAtualizado.getStatus());
            u.setTipoDeUsuario(usuarioAtualizado.getTipoDeUsuario());
            return usuarioRepository.save(u);
        });
    }
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(int id) {
        return usuarioRepository.findById(id);
    }

    public boolean deletarUsuario(int id) {
        if (usuarioRepository.existsById(id)) {
            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }


}

