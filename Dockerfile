FROM amd64/amazoncorretto:17
WORKDIR /app
COPY ./build/libs/Tiki-server-0.0.1-SNAPSHOT.jar /app/Tiki.jar
CMD ["java", "-Duser.timezone=Asia/Seoul" ,"-jar", "-Dspring.profiles.active=dev","Tiki.jar"]