
# Mixtape API

## Building instructions

- First, you need Java 17. Get that downloaded first.

Once you're using java 17...

1. Run `./gradlew :bootBuildImage`

This command builds a docker image out of the main API. This creates an image called `mixtape-api:0.0.1-SNAPSHOT`
locally on your machine.

2. Run `./gradlew :spotify-auth-server:bootBuildImage`

This command does the same as above, but for the spotify auth server. This builds an image called `spotify-auth-server:0.0.1-SNAPSHOT`

3. Run `cd reverse-proxy && docker build -t mixtape-proxy:0.0.1 .`

This builds the reverse proxy. Makes an image called `mixtape-proxy:0.0.1`

4. Run `docker stack deploy -c docker-compose.yml mixtape`

Finally, this runs all the images together in one nice container

## Access the API
Make requests to `api.localhost:8081/`. For example: `api.localhost:8081/v1/profile/me` to get the current profile for
the logged in user.

Use an oauth client to log in.

## Stopping the API
Run `docker stack rm mixtape`.