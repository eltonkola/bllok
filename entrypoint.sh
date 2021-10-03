#!/bin/sh -l

echo "Running bllok with params $1 $2 $3"
./gradlew build
java -jar build/libs/bllok-1.0-SNAPSHOT.jar ./template/ ./docs/ $2 $3 $1
bllok-status=$(date)
echo "::set-output name=bllok-status::bllok-status"
