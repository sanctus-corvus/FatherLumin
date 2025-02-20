FROM gradle:jdk21 as builder
WORKDIR /project
COPY src ./src
COPY build.gradle.kts ./build.gradle.kts
COPY settings.gradle.kts ./settings.gradle.kts
RUN gradle clean installDist

FROM eclipse-temurin as backend
WORKDIR /root
COPY --from=builder /project/build/install/LuminBot/ ./app/
WORKDIR /root/app/
ENTRYPOINT ["bin/LuminBot"]