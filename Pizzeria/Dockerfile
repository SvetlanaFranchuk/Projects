FROM node:18 as frontend-builder
WORKDIR /app
COPY front/package.json front/package-lock.json ./
RUN npm install
COPY front .
RUN npm run build

FROM maven:3.9.5-eclipse-temurin-21 as backend-builder
WORKDIR /opt/app
COPY mvnw pom.xml ./
COPY src_back ./src
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jdk-alpine
WORKDIR /opt/app
EXPOSE 8095
COPY --from=frontend-builder /app/build/ /opt/app/front/static/
COPY --from=backend-builder /opt/app/target/*.jar /opt/app/app.jar

ENTRYPOINT ["java", "-jar", "/opt/app/app.jar"]