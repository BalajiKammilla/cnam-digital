# cnam-api

## Configuration

The following table lists the configurable parameters and their default values.

| Parameter | Description | Default                                            |
|  ---  |  ---  |----------------------------------------------------|
| `app.envs.QUARKUS_DATASOURCE_JDBC_URL` |   | datasource_jdbc_url                                |
| `app.envs.QUARKUS_DATASOURCE_PASSWORD` |   | datasource_password                                |
| `app.envs.QUARKUS_DATASOURCE_USERNAME` |   | datasource_username                                |
| `app.envs.QUARKUS_LIQUIBASE_ENABLED` |   | false                                              |
| `app.envs.QUARKUS_MINIO_ACCESS_KEY` |   | minio_access_key                                   |
| `app.envs.QUARKUS_MINIO_SECRET_KEY` |   | minio_secret_key                                   |
| `app.envs.QUARKUS_MINIO_URL` |   | minio_url                                          |
| `app.host` | The host under which the application is going to be exposed. | mobis.ipscnam.ci                                   |
| `app.image` | The container image to use. | registry.hub.docker.com/aptiway/cnam-api:<version> |
| `app.serviceType` | The service type to use. | ClusterIP                                          |

Specify each parameter using the `--set key=value[,key=value]` argument to `helm install`.
Alternatively, a YAML file that specifies the values for the above parameters can be provided while installing the chart. For example,
```
$ helm install --name chart-name -f values.yaml .
```
> **Tip**: You can use the default [values.yaml](values.yaml)
