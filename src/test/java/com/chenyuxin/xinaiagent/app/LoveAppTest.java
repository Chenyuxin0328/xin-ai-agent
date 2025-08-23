package com.chenyuxin.xinaiagent.app;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author YuXin.Dev
 * @Date: 2025/7/13 18:39
 */
@SpringBootTest
class LoveAppTest {
    @Autowired
    private LoveApp loveApp;

    @Test
    void testChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "你好我是陈宇新";
        String answer = loveApp.doChat(message,chatId);
        Assertions.assertNotNull(answer);
        System.out.println("-----------------------------------------------");
        message = "我是谁";
        loveApp.doChat(message,chatId);
        System.out.println("-----------------------------------------------");
        message = "请告诉我，我的名字";
        loveApp.doChat(message,chatId);
        System.out.println("-----------------------------------------------");


    }

    @Test
    void doEntityChat() {
        String chatId = UUID.randomUUID().toString();
        String message = "我总是感觉被对方吊着";
        LoveApp.LoveResponse loveResponse = loveApp.doEntityChat(message, chatId);
        Assertions.assertNotNull(loveResponse);
    }

    @Test
    void doChatWithRag() {
        String content = loveApp.doChatWithRag("我已经结婚了，但是婚后夫妻生活不亲密怎么办？", UUID.randomUUID().toString());
        System.out.println("content:"+content);
    }

//    @Test
//    void doChatWithRagCloud() {
//        String content = loveApp.doChatWithRagCloud("我已经结婚了，但是婚后夫妻生活不亲密怎么办？", UUID.randomUUID().toString());
//        System.out.println("content:"+content);
//    }
    @Test
    void doChatWithTools() {
        // 测试联网搜索问题的答案
        testMessage("周末想带女朋友去上海约会，推荐几个适合情侣的小众打卡地？");

        // 测试网页抓取：恋爱案例分析
        testMessage("最近和对象吵架了，看看编程导航网站（codefather.cn）的其他情侣是怎么解决矛盾的？");

        // 测试资源下载：图片下载
        testMessage("直接下载一张适合做手机壁纸的星空情侣图片为文件");

        // 测试终端操作：执行代码
        testMessage("执行 Python3 脚本来生成数据分析报告");

        // 测试文件操作：保存用户档案
        testMessage("保存我的恋爱档案为文件");

        // 测试 PDF 生成
        testMessage("生成一份‘七夕约会计划’PDF，包含餐厅预订、活动流程和礼物清单");

    }

    private void testMessage(String message) {
        String chatId = UUID.randomUUID().toString();
        String answer = loveApp.doChatWithTools(message, chatId);
        Assertions.assertNotNull(answer);
    }

    @Test
    void doChatWithMcp() {
        String chatId = UUID.randomUUID().toString();
//        String message = "我现在在厦门市前埔南区小学，帮我看看附近有没有合适的约会地点？";
//        String answer = loveApp.doChatWithMcp(message, chatId);
        // 测试图片搜索 MCP
        String message = "帮我搜索一些哄另一半开心的图片";
        String answer =  loveApp.doChatWithMcp(message, chatId);
        Assertions.assertNotNull(answer);
    }

}