# cnam-api

## Package Helm Chart

```bash
./gradlew :cnam-api:quarkusBuild
helm package cnam-api/deployment/helm/chart/
```

## Push Helm Chart to Repository

```bash
export CLOUDSMITH_REPOSITORY=aptiway/cnam
export HELM_PACKAGE_VERSION=1.6.0
cloudsmith push helm $CLOUDSMITH_REPOSITORY cnam-api-$HELM_PACKAGE_VERSION.tgz
```

## Publish Docker Image

```bash
export QUARKUS_CONTAINER_IMAGE_PASSWORD=...
```

### Publish cnam-api Docker Image

```bash
./gradlew :cnam-api:imageBuild -Dquarkus.profile=prod,api,management-api,scheduled-job
```

### Publish cnam-management-api Docker Image

```bash
./gradlew :cnam-api:imageBuild -Dquarkus.profile=prod,management-api,management-api-image
```

### Publish cnam-scheduled-job Docker Image

```bash
./gradlew :cnam-api:imageBuild -Dquarkus.profile=prod,scheduled-job,scheduled-job-image
```

## Deploy

```bash
helm repo update
helm install cnam-api aptiway-cnam/cnam-api --version 1.6.0
```

## Upgrade

```bash
helm repo update
helm upgrade cnam-api aptiway-cnam/cnam-api --version 1.4.0
```
