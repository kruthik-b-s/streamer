package com.projects.streamer.service;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EcsService {

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secret}")
    private String secret;

    @Value("${s3.region}")
    private String region;

    @Autowired
    private AmazonECS amazonECS;

    public void runContainer(String videoFileName) {
        ContainerOverride containerOverride = new ContainerOverride()
                .withName("video-segmentation")
                .withCommand(accessKey, secret, region, "json")
                .withEnvironment(new KeyValuePair().withName("VIDEO_NAME").withValue(videoFileName));

        TaskOverride taskOverride = new TaskOverride()
                .withContainerOverrides(containerOverride);

        RunTaskRequest runTaskRequest = new RunTaskRequest()
                .withCluster("")
                .withTaskDefinition("")
                .withLaunchType("") // LaunchType.EC2
                .withOverrides(taskOverride);

        RunTaskResult runTaskResult = amazonECS.runTask(runTaskRequest);

        System.out.println("Task ARN: " + runTaskResult.getTasks().getFirst().getTaskArn());
    }

}
