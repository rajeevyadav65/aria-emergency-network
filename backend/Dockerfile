# build stage
FROM maven:3.8.8-openjdk-17 AS build
WORKDIR /app

# copy only what we need to leverage layer caching
COPY pom.xml .
COPY src ./src

# build the app (skip tests in CI to speed up build; remove -DskipTests if you want tests)
RUN mvn -B -DskipTests package

# runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

# copy the fat jar
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_OPTS=""
EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar /app/app.jar"]
