package com.chenyuxin.xinaiagent.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.prompt.Prompt;
import reactor.core.publisher.Flux;

/**
 * @author YuXin.Dev
 * @Date: 2025/7/14 16:48
 */
public class ReReadingAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {

    private AdvisedRequest before(AdvisedRequest advisedRequest){
        String userText = advisedRequest.userText();
        // 修改用户提示词
        String newUserText = """
                %s
                Read the question again: %s
                """.formatted(userText, userText);
        return AdvisedRequest.from(advisedRequest)
                .userText(newUserText)
                .build();
    }
    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        return chain.nextAroundCall(this.before(advisedRequest));
    }

    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        return chain.nextAroundStream(this.before(advisedRequest));
    }

    @NotNull
    @Override
    public String getName() {
        return "ReReadingAdvisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
