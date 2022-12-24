FROM openjdk:17 as builder
WORKDIR /app
COPY . .
RUN microdnf install findutils
RUN ./gradlew build
COPY build/libs/*.jar app.jar

FROM openjdk:17
COPY --from=builder /app/app.jar .
ENTRYPOINT ["java", "-jar", "/app.jar"]