package com.projects.streamer.service;

import com.amazonaws.services.ecs.AmazonECS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EcsService {

    @Autowired
    private AmazonECS amazonECS;

}
