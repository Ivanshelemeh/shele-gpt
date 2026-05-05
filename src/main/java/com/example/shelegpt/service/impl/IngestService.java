package com.example.shelegpt.service.impl;

import com.example.shelegpt.model.WikiProperties;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
@Slf4j
public class IngestService {

    private RestClient client;
    private final ChatClient chatClient;
    private final WikiProperties props;

    public IngestService(RestClient.Builder restClientBuilder,
                         ChatClient.Builder chatClientBuilder,
                         WikiProperties props) {
        this.client = restClientBuilder.build();
        this.chatClient = chatClientBuilder.build();
        this.props = props;
    }

    public Path ingestUrl(String url, String title, List<String> tags) throws Exception {
        log.info("Ingesting URL: {}", url);

        String html = client.get()
                                .uri(url)
                                .retrieve()
                                .body(String.class);

        Document doc = Jsoup.parse(html, url);
        doc.select(
                "script, style, noscript, iframe, svg, link, meta, " +
                "nav, header, footer, aside, form, button, " +
                "[role=navigation], [role=banner], [role=contentinfo], " +
                ".nav, .navigation, .menu, .sidebar, .footer, .header, " +
                ".comments, .related, .share, .social, .ads, .advertisement"
        ).remove();

        Element main = doc.selectFirst(
                "article, main, [role=main], .post, .post-content, .entry-content, .article-content"
        );
        Element root = main != null ? main : doc.body();
        String cleaned = root.html();
        log.info("Extracted {} chars of content from {} chars of HTML", cleaned.length(), html.length());

        String prompt = """
                Convert the following HTML fragment into clean, readable Markdown.
                - Preserve headings, code blocks, lists, tables, and meaningful links.
                - Output Markdown only — no commentary.

                Source URL: %s

                HTML:
                %s
                """.formatted(url, cleaned);

        String markdown = chatClient.prompt().user(prompt).call().content();

        String slug = slugify(title != null && !title.isBlank() ? title : url);
        String filename = LocalDate.now() + "-" + slug + ".md";
        Path rawDir = Path.of(props.paths().raw());
        Files.createDirectories(rawDir);
        Path file = rawDir.resolve(filename);

        StringBuilder out = new StringBuilder();
        out.append("---\n");
        if (title != null) out.append("title: ").append(title).append('\n');
        out.append("source: ").append(url).append('\n');
        out.append("ingested: ").append(LocalDate.now()).append('\n');
        if (tags != null && !tags.isEmpty()) {
            out.append("tags: [").append(String.join(", ", tags)).append("]\n");
        }
        out.append("---\n\n");
        out.append(markdown);

        Files.writeString(file, out.toString());
        log.info("Saved ingested content to {}", file);
        return file;
    }

    private static String slugify(String input) {
        String s = input.toLowerCase(Locale.ROOT)
                        .replaceAll("https?://", "")
                        .replaceAll("[^a-z0-9]+", "-")
                        .replaceAll("(^-|-$)", "");
        return s.length() > 80 ? s.substring(0, 80) : s;
    }
}
