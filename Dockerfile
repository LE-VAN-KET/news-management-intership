# Specify base image
FROM maven:3.6.3-jdk-11

# Maintainer information
LABEL maintainer="anhle1512001@gmail.com"

# Create an application directory
RUN mkdir -p /app

# The /app directory should act as the main application directory
WORKDIR /app

# Copy or project directory (locally) in the current directory of our docker image (/app)
COPY ./target/backend-springboot-web-article.jar .

EXPOSE 8080
# Set environment code UTF-8
ENV LANG C.UTF-8
# Run - configures the container to be executable
ENTRYPOINT ["java", "-jar", "backend-springboot-web-article.jar"]
