FROM amazoncorretto:21

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} seulmaetest.jar

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/seulmaetest.jar"]
