ARG githubToken
ARG githubOwner
ARG githubRepo

# Container image that runs your code
FROM openjdk:11-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew --no-daemon build

FROM openjdk:11-jre

COPY --from=BUILD build/libs/bllok-1.0-SNAPSHOT.jar /bin/runner/run.jar
WORKDIR /bin/runner

CMD ["java","-jar","run.jar", "./template/", "./docs/", ${githubToken}, ${githubOwner}, ${githubRepo}]
#echo "::set-output name=bllok-status::bllok-status"
