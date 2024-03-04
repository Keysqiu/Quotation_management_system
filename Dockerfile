FROM openjdk:8-jdk
COPY target/*.jar app.jar
EXPOSE 80
ENTRYPOINT ["java","-jar","app.jar"]