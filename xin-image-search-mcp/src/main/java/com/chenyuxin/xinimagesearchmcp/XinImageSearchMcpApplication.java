package com.chenyuxin.xinimagesearchmcp;

import com.chenyuxin.xinimagesearchmcp.tools.ImageSearchTool;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class XinImageSearchMcpApplication {

    public static void main(String[] args) {
        SpringApplication.run(XinImageSearchMcpApplication.class, args);
    }
    @Bean
    public ToolCallbackProvider imageSearchTools(ImageSearchTool imageSearchTool){
        return MethodToolCallbackProvider.builder()
                .toolObjects(imageSearchTool)
                .build();
    }
}
