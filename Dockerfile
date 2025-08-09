FROM openjdk:21-jdk-slim

LABEL maintainer="Ehtishamul Hassan"
LABEL version="1.0"
LABEL description="this is the springboot app with database"

WORKDIR /app

# COPY target/*.jar app.jar
COPY app.jar app.jar

EXPOSE 8080

# CMD ["java" , "-jar", "app.jar"]
ENTRYPOINT ["sh", "-c", "java -Dspring.datasource.url=$DB_URL -Dspring.datasource.username=$DB_USER -Dspring.datasource.password=$DB_PASS -Dspring.datasource.driver-class-name=$DB_DRIVER -jar app.jar"]


