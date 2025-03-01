FROM gradle:jdk21 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
COPY settings.gradle.kts ./settings.gradle.kts
RUN gradle clean installDist

FROM eclipse-temurin as backend
WORKDIR /root
COPY test-session ./app/test-session/
RUN apt-get update && apt-get install -y libssl-dev

COPY --from=builder /project/build/install/LuminBot/ ./app/
WORKDIR /root/app/
ENTRYPOINT ["bin/LuminBot"]