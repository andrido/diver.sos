package com.ufc.diversos.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ArquivoService {

    // Define onde as pastas vão ficar na raiz do projeto
    private final Path diretorioRaiz = Paths.get("uploads");

    public String salvarArquivo(MultipartFile arquivo, String subDiretorio) {
        if (arquivo.isEmpty()) {
            throw new RuntimeException("Arquivo vazio.");
        }

        try {
            // 1. Cria o caminho: uploads/perfis/
            Path pastaDestino = diretorioRaiz.resolve(subDiretorio);
            if (!Files.exists(pastaDestino)) {
                Files.createDirectories(pastaDestino);
            }

            // 2. Gera nome único para evitar cache do navegador e sobreposição
            // Ex: user_1_uuid.jpg
            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = "";
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            }
            String nomeArquivo = UUID.randomUUID().toString() + extensao;

            // 3. Copia o arquivo para a pasta (Substitui se existir igual)
            Path caminhoArquivo = pastaDestino.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

            // 4. Retorna o caminho relativo para salvar no banco
            // Retorna: /imagens/perfis/nome-do-arquivo.jpg
            return "/imagens/" + subDiretorio + "/" + nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}