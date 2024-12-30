#! /usr/bin/env bash

while getopts v: options; do
  case $options in
    v) VERSION=$OPTARG
  esac
done

if [[ -z "${VERSION}" ]]; then
  echo "Version not set"
  exit 1
else
  echo "Launching Version: $VERSION"
fi

IMAGE=aptiway/cnam-api:"$VERSION"

docker stop cnam-api
docker rm cnam-api
docker pull "$IMAGE"

docker run -d \
 -p 9191:9191 \
 --name cnam-api \
 -v /home/cnam/object-store:/home/jboss/object-store \
 -e QUARKUS_DATASOURCE_JDBC_URL=jdbc:postgresql://172.17.0.1:5432/cnam \
 -e QUARKUS_DATASOURCE_USERNAME=cnam \
 -e QUARKUS_DATASOURCE_PASSWORD=q9GB7M^hLfzTHZk \
 -e QUARKUS_MINIO_URL= \
 -e QUARKUS_MINIO_ACCESS_KEY= \
 -e QUARKUS_MINIO_SECRET_KEY= \
 "$IMAGE"
