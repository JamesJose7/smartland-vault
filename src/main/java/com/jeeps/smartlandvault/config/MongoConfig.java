package com.jeeps.smartlandvault.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.jeeps.smartlandvault.nosql_model")
public class MongoConfig {
    @Value("${smartlandvault.nosql.db}")
    private String dbName;
    @Value("${smartlandvault.nosql.host}")
    private String host;
    @Value("${smartlandvault.nosql.port}")
    private int port;

    @Bean
    public MongoClient mongo() {
        return new MongoClient(host, port);
    }

    @Bean
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), dbName);
    }
}
