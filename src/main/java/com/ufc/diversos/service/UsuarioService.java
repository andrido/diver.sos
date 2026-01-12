package com.ufc.diversos.service;

import com.ufc.diversos.model.*;
import com.ufc.diversos.repository.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID; // Importante para gerar o token
import java.util.function.Consumer;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final VagaRepository vagaRepository;
    private final HabilidadeRepository habilidadeRepository;
    private final GrupoRepository grupoRepository;
    private final VerificationTokenRepository tokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ArquivoService arquivoService;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          VagaRepository vagaRepository,
                          HabilidadeRepository habilidadeRepository,
                          GrupoRepository grupoRepository,
                          VerificationTokenRepository tokenRepository,
                          EmailService emailService,
                          BCryptPasswordEncoder encoder,
                          ArquivoService arquivoService) {

        this.usuarioRepository = usuarioRepository;
        this.vagaRepository = vagaRepository;
        this.habilidadeRepository = habilidadeRepository;
        this.grupoRepository = grupoRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = encoder;
        this.arquivoService = new ArquivoService();
    }

    // --- MÉTODOS DE SUPORTE (SEGURANÇA) ---

    public Usuario getUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Erro de segurança: Usuário logado não encontrado."));
    }

    // --- CADASTRO E CONFIRMAÇÃO ---

    @Transactional
    public Usuario criarUsuario(Usuario usuario){
        // 1. Verifica se e-mail já existe
        if (usuarioRepository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new RuntimeException("Este e-mail já está em uso.");
        }

        // 2. Define padrões
        if (usuario.getTipoDeUsuario() == null) {
            usuario.setTipoDeUsuario(TipoDeUsuario.USUARIO);
        }

        // 3. Criptografa senha
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Define status INATIVO até confirmar e-mail
        usuario.setStatus(StatusUsuario.INATIVO);

        // 4. Salva no Banco (Rápido)
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // 5. Cria o Token
        VerificationToken token = new VerificationToken(usuarioSalvo);
        tokenRepository.save(token);


        emailService.enviarEmailConfirmacao(usuarioSalvo.getEmail(), token.getToken());

        return usuarioSalvo;
    }
    @Transactional
    public String confirmarConta(String token) {
        VerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token inválido ou inexistente."));

        Usuario usuario = verificationToken.getUsuario();

        // Verifica expiração
        if (verificationToken.getDataExpiracao().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Este link de confirmação expirou. Solicite um novo.");
        }

        // --- CORREÇÃO PRINCIPAL AQUI ---
        // Ativa o usuário mudando o Status
        usuario.setStatus(StatusUsuario.ATIVO);
        usuarioRepository.save(usuario);

        // Remove o token usado para limpar o banco
        tokenRepository.delete(verificationToken);

        return "Conta verificada com sucesso! Você já pode fazer login.";
    }

    // --- ATUALIZAÇÃO (UPDATE) ---

    @Transactional
    public Usuario atualizarFotoPerfil(Usuario usuario, org.springframework.web.multipart.MultipartFile arquivo) {

        if (arquivo == null || arquivo.isEmpty()) {
            throw new RuntimeException("O arquivo de imagem não pode ser vazio.");
        }

        String caminhoDaFoto = arquivoService.salvarArquivo(arquivo, "perfis");

        usuario.setFotoPerfil(caminhoDaFoto);

        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Optional<Usuario> atualizarUsuario(int idAlvo, Usuario dadosAtualizados){
        Usuario usuarioLogado = getUsuarioLogado();

        return usuarioRepository.findById(idAlvo).map(usuarioAlvo -> {

            // 1. Validação de Segurança
            validarPermissaoDeEdicao(usuarioLogado, usuarioAlvo, idAlvo);

            // 2. Atualização de Campos Básicos
            atualizarSeValido(dadosAtualizados.getNome(), usuarioAlvo::setNome);
            atualizarSeValido(dadosAtualizados.getEmail(), usuarioAlvo::setEmail);
            atualizarSeValido(dadosAtualizados.getTelefone(), usuarioAlvo::setTelefone);
            atualizarSeValido(dadosAtualizados.getCpf(), usuarioAlvo::setCpf);
            atualizarSeValido(dadosAtualizados.getPronomes(), usuarioAlvo::setPronomes);

            // Data de Nascimento
            if (dadosAtualizados.getDataNascimento() != null) {
                usuarioAlvo.setDataNascimento(dadosAtualizados.getDataNascimento());
            }

            // 3. Atualização de Senha
            if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isEmpty()) {
                usuarioAlvo.setSenha(passwordEncoder.encode(dadosAtualizados.getSenha()));
            }

            // 4. Atualização de Endereço
            atualizarCamposEndereco(usuarioAlvo, dadosAtualizados.getEndereco());

            // 5. Atualização de Cargos/Status
            aplicarRegrasDeHierarquia(usuarioLogado, usuarioAlvo, dadosAtualizados);

            // 6. Atualização de Habilidades
            if (dadosAtualizados.getHabilidades() != null) {
                List<Habilidade> novasHabilidades = new ArrayList<>();

                for (Habilidade h : dadosAtualizados.getHabilidades()) {
                    if (h.getId() != null) { // Verifica se é Long ou Integer conforme sua model
                        habilidadeRepository.findById(h.getId())
                                .ifPresent(novasHabilidades::add);
                    }
                }
                usuarioAlvo.setHabilidades(novasHabilidades);
            }

            return usuarioRepository.save(usuarioAlvo);
        });
    }

    // --- MÉTODOS PRIVADOS DE LÓGICA ---

    private void validarPermissaoDeEdicao(Usuario logado, Usuario alvo, int idAlvo) {
        boolean isStaff = logado.getTipoDeUsuario() == TipoDeUsuario.ADMINISTRADOR ||
                logado.getTipoDeUsuario() == TipoDeUsuario.MODERADOR;

        if (!isStaff && logado.getId() != idAlvo) {
            throw new RuntimeException("Acesso negado: Você não pode alterar dados de outros usuários.");
        }

        if (logado.getTipoDeUsuario() == TipoDeUsuario.MODERADOR) {
            if (alvo.getTipoDeUsuario() == TipoDeUsuario.ADMINISTRADOR) {
                throw new RuntimeException("Acesso negado: Moderadores não podem alterar Administradores.");
            }
            if (alvo.getTipoDeUsuario() == TipoDeUsuario.MODERADOR && logado.getId() != idAlvo) {
                throw new RuntimeException("Acesso negado: Moderadores não podem alterar outros Moderadores.");
            }
        }
    }

    private void aplicarRegrasDeHierarquia(Usuario logado, Usuario alvo, Usuario dados) {
        if (logado.getTipoDeUsuario() == TipoDeUsuario.ADMINISTRADOR) {
            atualizarSePresente(dados.getTipoDeUsuario(), alvo::setTipoDeUsuario);
            atualizarSePresente(dados.getStatus(), alvo::setStatus);
        }
        else if (logado.getTipoDeUsuario() == TipoDeUsuario.MODERADOR) {
            if (alvo.getTipoDeUsuario() == TipoDeUsuario.USUARIO) {
                if (dados.getTipoDeUsuario() != null && dados.getTipoDeUsuario() != TipoDeUsuario.ADMINISTRADOR) {
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

    // --- MÉTODOS AUXILIARES ---


    private void atualizarSeValido(String valor, Consumer<String> setter) {
        if (valor != null && !valor.isBlank()) {
            setter.accept(valor);
        }
    }

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

    @Transactional
    public boolean deletarUsuario(int id) {
        if (usuarioRepository.existsById(id)) {
            // Nota: Se você não configurou o CascadeType.ALL na entidade Usuario para o Token,
            // descomente a linha abaixo para evitar erro de Foreign Key:
            // tokenRepository.deleteByUsuarioId(id);

            usuarioRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // --- FUNCIONALIDADES DE VAGAS E GRUPOS (Mantidas iguais) ---
    // (Omiti para economizar espaço, mas pode manter o que você já tinha)

    @Transactional
    public void salvarVaga(Long vagaId) {
        Usuario usuario = getUsuarioLogado();
        Vaga vaga = vagaRepository.findById(vagaId).orElseThrow(() -> new RuntimeException("Vaga não encontrada"));
        if (!usuario.getVagasSalvas().contains(vaga)) {
            usuario.getVagasSalvas().add(vaga);
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void removerVagaSalva(Long vagaId) {
        Usuario usuario = getUsuarioLogado();
        Vaga vaga = vagaRepository.findById(vagaId).orElseThrow(() -> new RuntimeException("Vaga não encontrada"));
        usuario.getVagasSalvas().remove(vaga);
        usuarioRepository.save(usuario);
    }

    public List<Vaga> listarMinhasVagasSalvas() { return getUsuarioLogado().getVagasSalvas(); }

    @Transactional
    public void salvarGrupo(Long grupoId) {
        Usuario usuario = getUsuarioLogado();
        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        if (!usuario.getGruposSalvos().contains(grupo)) {
            usuario.getGruposSalvos().add(grupo);
            usuarioRepository.save(usuario);
        }
    }

    @Transactional
    public void removerGrupoSalvo(Long grupoId) {
        Usuario usuario = getUsuarioLogado();
        Grupo grupo = grupoRepository.findById(grupoId).orElseThrow(() -> new RuntimeException("Grupo não encontrado"));
        usuario.getGruposSalvos().remove(grupo);
        usuarioRepository.save(usuario);
    }

    public List<Grupo> listarMeusGruposSalvos() { return getUsuarioLogado().getGruposSalvos(); }
}