FROM gradle:jdk11 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build

FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=gradleimage /home/gradle/source/build/libs/bllok-1.0-SNAPSHOT.jar /app/bllok.jar
COPY --from=gradleimage /home/gradle/source/template  /app/template
#COPY entrypoint.sh /entrypoint.sh
#RUN ["chmod", "+x", "/entrypoint.sh"]
#ENTRYPOINT ["/entrypoint.sh"]
ENTRYPOINT ["java","-jar", "/app/bllok.jar", "/app/template/", "/app/docs/", "$1", "$2", "$3"]
