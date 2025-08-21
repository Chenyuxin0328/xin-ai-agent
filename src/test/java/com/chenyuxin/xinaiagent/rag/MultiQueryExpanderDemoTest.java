package com.chenyuxin.xinaiagent.rag;

import org.junit.jupiter.api.Test;
import org.springframework.ai.rag.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author YuXin.Dev
 * @Date: 2025/8/15 14:07
 */
@SpringBootTest
class MultiQueryExpanderDemoTest {
    @Autowired
    private MultiQueryExpanderDemo multiQueryExpanderDemo;
    @Test
    void test(){
        List<Query> expand = multiQueryExpanderDemo.expand("陈宇新是谁？");
        System.out.println(expand);
    }

}