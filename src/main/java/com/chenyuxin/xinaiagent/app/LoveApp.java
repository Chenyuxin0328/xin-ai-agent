package com.chenyuxin.xinaiagent.app;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.chenyuxin.xinaiagent.advisor.MyLoggerAdvisor;
import com.chenyuxin.xinaiagent.chatmemory.MyBatisPlusChatMemory;
import com.chenyuxin.xinaiagent.rag.LoveAppContextualQueryAugmenterFactory;
import com.chenyuxin.xinaiagent.rag.LoveAppRagCustomAdvisorFactory;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.util.List;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

/**
 * @author YuXin.Dev
 * @Date: 2025/7/13 18:10
 */
@Component
@Slf4j
@Getter
public class LoveApp {

    private final MyBatisPlusChatMemory chatMemory;
    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "扮演深耕恋爱心理领域的专家。开场向用户表明身份，告知用户可倾诉恋爱难题。" +
            "围绕单身、恋爱、已婚三种状态提问：单身状态询问社交圈拓展及追求心仪对象的困扰；" +
            "恋爱状态询问沟通、习惯差异引发的矛盾；已婚状态询问家庭责任与亲属关系处理的问题。" +
            "引导用户详述事情经过、对方反应及自身想法，以便给出专属解决方案。";

    /**
     * 初始化AI客户端
     * @param dashscopeChatModel 阿里云百炼客户端
     */
    public LoveApp(DashScopeChatModel dashscopeChatModel,MyBatisPlusChatMemory chatMemory,
                   @Value("classpath:/prompts/system-message.st") Resource systemResource){
        this.chatMemory = chatMemory;
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(systemResource)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory)
//                        new MyLoggerAdvisor(),
//                        new ReReadingAdvisor()
                )
                .build();
    }

    /**
     * 基础对话功能
     * @param message 用户提示词
     * @param chatId 对话id
     * @return ai提示词
     */
    public String doChat(String message,String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
//        log.info("content:{}",content);
        return content;
    }
    record LoveResponse(String title, List<String> suggestions){
    }

    /**
     * 结构化输出对话功能
     * @param message 用户提示词
     * @param chatId 对话id
     * @return ai提示词
     */
    public LoveResponse doEntityChat(String message, String chatId) {
        LoveResponse loveResponse = chatClient.prompt()
                .system(SYSTEM_PROMPT+"每次对话后都要生成恋爱结果，标题为{用户名}的恋爱报告，内容为建议列表")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId).param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 5))
                .call()
                .entity(LoveResponse.class);
        log.info("loveResponse：{}",loveResponse);
        return loveResponse;
    }
//    @Autowired
//    private VectorStore loveAppVectorStore;
    @Autowired
    private VectorStore pgVectorStore;
    /**
     * 使用本地向量数据库RAG
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithRag(String message, String chatId) {
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
//                // 应用知识库问答
//                .advisors(new QuestionAnswerAdvisor(pgVectorStore))
//                // 使用自定义检索器
//                .advisors(LoveAppRagCustomAdvisorFactory.createLoveAppRagCustomAdvisor(pgVectorStore,"单身"))
                .advisors(RetrievalAugmentationAdvisor.builder()
                        .documentRetriever(VectorStoreDocumentRetriever.builder()
                                .vectorStore(pgVectorStore)
                                .similarityThreshold(0.9) // 相似度阈值
                                .topK(3) // 返回文档数量
                                .build())
                        .queryAugmenter(LoveAppContextualQueryAugmenterFactory.createInstance())
                        .build()
                )

                .call()
                .chatResponse();
        String content = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


//    @Autowired
//    private Advisor loveAppRagCloudAdvisor;
//
//    /**
//     * 使用阿里云百炼知识库RAG
//     * @param message
//     * @param chatId
//     * @return
//     */
//    public String doChatWithRagCloud(String message, String chatId) {
//        ChatResponse chatResponse = chatClient
//                .prompt()
//                .user(message)
//                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
//                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
//                // 开启日志，便于观察效果
//                .advisors(new MyLoggerAdvisor())
//                // 应用增强检索服务（云知识库服务）
//                .advisors(loveAppRagCloudAdvisor)
//                .call()
//                .chatResponse();
//        String content = chatResponse.getResult().getOutput().getText();
//        log.info("content: {}", content);
//        return content;
//    }
    @Autowired
    private ToolCallback[] allTools;

    /**
     * 使用工具调用构建智能体
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithTools(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(allTools)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }
//
    @Autowired
    private ToolCallbackProvider toolCallbackProvider;

    /**
     * 使用Mcp服务构建智能体
     * @param message
     * @param chatId
     * @return
     */
    public String doChatWithMcp(String message,String chatId){
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                // 开启日志，便于观察效果
                .advisors(new MyLoggerAdvisor())
                .tools(toolCallbackProvider)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }


}
