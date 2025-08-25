package com.chenyuxin.xinaiagent.agent;

import com.chenyuxin.xinaiagent.advisor.MyLoggerAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.stereotype.Component;

/**
 * @author YuXin.Dev
 * @Date: 2025/8/25 19:08
 */
@Component
public class XinManus extends ToolCallAgent{
    public XinManus(ToolCallback[] toolCallbacks, ChatModel dashscopeChatModel) {
        super(toolCallbacks);
        this.setName("xinManus");
        String SYSTEM_PROMPT = """
                    You are XinManus, an all-capable AI assistant, aimed at solving any task presented by the user. \s
                    You have various tools at your disposal that you can call upon to efficiently complete complex requests. \s
                """;
        this.setSystemPrompt(SYSTEM_PROMPT);
        String NEXT_STEP_PROMPT = """
                Based on user needs, proactively select the most appropriate tool or combination of tools. \s
                                For complex tasks, you can break down the problem and use different tools step by step to solve it. \s
                                After using each tool, clearly explain the execution results and suggest the next steps. \s
                                If you want to stop the interaction at any point, use the `terminate` tool/function call. \s
                                You need to finish within """ + getMaxSteps() + "step. \s"
               ;
        this.setNextStepPrompt(NEXT_STEP_PROMPT);
        this.setMaxSteps(5);
        ChatClient chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultAdvisors(new MyLoggerAdvisor())
                .build();
        this.setChatClient(chatClient);

    }
}
