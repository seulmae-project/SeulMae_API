FROM amazoncorretto:21

ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} seulmaetest.jar

COPY src/main/resources/firebase/ /home/rocky/seulmae/resources/firebase/

ENTRYPOINT ["java", "-Dspring.profiles.active=dev", "-jar", "/seulmaetest.jar"]
