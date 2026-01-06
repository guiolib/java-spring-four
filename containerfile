FROM docker.io/library/maven:3.9.12-amazoncorretto-25-debian-trixie as builder

WORKDIR /opt/app
#  RUN addgroup -S spring && adduser -S spring -G spring #alpine
# RUN addgroup --system spring 
# RUN adduser --system spring
# RUN adduser spring spring
USER root:root

COPY ./pom.xml .
# COPY ./target ./target
COPY ./src ./src
COPY ./mvnw .
COPY ./.mvn/ ./.mvn

RUN ./mvnw package

FROM docker.io/library/eclipse-temurin:25-jre-alpine as runner
# FROM docker.io/library/eclipse-temurin:25 as runner

WORKDIR /opt/app
# RUN addgroup -S spring && adduser -S spring -G spring #alpine
USER root:root

COPY --from=builder /opt/app/target/app.jar .

EXPOSE 8080

RUN /opt/java/openjdk/bin/jar -tf ./app.jar

CMD ["java", "-jar", "app.jar"]
