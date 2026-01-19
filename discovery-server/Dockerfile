# discovery-server/Dockerfile (e similar para outros serviços)
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="your-email@example.com"
LABEL version="1.0.0"

# Create non-root user
RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

# Copy JAR
COPY target/*.jar app.jar

# Change ownership
RUN chown -R spring:spring /app

USER spring

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8761/actuator/health || exit 1

EXPOSE 8761

ENTRYPOINT ["java", "-jar", "app.jar"]