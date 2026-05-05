package com.example.shelegpt.model;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;

@ConfigurationProperties(prefix = "wiki")
public record WikiProperties(Paths paths, Ingest ingest) {

    public record Paths(@NonNull String raw, @NonNull String wiki, @NonNull String skills, @NonNull String memory) {}

    public record Ingest(boolean autoCompile) {}
}
