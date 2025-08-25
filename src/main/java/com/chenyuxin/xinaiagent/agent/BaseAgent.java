package com.chenyuxin.xinaiagent.agent;

import com.chenyuxin.xinaiagent.agent.model.AgentState;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YuXin.Dev
 * @Date: 2025/8/25 18:10
 */
@Data
@Slf4j
public abstract class BaseAgent {
    private String name;
    private String SystemPrompt;
    private String nextStepPrompt;
    private AgentState state = AgentState.IDLE;

    private int maxSteps = 10;
    private int currentSteps = 0;

    private ChatClient chatClient;

    private List<Message> messageList = new ArrayList<>();

    public String run (String userPrompt){
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("智能体状态不是空闲，无法运行智能体");
        }
        if(userPrompt==null){
            throw new RuntimeException("用户提示词为空，无法运行智能体");
        }
        state = AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));
        List<String> results = new ArrayList<>();
        try{
            for(int i = 0;i<maxSteps&& state!=AgentState.FINISHED;i++){
                int stepNum = i+1;
                currentSteps = stepNum;
                log.info("智能体执行第"+currentSteps+"/"+maxSteps+"步骤");
                String stepResult = this.step();
                String result = "Step"+stepNum+":"+stepResult;
                results.add(result);
            }
            if(currentSteps>=maxSteps){
                state = AgentState.FINISHED;
                results.add("Terminate:Reached max steps ("+maxSteps+")");
            }
            return String.join("\n",results);
        }catch (Exception e){
            state = AgentState.ERROR;
            log.error("Error executing agent",e);
            return "执行错误"+e.getMessage();
        }finally {
            this.cleanup();
        }
    }

    public abstract String step();

    protected void cleanup() {

    }

}
