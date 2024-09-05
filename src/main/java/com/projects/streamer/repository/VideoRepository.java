package com.projects.streamer.repository;

import com.projects.streamer.entity.Video;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface VideoRepository extends JpaRepository<Video, Long> { }
