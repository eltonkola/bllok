# bllok
bloke is a GitHub based blog


java -jar build/libs/bllok-1.0-SNAPSHOT.jar /Users/elton/Documents/git/bllok/template/ /Users/elton/Documents/git/bllok/docs/



# Hello world docker action

This action prints "Hello World" or "Hello" + the name of a person to greet to the log.

## Inputs

## `who-to-greet`

**Required** The name of the person to greet. Default `"World"`.

## Outputs

## `time`

The time we greeted you.

## Example usage

uses: actions/hello-world-docker-action@v1
with:
who-to-greet: 'Mona the Octocat'


