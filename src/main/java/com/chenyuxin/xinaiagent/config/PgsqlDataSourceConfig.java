package com.chenyuxin.xinaiagent.config;

import javax.sql.DataSource;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgDistanceType.COSINE_DISTANCE;
import static org.springframework.ai.vectorstore.pgvector.PgVectorStore.PgIndexType.HNSW;

@Configuration
public class PgsqlDataSourceConfig {

    @Bean(name = "pgsqlDataSource")
    @ConfigurationProperties(prefix = "spring.datasource.pgsql")
    public DataSource pgsqlDataSource() {
        return DataSourceBuilder.create().build();
    }
    @Bean(name = "pgsqlJdbcTemplate")
    public JdbcTemplate pgsqlJdbcTemplate(@Qualifier("pgsqlDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

}