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
            Path pastaDestino = diretorioRaiz.resolve(subDiretorio);
            if (!Files.exists(pastaDestino)) {
                Files.createDirectories(pastaDestino);
            }


            String nomeOriginal = arquivo.getOriginalFilename();
            String extensao = "";
            if (nomeOriginal != null && nomeOriginal.contains(".")) {
                extensao = nomeOriginal.substring(nomeOriginal.lastIndexOf("."));
            }
            String nomeArquivo = UUID.randomUUID().toString() + extensao;


            Path caminhoArquivo = pastaDestino.resolve(nomeArquivo);
            Files.copy(arquivo.getInputStream(), caminhoArquivo, StandardCopyOption.REPLACE_EXISTING);

            return "/imagens/" + subDiretorio + "/" + nomeArquivo;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo: " + e.getMessage());
        }
    }
}