package com.ufc.diversos.service;

import com.ufc.diversos.model.Endereco;
import com.ufc.diversos.model.Usuario;
import com.ufc.diversos.model.Vaga;
import com.ufc.diversos.model.tipoDeUsuario;
import com.ufc.diversos.repository.UsuarioRepository;
import com.ufc.diversos.repository.VagaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final VagaRepository vagaRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          VagaRepository vagaRepository,
                          BCryptPasswordEncoder encoder) {
        this.usuarioRepository = usuarioRepository;
        this.vagaRepository = vagaRepository;
        this.passwordEncoder = encoder;
    }

    // --- MÉTODOS DE SUPORTE (SEGURANÇA) ---

    public Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Erro de segurança: Usuário logado não encontrado."));
    }

    // --- CRUD ---

    public Usuario criarUsuario(Usuario usuario){
        if (usuario.getTipoDeUsuario() == null) {
            usuario.setTipoDeUsuario(tipoDeUsuario.USUARIO);
        }
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Optional<Usuario> atualizarUsuario(int idAlvo, Usuario dadosAtualizados){
        Usuario usuarioLogado = getUsuarioLogado();

        return usuarioRepository.findById(idAlvo).map(usuarioAlvo -> {

            // 1. Validação de Segurança (Quem pode editar quem?)
            validarPermissaoDeEdicao(usuarioLogado, usuarioAlvo, idAlvo);

            // 2. Atualização de Campos Básicos (Strings)
            atualizarSeValido(dadosAtualizados.getNome(), usuarioAlvo::setNome);
            atualizarSeValido(dadosAtualizados.getEmail(), usuarioAlvo::setEmail);
            atualizarSeValido(dadosAtualizados.getUsername(), usuarioAlvo::setUsername);
            atualizarSeValido(dadosAtualizados.getTelefone(), usuarioAlvo::setTelefone);
            atualizarSeValido(dadosAtualizados.getCpf(), usuarioAlvo::setCpf);

            // 3. Atualização de Senha
            if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
                usuarioAlvo.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
            }

            // 4. Atualização de Endereço (Lógica extraída)
            atualizarCamposEndereco(usuarioAlvo, dadosAtualizados.getEndereco());

            // 5. Atualização de Cargos/Status (Regras complexas extraídas)
            aplicarRegrasDeHierarquia(usuarioLogado, usuarioAlvo, dadosAtualizados);

            return usuarioRepository.save(usuarioAlvo);
        });
    }

    // --- MÉTODOS PRIVADOS DE LÓGICA (Refatoração) ---

    private void validarPermissaoDeEdicao(Usuario logado, Usuario alvo, int idAlvo) {
        boolean isStaff = logado.getTipoDeUsuario() == tipoDeUsuario.ADMINISTRADOR ||
                logado.getTipoDeUsuario() == tipoDeUsuario.MODERADOR;

        // Regra: Usuário comum só mexe nele mesmo
        if (!isStaff && logado.getId() != idAlvo) {
            throw new RuntimeException("Acesso negado: Você não pode alterar dados de outros usuários.");
        }

        // Regra: Moderador vs Admin/Moderador
        if (logado.getTipoDeUsuario() == tipoDeUsuario.MODERADOR) {
            if (alvo.getTipoDeUsuario() == tipoDeUsuario.ADMINISTRADOR) {
                throw new RuntimeException("Acesso negado: Moderadores não podem alterar Administradores.");
            }
            if (alvo.getTipoDeUsuario() == tipoDeUsuario.MODERADOR && logado.getId() != idAlvo) {
                throw new RuntimeException("Acesso negado: Moderadores não podem alterar outros Moderadores.");
            }
        }
    }

    private void aplicarRegrasDeHierarquia(Usuario logado, Usuario alvo, Usuario dados) {
        // ADMIN: Pode tudo
        if (logado.getTipoDeUsuario() == tipoDeUsuario.ADMINISTRADOR) {
            atualizarSePresente(dados.getTipoDeUsuario(), alvo::setTipoDeUsuario);
            atualizarSePresente(dados.getStatus(), alvo::setStatus);
        }
        // MODERADOR: Só altera status/tipo de usuários comuns
        else if (logado.getTipoDeUsuario() == tipoDeUsuario.MODERADOR) {
            if (alvo.getTipoDeUsuario() == tipoDeUsuario.USUARIO) {
                // Impede promoção para ADMIN
                if (dados.getTipoDeUsuario() != null && dados.getTipoDeUsuario() != tipoDeUsuario.ADMINISTRADOR) {
                    alvo.setTipoDeUsuario(dados.getTipoDeUsuario());
                }
                atualizarSePresente(dados.getStatus(), alvo::setStatus);
            }
        }
    }

    private void atualizarCamposEndereco(Usuario alvo, Endereco novoEndereco) {
        if (novoEndereco == null) return;

        if (alvo.getEndereco() == null) {
            alvo.setEndereco(novoEndereco);
        } else {
            Endereco endAntigo = alvo.getEndereco();
            atualizarSeValido(novoEndereco.getRua(), endAntigo::setRua);
            atualizarSeValido(novoEndereco.getCep(), endAntigo::setCep);
            atualizarSeValido(novoEndereco.getCidade(), endAntigo::setCidade);
            atualizarSeValido(novoEndereco.getEstado(), endAntigo::setEstado);
            atualizarSeValido(novoEndereco.getBairro(), endAntigo::setBairro);
            atualizarSeValido(novoEndereco.getNumero(), endAntigo::setNumero);
            atualizarSeValido(novoEndereco.getComplemento(), endAntigo::setComplemento);
        }
    }

    // --- MÉTODOS AUXILIARES GENÉRICOS (Clean Code) ---

    // Para Strings: verifica null e vazio ("")
    private void atualizarSeValido(String valor, Consumer<String> setter) {
        if (valor != null && !valor.isBlank()) {
            setter.accept(valor);
        }
    }

    // Para Objetos/Enums: verifica apenas null
    private <T> void atualizarSePresente(T valor, Consumer<T> setter) {
        if (valor != null) {
            setter.accept(valor);
        }
    }

    // --- MÉTODOS DE LEITURA E DELEÇÃO ---

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

    // --- FUNCIONALIDADES DE VAGAS SALVAS ---

    @Transactional
    public void salvarVaga(Long vagaId) {
        Usuario usuario = getUsuarioLogado();
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        if (!usuario.getVagasSalvas().contains(vaga)) {
            usuario.getVagasSalvas().add(vaga);
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void removerVagaSalva(Long vagaId) {
        Usuario usuario = getUsuarioLogado();
        Vaga vaga = vagaRepository.findById(vagaId)
                .orElseThrow(() -> new RuntimeException("Vaga não encontrada"));

        usuario.getVagasSalvas().remove(vaga);
        usuarioRepository.save(usuario);
    }

    public List<Vaga> listarMinhasVagasSalvas() {
        return getUsuarioLogado().getVagasSalvas();
    }
}