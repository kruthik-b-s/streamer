#!/bin/bash

# Define input and output paths
VIDEO_NAME=$VIDEO_NAME

AWS_ACCESS_KEY_ID=$1
AWS_SECRET_ACCESS_KEY=$2
AWS_REGION=$3
AWS_OUTPUT=$4

# Set AWS credentials and default region
aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
aws configure set default.region "$AWS_REGION"
aws configure set default.output "$AWS_OUTPUT"

# Download the script from S3
aws s3 cp s3://java-streamer/uploads/$VIDEO_NAME /app/$VIDEO_NAME

INPUT_PATH="/app/$VIDEO_NAME"

# Create an output folder
mkdir /app/segments

OUTPUT_PATH="/app/segments/"

# Check for audio streams
audio_stream=$(ffprobe -v error -select_streams a:0 -show_entries stream=index -of csv=p=0 $INPUT_PATH)

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
ffmpeg -i $INPUT_PATH -filter_complex \
"[0:v]split=3[v1][v2][v3]; \
 [v1]scale=w=426:h=240[v1out]; \
 [v2]scale=w=854:h=480[v2out]; \
 [v3]scale=w=1280:h=720[v3out]" \
-map [v1out] $audio_map -c:v:0 libx264 -b:v:0 500k -preset fast -g 48 -sc_threshold 0 \
  $audio_codec -hls_time 10 -hls_playlist_type vod \
  -hls_segment_filename "$OUTPUT_PATH/240p_%03d.ts" "$OUTPUT_PATH/240p.m3u8" \
-map [v2out] $audio_map -c:v:1 libx264 -b:v:1 1000k -preset fast -g 48 -sc_threshold 0 \
  $audio_codec -hls_time 10 -hls_playlist_type vod \
  -hls_segment_filename "$OUTPUT_PATH/480p_%03d.ts" "$OUTPUT_PATH/480p.m3u8" \
-map [v3out] $audio_map -c:v:2 libx264 -b:v:2 2500k -preset fast -g 48 -sc_threshold 0 \
  $audio_codec -hls_time 10 -hls_playlist_type vod \
  -hls_segment_filename "$OUTPUT_PATH/720p_%03d.ts" "$OUTPUT_PATH/720p.m3u8"

  # Upload the segments to S3
  aws s3 cp /app/segments s3://java-streamer/segments/$VIDEO_NAME --recursive
