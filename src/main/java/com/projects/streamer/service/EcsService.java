package com.projects.streamer.service;

import com.amazonaws.services.ecs.AmazonECS;
import com.amazonaws.services.ecs.model.*;
import com.amazonaws.services.logs.AWSLogs;
import com.amazonaws.services.logs.model.GetLogEventsRequest;
import com.amazonaws.services.logs.model.GetLogEventsResult;
import com.amazonaws.services.logs.model.OutputLogEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import java.util.List;

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

    @Autowired
    private AWSLogs logsClient;

    public String handleContainer(String videoFileName) {
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
        String taskArn = runTaskResult.getTasks().getFirst().getTaskArn();
        log.info("Started the ECS task with ARN: {}", taskArn);
        return taskArn;
    }

    public Flux<String> streamLogs(String taskArn) {
        String logGroupName = "/ecs/" + taskDefinition;
        String logStreamName = getLogStreamName(taskArn);

        return Flux.create(sink -> {
            GetLogEventsRequest logEventsRequest = new GetLogEventsRequest()
                    .withLogGroupName(logGroupName)
                    .withLogStreamName(logStreamName);

            GetLogEventsResult logEventsResult = logsClient.getLogEvents(logEventsRequest);
            List<OutputLogEvent> events = logEventsResult.getEvents();

            for (OutputLogEvent event : events) {
                sink.next(event.getMessage());
            }

            log.info("Flushed the log to response");
            sink.complete();
        });
    }

    private String getLogStreamName(String taskArn) {
        return "ecs/" + containerName + "/" + taskArn.substring(taskArn.lastIndexOf('/') + 1);
    }

}
