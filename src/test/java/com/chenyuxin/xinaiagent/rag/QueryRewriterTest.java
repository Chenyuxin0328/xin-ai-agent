package com.chenyuxin.xinaiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author YuXin.Dev
 * @Date: 2025/8/15 14:16
 */
@SpringBootTest
class QueryRewriterTest {
    @Resource
    private QueryRewriter queryRewriter;
    @Test
    void test(){
        String message = "我叫陈宇新，我是今年刚上小学，请问前埔南区小学在哪里？";
        String rewrittenMessage = queryRewriter.doQueryRewrite(message);
        System.out.println(rewrittenMessage);
    }
}