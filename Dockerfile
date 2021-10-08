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

WORKDIR /app

COPY --from=gradleimage /home/gradle/source/build/libs/bllok-1.0-SNAPSHOT.jar /app/bllok.jar

COPY entrypoint.sh /entrypoint.sh
RUN ["chmod", "+x", "/entrypoint.sh"]
# Code file to execute when the docker container starts up (`entrypoint.sh`)
ENTRYPOINT ["/entrypoint.sh"]
