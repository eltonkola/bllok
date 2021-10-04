FROM openjdk:11-jdk

COPY . /src
WORKDIR /src
RUN ./gradlew build

COPY /src/build/libs/bllok-1.0-SNAPSHOT.jar /bin/runner/run.jar
WORKDIR /bin/runner

ENTRYPOINT [ "entrypoint.sh" ]
