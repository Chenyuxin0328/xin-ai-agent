package com.chenyuxin.xinaiagent.config;

import com.chenyuxin.xinaiagent.rag.LoveAppDocumentLoader;
import com.chenyuxin.xinaiagent.rag.MyKeywordEnricher;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author YuXin.Dev
 * @Date: 2025/8/14 05:36
 */
@SpringBootTest
class PgVectorStoreConfigTest {
    @Autowired
    private VectorStore pgVectorStore;

    @Test
    void pgVectorStoreTest(){
        List<Document> documents = List.of(
                new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
                new Document("The World is Big and Salvation Lurks Around the Corner"),
                new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));

        // Add the documents to PGVector
        pgVectorStore.add(documents);

        // Retrieve documents similar to a query
        List<Document> results = this.pgVectorStore.similaritySearch(SearchRequest.builder().query("Spring").topK(5).build());
        System.out.println(results);
    }
    @Resource
    private LoveAppDocumentLoader loveAppDocumentLoader;
    
    @Resource
    private EmbeddingModel dashscopeEmbeddingModel;

    @Resource
    private MyKeywordEnricher myKeywordEnricher;
    @Test
    void pgVectorStoreDataInit(){
        // 加载文档
        List<Document> documents = loveAppDocumentLoader.loadMarkdowns();
        List<Document> enrichDocuments = myKeywordEnricher.enrichDocuments(documents);
        pgVectorStore.add(enrichDocuments);
    }

}
