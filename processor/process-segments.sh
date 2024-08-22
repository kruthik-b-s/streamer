#!/bin/bash

# Define input and output paths
INPUT_VIDEO=$INPUT
OUTPUT_DIR=$OUTPUT

# Create output directory if it doesn't exist
mkdir -p $OUTPUT_DIR

# Check for audio streams
audio_stream=$(ffprobe -v error -select_streams a:0 -show_entries stream=index -of csv=p=0 $INPUT_VIDEO)

# If audio stream exists, set audio mapping and codec options
if [ -n "$audio_stream" ]; then
  audio_map="-map 0:a"
  audio_codec="-c:a aac -b:a 128k -ac 1"
  echo "Audio stream found, including audio in the output."
else
  audio_map=""
  audio_codec=""
  echo "No audio stream found. Processing video only."
fi

# Transcode to multiple resolutions and create HLS segments
ffmpeg -i $INPUT_VIDEO -filter_complex \
"[0:v]split=3[v1][v2][v3]; \
 [v1]scale=w=426:h=240[v1out]; \
 [v2]scale=w=854:h=480[v2out]; \
 [v3]scale=w=1280:h=720[v3out]" \
-map [v1out] $audio_map -c:v:0 libx264 -b:v:0 500k -preset fast -g 48 -sc_threshold 0 \
  $audio_codec -hls_time 10 -hls_playlist_type vod \
  -hls_segment_filename "$OUTPUT_DIR/240p_%03d.ts" "$OUTPUT_DIR/240p.m3u8" \
-map [v2out] $audio_map -c:v:1 libx264 -b:v:1 1000k -preset fast -g 48 -sc_threshold 0 \
  $audio_codec -hls_time 10 -hls_playlist_type vod \
  -hls_segment_filename "$OUTPUT_DIR/480p_%03d.ts" "$OUTPUT_DIR/480p.m3u8" \
-map [v3out] $audio_map -c:v:2 libx264 -b:v:2 2500k -preset fast -g 48 -sc_threshold 0 \
  $audio_codec -hls_time 10 -hls_playlist_type vod \
  -hls_segment_filename "$OUTPUT_DIR/720p_%03d.ts" "$OUTPUT_DIR/720p.m3u8"
