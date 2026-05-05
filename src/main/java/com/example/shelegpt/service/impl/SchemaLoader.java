package com.example.shelegpt.service.impl;

import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class SchemaLoader {

    private final String schema;

    public SchemaLoader() {
        Path file = Path.of("SCHEMA.md");
        String body;
        try {
            body = Files.exists(file) ? Files.readString(file) : "";
        } catch (Exception e) {
            body = "";
        }
        this.schema = body;
    }

    /** Returns SCHEMA.md verbatim, or "" if it doesn't exist. */
    public String schema() {
        return schema;
    }

    /** Convenience: schema framed as a system-prompt block, or empty string. */
    public String asSystemBlock() {
        if (schema.isBlank()) return "";
        return "Authoritative wiki schema (SCHEMA.md). Follow it strictly:\n\n" + schema + "\n\n---\n\n";
    }
}
