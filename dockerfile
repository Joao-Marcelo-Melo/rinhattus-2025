# FROM alpine:3.19
#
# RUN apk add --no-cache libc6-compat
#
# WORKDIR /app
#
# COPY target/rinha /app/rinha
#
# RUN chmod +x /app/rinha
#
# EXPOSE 8080
#
# ENTRYPOINT ["./rinha"]

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY target/rinha-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
   "-Xms32m", "-Xmx64m", \
   "-XX:+UseSerialGC", \
   "-XX:+AlwaysPreTouch", \
   "-XX:+ExitOnOutOfMemoryError", \
   "-jar", "app.jar"]
