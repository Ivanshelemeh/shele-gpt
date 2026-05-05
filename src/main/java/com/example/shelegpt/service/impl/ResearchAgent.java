package com.example.shelegpt.service.impl;

import com.example.shelegpt.model.QueryResult;
import org.springaicommunity.agent.advisors.AutoMemoryToolsAdvisor;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.GlobTool;
import org.springaicommunity.agent.tools.GrepTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
public class ResearchAgent {

    private static final String SYSTEM_AGENT_PROMT = """
            You are the ResearchAgent for a personal Markdown knowledge base.
            
                        For every user question:
                          1. Search wiki/ first (use grep/glob/read tools).
                          2. Synthesize a clear, well-cited answer.
                          3. When the user asks for a "note", "comparison", "slides", or
                             similar artifact, write a Markdown file under wiki/outputs/.
                             Use Mermaid for diagrams and Marp front-matter for slides.
                          4. ALWAYS populate the `sources` array with the wiki-relative
                             paths of every file you actually read to answer the question
                             (e.g. "wiki/articles/react.md"). Never leave sources empty
                             unless you genuinely could not find anything in the wiki.
                          5. If a wiki page has a `repo` front-matter field, it points to a
                             local clone of the associated source code. When answering code
                             questions, read actual source files from the repo (use grep/glob
                             to find relevant files, then read them). Include real code
                             snippets in your answer — never paraphrase code.
                          6. If wiki coverage is thin, say so in `answer` and suggest what
                             should be ingested next.
            """;
    private final ChatClient chatClient;

    public ResearchAgent(
            ChatClient.Builder builder,
            FileSystemTools fs,
            GrepTool grepTool,
            GlobTool globTool,
            ToolCallback skillTool,
            AutoMemoryToolsAdvisor memoryToolsAdvisor,
            SchemaLoader loader
    ) {
        this.chatClient = builder
                .defaultSystem(loader.asSystemBlock() + SYSTEM_AGENT_PROMT)
                .defaultTools(fs, grepTool, globTool)
                .defaultToolCallbacks(skillTool)
                .defaultAdvisors(memoryToolsAdvisor)
                .build();

    }

    public QueryResult query(@NotNull String question) {
        return chatClient.prompt().user(question).call().entity(QueryResult.class);
    }


}
