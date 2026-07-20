package com.ecommerce.pedido.services;

import com.ecommerce.pedido.services.exceptions.ValidacaoNegocioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@Service
public class FileStorageService {

    private static final String UPLOAD_DIR = "/data/uploads";
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    public String salvarImagem(Long entidadeId, String prefixo, MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new ValidacaoNegocioException("Arquivo vazio.");
        }

        if (arquivo.getSize() > MAX_FILE_SIZE) {
            throw new ValidacaoNegocioException("Arquivo muito grande. Tamanho máximo: 5MB.");
        }

        String extensao = extrairExtensao(arquivo.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extensao.toLowerCase())) {
            throw new ValidacaoNegocioException("Formato inválido. Aceitos: JPEG, PNG, WebP.");
        }

        try {
            Path dir = Paths.get(UPLOAD_DIR, prefixo);
            Files.createDirectories(dir);

            String nomeArquivo = entidadeId + "." + extensao;
            Path destino = dir.resolve(nomeArquivo);

            arquivo.transferTo(destino.toFile());

            return "/uploads/" + prefixo + "/" + nomeArquivo;
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar arquivo.", e);
        }
    }

    public void deletarImagem(String path) {
        if (path == null || path.isBlank()) return;
        try {
            Path arquivo = Paths.get(UPLOAD_DIR).resolve(path.replace("/uploads/", ""));
            Files.deleteIfExists(arquivo);
        } catch (IOException e) {
            // Se falhar ao deletar, não quebra a operação
        }
    }

    private String extrairExtensao(String nomeArquivo) {
        if (nomeArquivo == null || !nomeArquivo.contains(".")) {
            throw new ValidacaoNegocioException("Arquivo sem extensão.");
        }
        return nomeArquivo.substring(nomeArquivo.lastIndexOf(".") + 1);
    }
}
