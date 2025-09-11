# FROM alpine:3.19

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

FROM amazoncorretto:21-alpine3.19-jdk

WORKDIR /app

COPY target/rinha-0.0.1-SNAPSHOT.jar app.jar

RUN apk add --no-cache libc6-compat && \
    echo 'net.core.somaxconn=65535' >> /etc/sysctl.conf && \
    echo 'net.ipv4.tcp_tw_reuse=1' >> /etc/sysctl.conf

EXPOSE 8080

ENTRYPOINT ["java", \
   "-server", \
   "-Xms128m", "-Xmx128m", \
   "-XX:+UseG1GC", \
   "-XX:MaxGCPauseMillis=1", \
   "-XX:G1HeapRegionSize=8m", \
   "-XX:+G1UseAdaptiveIHOP", \
   "-XX:G1MixedGCCountTarget=8", \
   "-XX:+TieredCompilation", \
   "-XX:TieredStopAtLevel=1", \
   "-XX:CompileThreshold=1", \
   "-XX:+AlwaysPreTouch", \
   "-XX:+UseStringDeduplication", \
   "-XX:+ExitOnOutOfMemoryError", \
   "-XX:+UseCompressedOops", \
   "-XX:+UseCompressedClassPointers", \
   "-XX:+DisableExplicitGC", \
   "-XX:MaxInlineLevel=15", \
   "-XX:MaxTrivialSize=12", \
   "-XX:InlineSmallCode=2000", \
   "-XX:MaxInlineSize=70", \
   "-XX:FreqInlineSize=325", \
   "-XX:+UnlockDiagnosticVMOptions", \
   "-Djava.security.egd=file:/dev/./urandom", \
   "-Djava.awt.headless=true", \
   "-Dspring.backgroundpreinitializer.ignore=true", \
   "-Dspring.output.ansi.enabled=never", \
   "-Djava.net.preferIPv4Stack=true", \
   "-Dfile.encoding=UTF-8", \
   "-jar", "app.jar"]
