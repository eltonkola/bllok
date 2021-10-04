#!/bin/sh -l

bash java -jar run.jar /src/template/ /src/docs/ "$1" "$2" "$3"
echo "::set-output name=bllok-status::bllok-status"