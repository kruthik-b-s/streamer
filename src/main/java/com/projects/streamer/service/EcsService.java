package com.projects.streamer.service;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EcsService {

    @Value("${s3.accessKey}")
    private String accessKey;

    @Value("${s3.secret}")
    private String secret;

    @Value("${aws.region}")
    private String region;

    @Value("${ecs.object.type}")
    private String objectType;

    @Value("${ecs.task.definition.container.name}")
    private String containerName;

    @Value("${ecs.task.definition.env.key}")
    private String inputEnvKey;

    @Value("${ecs.cluster.name}")
    private String clusterName;

    @Value("${ecs.task.definition}")
    private String taskDefinition;

    @Value("${ecs.subnets}")
    private String[] subnets;

    @Value("${ecs.security.groups}")
    private String[] securityGroups;

    @Autowired
    private AmazonECS amazonECS;

    public void handleContainer(String videoFileName) {
        log.info("Setting up the container override with commands and environment variables");
        ContainerOverride containerOverride = new ContainerOverride()
                .withName(containerName)
                .withCommand(accessKey, secret, region, objectType)
                .withEnvironment(new KeyValuePair().withName(inputEnvKey).withValue(videoFileName));

        log.info("Overriding the task definition with the container overrides");
        TaskOverride taskOverride = new TaskOverride()
                .withContainerOverrides(containerOverride);

        log.info("Initiating the run task request with VPC network configuration");
        RunTaskRequest runTaskRequest = new RunTaskRequest()
                .withCluster(clusterName)
                .withTaskDefinition(taskDefinition)
                .withLaunchType(LaunchType.FARGATE)
                .withOverrides(taskOverride)
                .withNetworkConfiguration(new NetworkConfiguration()
                        .withAwsvpcConfiguration(new AwsVpcConfiguration()
                                .withSubnets(subnets)
                                .withSecurityGroups(securityGroups)
                                .withAssignPublicIp(AssignPublicIp.ENABLED)));

        RunTaskResult runTaskResult = amazonECS.runTask(runTaskRequest);
        log.info("Started the ECS task with ARN: {}", runTaskResult.getTasks().getFirst().getTaskArn());
        // TODO: return some useful data
        System.out.println("Task ARN: " + runTaskResult.getTasks().getFirst().getTaskArn());
    }

}
