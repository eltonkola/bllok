FROM openjdk:11-jdk

COPY . /src
WORKDIR /src
RUN ./gradlew build

ENTRYPOINT [ "entrypoint.sh" ]
