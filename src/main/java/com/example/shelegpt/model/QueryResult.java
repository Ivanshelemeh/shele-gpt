package com.example.shelegpt.model;

import java.util.List;

public record QueryResult(
        String answer,
        List<String> sources
) {
}
