package com.chenyuxin.xinaiagent;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@SpringBootApplication
public class XinAiAgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(XinAiAgentApplication.class, args);
    }

}
