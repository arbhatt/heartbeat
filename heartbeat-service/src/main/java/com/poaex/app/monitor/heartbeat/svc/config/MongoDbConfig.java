package com.poaex.app.monitor.heartbeat.svc.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class MongoDbConfig {

    @Value("${config-db.mongodb.database}")
    private String mongoConfigDb;

    @Value("${config-db.mongodb.userid}")
    private String mongoUserId;

    @Value("${config-db.mongodb.password}")
    private String mongoPassword;

    @Value("${config-db.mongodb.host}")
    private String mongohost;

    @Value("${config-db.mongodb.auth.database}")
    private String mongoAuthdb;

    @Value("${config-db.mongodb.port}")
    private Integer mongoport;

    @Bean
    public MongoTemplate mongoTemplate() throws UnknownHostException {

        ServerAddress serverAddress = new ServerAddress(mongohost, mongoport);

        List<MongoCredential> mongoCredentials = new ArrayList<>(1);
        mongoCredentials.add(MongoCredential.createScramSha1Credential(mongoUserId, mongoAuthdb, mongoPassword.toCharArray()));

        MongoClientOptions options = MongoClientOptions.builder()
                .applicationName("hb")
                .maxConnectionIdleTime(5000)
                .minConnectionsPerHost(2)
                .threadsAllowedToBlockForConnectionMultiplier(10)
                .connectionsPerHost(50).build();

        MongoClient mongoClient = new MongoClient(serverAddress, mongoCredentials, options);
        SimpleMongoDbFactory simpleMongoDbFactory = new FileServerMongodbFactory(mongoClient, mongoConfigDb);

        return new MongoTemplate(simpleMongoDbFactory);
    }

    private class FileServerMongodbFactory extends SimpleMongoDbFactory {
        private String mongoDB;

        public FileServerMongodbFactory(MongoClient mongoClient, String databaseName) {
            super(mongoClient, databaseName);
            mongoDB = databaseName;
        }
    }

}