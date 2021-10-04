#!/bin/sh -l

bash java -jar /bin/runner/run.jar /src/template/ /src/docs/ "$1" "$2" "$3"
echo "::set-output name=bllok-status::bllok-status"
