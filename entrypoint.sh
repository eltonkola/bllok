#!/bin/sh -l

bash java -jar /src/build/libs/bllok-1.0-SNAPSHOT.jar /src/template/ /src/docs/ "$1" "$2" "$3"
echo "::set-output name=bllok-status::bllok-status"
