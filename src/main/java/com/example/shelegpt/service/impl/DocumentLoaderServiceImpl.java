package com.example.shelegpt.service.impl;

import com.example.shelegpt.entity.DocumentLoaderEntity;
import com.example.shelegpt.repo.DocumentLoadRepository;
import com.example.shelegpt.service.DocumentLoaderService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentLoaderServiceImpl implements DocumentLoaderService, CommandLineRunner {

    private static final Integer DEFAULT_CHUNK_SIZE = 500;
    private final DocumentLoadRepository loadRepository;
    private final VectorStore vectorStore;
    private final ResourcePatternResolver patternResolver;

    @SneakyThrows
    @Override
    public void loadDocuments() {
        List<Resource> resourceList = Arrays.stream(patternResolver.getResources("classpath:/knowlegebase/**/*.txt"))
                                            .toList();
        resourceList.stream()
                    .filter(this::existsResource)
                    .forEach(this::createDocumentEntity);

    }

    @SneakyThrows
    private String calcContentHash(Resource resource) {
        return DigestUtils.md5DigestAsHex(resource.getInputStream());
    }

    private boolean existsResource(Resource resource) {
        return loadRepository.existsByFilenameAndContentHash(
                resource.getFilename(),
                calcContentHash(resource)
        );
    }

    private void createDocumentEntity(Resource resource) {
        List<Document> documents = new TextReader(resource).get();
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                                                      .withChunkSize(DEFAULT_CHUNK_SIZE)
                                                      .build();
        List<Document> chunks = splitter.apply(documents);
        vectorStore.accept(chunks);
        DocumentLoaderEntity txt = DocumentLoaderEntity.builder()
                                                       .documentType("txt")
                                                       .contentHash(calcContentHash(resource))
                                                       .chunkCount(chunks.size())
                                                       .filename(resource.getFilename())
                                                       .build();
        loadRepository.save(txt);

    }

    @Override
    public void run(String... args) throws Exception {
         loadDocuments();
    }
}
