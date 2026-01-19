# order-service/Dockerfile
FROM eclipse-temurin:21-jre-alpine

LABEL maintainer="your-email@example.com"
LABEL version="1.0.0"

RUN addgroup -S spring && adduser -S spring -G spring

WORKDIR /app

COPY target/*.jar app.jar

RUN chown -R spring:spring /app

USER spring

HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8083/actuator/health || exit 1

EXPOSE 8083

ENTRYPOINT ["java", "-jar", "app.jar"]