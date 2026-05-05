package com.example.shelegpt.config;

import com.example.shelegpt.model.WikiProperties;
import org.springaicommunity.agent.advisors.AutoMemoryToolsAdvisor;
import org.springaicommunity.agent.tools.FileSystemTools;
import org.springaicommunity.agent.tools.GlobTool;
import org.springaicommunity.agent.tools.GrepTool;
import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;

@Configuration
public class ToolsConfig {

    private final WikiProperties wikiProperties;


    public ToolsConfig(WikiProperties wikiProperties) {
        this.wikiProperties = wikiProperties;
    }

    @Bean
    FileSystemTools fileSystemTools() {
        return FileSystemTools.builder().build();
    }

    @Bean
    GrepTool grepTool() {
        return GrepTool.builder().workingDirectory(Path.of(".")).build();
    }

    @Bean
    public GlobTool globTool() {
        return GlobTool.builder().workingDirectory(Path.of(".")).build();
    }

    @Bean
    public ToolCallback skillsTool() {
        return SkillsTool.builder()
                         .addSkillsDirectory(wikiProperties.paths().skills())
                         .build();
    }

    @Bean
    public AutoMemoryToolsAdvisor autoMemoryToolsAdvisor() {
        return AutoMemoryToolsAdvisor.builder()
                                     .memoriesRootDirectory(wikiProperties.paths().memory())
                                     .build();

    }

}
