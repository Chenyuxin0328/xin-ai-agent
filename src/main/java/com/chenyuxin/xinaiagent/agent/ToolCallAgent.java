package com.chenyuxin.xinaiagent.agent;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.chenyuxin.xinaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.ToolResponseMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.tool.ToolCallback;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author YuXin.Dev
 * @Date: 2025/8/25 18:35
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class ToolCallAgent extends ReActAgent{

    private final ToolCallback[] availableTools;
    private ChatResponse toolCallResponse;
    private final ToolCallingManager toolCallingManager;
    private final ChatOptions chatOptions;

    public ToolCallAgent(ToolCallback[] toolCallbacks){
        super();
        this.availableTools = toolCallbacks;
        this.toolCallingManager = ToolCallingManager.builder().build();
        this.chatOptions = DashScopeChatOptions.builder()
                .withProxyToolCalls(true) // 手动控制
                .build();
    }
    @Override
    public boolean think() {
        if(getNextStepPrompt() != null && !getNextStepPrompt().isEmpty()){
            UserMessage userMessage = new UserMessage(getNextStepPrompt());
            getMessageList().add(userMessage);
        }
        List<Message> messageList = getMessageList();
        Prompt prompt = new Prompt(messageList, chatOptions);
        try {
            ChatResponse chatResponse = getChatClient().prompt(prompt)
                    .system(getSystemPrompt())
                    .tools(availableTools)
                    .call()
                    .chatResponse();
            this.toolCallResponse = chatResponse;
            AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
            String result = assistantMessage.getText();
            List<AssistantMessage.ToolCall> toolCallList = assistantMessage.getToolCalls();
            log.info(getName()+"的思考:"+result);
            log.info(getName()+"选择调用了:"+toolCallList.size()+"个工具");
            String toolCallInfo = toolCallList.stream()
                    .map(toolCall -> String.format("工具名称：%s,参数：%s", toolCall.name(), toolCall.arguments()))
                    .collect(Collectors.joining("\n"));
            log.info(toolCallInfo);
            if(toolCallList.isEmpty()){
                getMessageList().add(assistantMessage);
                return false;
            }else{
                return true;
            }
        } catch (Exception e) {
            log.error(getName()+"的思考过程遇到了问题"+e.getMessage());
            getMessageList().add(
                    new AssistantMessage("处理时遇到了错误"+e.getMessage())
            );
            return false;
        }
    }

    @Override
    public String act() {
        if(!toolCallResponse.hasToolCalls()){
            return "没有工具调用";
        }
        Prompt prompt = new Prompt(getMessageList(),chatOptions);
        ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, toolCallResponse);
        setMessageList(toolExecutionResult.conversationHistory());
        ToolResponseMessage toolResponseMessage = (ToolResponseMessage) CollUtil.getLast(toolExecutionResult.conversationHistory());
        String results = toolResponseMessage.getResponses().stream()
                .map(response -> "工具"+ response.name()+"完成了它的任务！结果："+response.responseData())
                .collect(Collectors.joining("\n"));
        boolean terminateToolCalled = toolResponseMessage.getResponses().stream()
                        .anyMatch(response ->"doTerminate".equals(response.name()));
        if(terminateToolCalled){
            setState(AgentState.FINISHED);
        }
        log.info(results);
        return results;
    }
}
