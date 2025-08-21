package com.chenyuxin.xinaiagent.tools;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class WebScrapingToolTest {

    @Test
    public void testScrapeWebPage() {
        WebScrapingTool tool = new WebScrapingTool();
        String url = "http://code-life.chenyuxin0328.cn/";
        String result = tool.scrapeWebPage(url);
        assertNotNull(result);
    }
}
