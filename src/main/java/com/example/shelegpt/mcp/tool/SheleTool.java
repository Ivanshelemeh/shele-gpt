package com.example.shelegpt.mcp.tool;

import io.modelcontextprotocol.spec.McpSchema;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springaicommunity.mcp.annotation.McpTool;
import org.springaicommunity.mcp.annotation.McpToolParam;
import org.springaicommunity.mcp.context.McpSyncRequestContext;
import org.springaicommunity.mcp.context.StructuredElicitResult;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;

@Component
@Slf4j
@Builder
public class SheleTool {

    @McpTool(name = "shele-tool-chat", description = "You can use this tool for  chat with shele")
    public String chatShele(
            @NotNull @McpToolParam(description = "Message input") String inputMessage,
            @NotNull @McpToolParam(description = "Type of message") String typeMessage,
            McpSyncRequestContext cntx
    ) {

        if (!cntx.elicitEnabled()) {
            return "Please provide any message , type";
        }
        StructuredElicitResult<SheleChatModel> result = cntx.elicit(
                elicitationSpec -> elicitationSpec.message("Let's complete your chat message"),
                SheleChatModel.class
        );
        if (result.action() != McpSchema.ElicitResult.Action.ACCEPT) {
            return "Cound not create shele chat";

        }
        var chatModel = result.structuredContent();
        log.info(
                "Income message to shele: message {}, type {}", chatModel.inputMessage(),
                chatModel.messageType().name()
        );
        return "Chat is start : %s, %s".formatted(chatModel.inputMessage(), chatModel.messageType().name());

    }

}



