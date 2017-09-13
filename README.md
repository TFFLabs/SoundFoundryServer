# SoundFoundryServer
Backend for the sound foundry application.

## General description.

This application provides the backend operations necessary for the SoundFoundry application to properly work.

The application offers a set of endpoints as well as a websocket necessary to keep all the listeners no the same run syncronized.

## Requirements.

1. You must have a local instance of mongoDB runing in your local environment or:

`docker run --name mongo -d -p27017:27017 mongo:3.4`

## Run in dev with:

`java -jar .\target\soundfoundry-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev`
