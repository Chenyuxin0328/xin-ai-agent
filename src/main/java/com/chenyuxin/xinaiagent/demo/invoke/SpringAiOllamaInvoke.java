package com.chenyuxin.xinaiagent.demo.invoke;


import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author YuXin.Dev
 * @Date: 2025/7/10 17:22
 */
@Component
public class SpringAiOllamaInvoke implements CommandLineRunner {
    @Autowired
    @Qualifier("ollamaChatModel")
    private ChatModel chatModel;

    @Override
    public void run(String... args) throws Exception {
        AssistantMessage message = chatModel.call(new Prompt("你好，我是chenyuxin")).getResult().getOutput();
        System.out.println(message.getText());
    }
}
