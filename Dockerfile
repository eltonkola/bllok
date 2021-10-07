#FROM openjdk:11-jdk
#
#COPY . /bllok
#WORKDIR /bllok
#RUN ./gradlew build
#COPY /bllok/build/libs/bllok-1.0-SNAPSHOT.jar bllok.jar
#ENTRYPOINT [ "./entrypoint.sh" ]


FROM gradle:jdk11 as gradleimage
COPY . /home/gradle/source
WORKDIR /home/gradle/source
RUN gradle build

FROM openjdk:11-jre-slim
COPY --from=gradleimage /home/gradle/source/build/libs/bllok-1.0-SNAPSHOT.jar /app/bllok.jar
COPY --from=gradleimage /home/gradle/source/entrypoint.sh /app/entrypoint.sh
WORKDIR /app
ENTRYPOINT [ "entrypoint.sh" ]
