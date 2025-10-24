package com.example.shelegpt.repo;

import com.example.shelegpt.entity.DocumentLoaderEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentLoadRepository extends JpaRepository<DocumentLoaderEntity, Long> {

    boolean existsByFilenameAndContentHash(String fileName, String contentHash);
}
