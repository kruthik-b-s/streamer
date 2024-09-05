package com.projects.streamer.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.AmazonECSClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EcsConfig {

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secret}")
    private String secret;

    @Value("${aws.region}")
    private String region;

    @Bean
    public AmazonECS ecsClient() {
        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secret);
        return AmazonECSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .build();
    }

}
