package com.projects.streamer.config;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EcsConfig {

    @Value("${ecs.region}")
    private String region;

    @Bean
    public AmazonECS ecsClient() {
        return AmazonECSClient.builder()
                .withRegion(region)
                .build();
    }

}
