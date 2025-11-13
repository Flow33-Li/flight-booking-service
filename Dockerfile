## Stage 1: Build the application
FROM registry.access.redhat.com/ubi8/openjdk-17:1.18 AS build

USER root
WORKDIR /build

# Copy Maven wrapper and pom.xml
COPY --chown=185 mvnw .
COPY --chown=185 .mvn .mvn
COPY --chown=185 pom.xml .

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY --chown=185 src src

# Build application with memory limits
RUN ./mvnw package -DskipTests -Dmaven.test.skip=true \
    -Dquarkus.package.type=fast-jar \
    && ls -la target/

## Stage 2: Create the runtime image
FROM registry.access.redhat.com/ubi8/openjdk-17-runtime:1.18

ENV LANGUAGE='en_US:en'

# We make four distinct layers so if there are application changes the library layers can be re-used
COPY --from=build --chown=185 /build/target/quarkus-app/lib/ /deployments/lib/
COPY --from=build --chown=185 /build/target/quarkus-app/*.jar /deployments/
COPY --from=build --chown=185 /build/target/quarkus-app/app/ /deployments/app/
COPY --from=build --chown=185 /build/target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185
ENV JAVA_OPTS="-Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager"
ENV JAVA_APP_JAR="/deployments/quarkus-run.jar"

