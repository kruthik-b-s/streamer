# Use a base image with FFmpeg installed
FROM jrottenberg/ffmpeg:latest

# Set the working directory inside the container
WORKDIR /app

# Copy the shell script into the container
COPY process-segments.sh /app/process-segments.sh

# Make the shell script executable
RUN chmod +x /app/process-segments.sh

# Set the entrypoint to run the shell script
ENTRYPOINT ["/app/process-segments.sh"]