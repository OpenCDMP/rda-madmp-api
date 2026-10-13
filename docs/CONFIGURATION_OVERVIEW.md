# Configuration Overview

This section provides an overview of the main configuration components and their roles within the system.

---

## Backend Service Configuration

Here you can view all available configuration options for main app backend service.

#### Integration Configuration

| Env Variable          | Description                                              | Required | Default Value | Example                                                 |
|-----------------------|----------------------------------------------------------|----------|---------------|---------------------------------------------------------|
| `RDA_MADMP_APPLY_INTEGRATION_ACTION`      | Apply  `action type` feature to the API. If is set to `true`, GET requests from this API contains only dmps that come from external sources. For more details check [Integration Overview](INTEGRATION_OVERVIEW.md) and [Model Overview](MODEL_OVERVIEW.md).                 | No      |  `false`            | `true` |

#### Authentication & Security

| Env Variable          | Description                                              | Required | Default Value | Example                                                 |
|-----------------------|----------------------------------------------------------|----------|---------------|---------------------------------------------------------|
| `IDP_ISSUER_URI`      | Keycloak realm URL string.                               | Yes      | -             | `http://<keycloak-hostname>:<port>/realms/<realm-name>` |
| `IDP_CLAIMS_ROLES_PATH`   | Keycloak API client roles path. | Yes      | -             | `<keycloak_client>.roles`                               |
| `SECURITY_AUDIENCE`   | Keycloak API client scope that security is intended for. | Yes      | -             | `<keycloak-client-scope>`                               |
| `CORS_ALLOWED_ORIGIN` | String that contains allowed origin for this server.     | Yes      | -             | `http://<hostname>:<port>`                              |

#### Database Configuration

| Env Variable           | Description                 | Required | Default Value | Example                                                                  |
|------------------------|-----------------------------|----------|---------------|--------------------------------------------------------------------------|
| `DB_CONNECTION_STRING` | Database connection string. | Yes      | -             | `jdbc:postgresql://<madmp-api-postgres-hostname>:<port>/<database-name>` | 
| `DB_USER`              | Database username string.   | Yes      | -             | `<madmp-api-user>`                                                       |
| `DB_PASSWORD`          | Database password string.   | Yes      | -             | `<madmp-api-password>`                                                   |

#### Elasticsearch Configuration

| Env Variable        | Description                          | Required | Default Value | Example                        |
|---------------------|--------------------------------------|----------|---------------|--------------------------------|
| `ELASTIC_HOST`      | URI that contains elastic host.      | Yes      | -             | `<elastic-host>:<port>`        |
| `ELASTIC_USE_SSL`   | Boolean value to enable Elastic SSL. | No       | `false`       | `true`                         |
| `ELASTIC_USER`      | Elastic username string.             | Yes      | -             | `<madmp-api-elastic-user>`     |
| `ELASTIC_PASS`      | Elastic password string.             | Yes      | -             | `<madmp-api-elastic-password>` |
| `ELASTIC_RDA_INDEX` | Elastic index name for rda.          | Yes      | -             | `<madmp-api-rda-index>`        |


#### RabbitMQ Configuration

| Env Variable     | Description                                                  | Required | Default Value | Example                      |
|------------------|--------------------------------------------------------------|----------|---------------|------------------------------|
| `QUEUE_APP_ID`   | [RabbitMQ](https://www.rabbitmq.com/) application id string. | Yes      | -             | `<madmp-api-app-id>`         |
| `OPENCDMP_QUEUE_APP_ID` | The `QUEUE_APP_ID` of the OpenCDMP API service. Messages with this application id may create, change and delete maDMPs, users and organisations. | Yes | - | `<opencdmp-api-app-id>` |
| `QUEUE_NAME`     | RabbitMQ queue name string.                                  | Yes      | -             | `<rabbit-mq-queue-name>`     |
| `QUEUE_EXCHANGE` | RabbitMQ exchange name string.                               | Yes      | -             | `<rabbit-mq-queue-exchange>` |
| `RABBIT_HOST`    | RabbitMQ connection host name.                               | Yes      | -             | `<hostname>`                 |
| `RABBIT_PORT`    | RabbitMQ connection port.                                    | Yes      | -             | `<port>`                     |
| `RABBIT_USER`    | RabbitMQ username string.                                    | Yes      | -             | `<rabbit-mq-username>`       |
| `RABBIT_PASS`    | RabbitMQ password string.                                    | Yes      | -             | `<rabbit-mq-password>`       |

#### Keycloak API Configuration

| Env Variable                 | Description                                      | Required | Default Value | Example                             |
|------------------------------|--------------------------------------------------|----------|---------------|-------------------------------------|
| `KEYCLOAK_API_SERVER_URL`    | Keycloak server URL string.                      | Yes      | -             | `http://<keycloak-hostname>:<port>` |
| `KEYCLOAK_API_REALM`         | Keycloak realm name.                             | Yes      | -             | `<keycloak-realm>`                  |
| `KEYCLOAK_API_CLIENT_ID`     | Keycloak client id string to get auth token.     | Yes      | -             | `<keycloak-client-id>`              |
| `KEYCLOAK_API_CLIENT_SECRET` | Keycloak client secret string to get auth token. | Yes      | -             | `<keycloak-client-secret>`          |
| `KEYCLOAK_API_USERNAME`      | Keycloak user name string.                       | Yes      | -             | `<keycloak-username>`               |
| `KEYCLOAK_API_PASSWORD`      | Keycloak password string.                        | Yes      | -             | `<keycloak-password>`               |

#### Logging Configuration

| Env Variable                | Description                                                                              | Required | Default Value                   | Example                                 |
|-----------------------------|------------------------------------------------------------------------------------------|----------|---------------------------------|-----------------------------------------|
| `LOGGING_CONFIG_PATH`       | Xml file that stores logging configuration.                                              | No       | `classpath:logging/logback.xml` | `classpath:<file-path>/<file-name>.xml` |
| `LOGGING_DEFAULT_LOG_LEVEL` | Level of error to store. Only 4 values can be assigned: `INFO`, `DEBUG`, `WARN`, `ERROR` | No       | -                               | `WARN`                                  |
| `LOGGING_PATH`              | File path to store logging.                                                              | No       | -                               | `logs/`                                 |

#### Swagger Configuration

| Env Variable                | Description                                                                 | Required | Default Value | Example                                                 |
|-----------------------------|-----------------------------------------------------------------------------|----------|---------------|---------------------------------------------------------|
| `SWAGGER_AUTH_CLIENT_ID`    | Keycloak Swagger client id                                                  | Yes      | -             | `<keycloak-client-id>`                                  |
| `SWAGGER_AUTH_REDIRECT_URL` | Swagger redirect URL (must be the same value with swagger keycloak client). | Yes      | -             | `https://(APP_URL)/api/swagger-ui/oauth2-redirect.html` |
| `SWAGGER_SERVER_URL`        | Server URL.                                                                 | Yes      | -             | `https://(APP_URL)/api`                                 |

---

# Deployment
You can view the quick start deployment guide [here](https://github.com/OpenCDMP/rda-madmp-api/tree/main/deployment).


## Docker

### Configuring the `.env` File

The `.env` file is a crucial component for deploying **rda-madmp-api** using Docker Compose. It contains environment variables that specify:

- Versions of the container images.
- Docker registry to be used.
- Exposed ports for the proxy, API, and PostgresSQL services.

This file is located in the same directory as the `docker-compose.yml` files. Check the deployment repo [here](https://github.com/OpenCDMP/rda-madmp-api/tree/main/deployment).

| Env Variable      | Description                                                                                                                     | Default Value  | Example           |
|-------------------|---------------------------------------------------------------------------------------------------------------------------------|----------------|-------------------|
| PROXY_TAG         | Nginx proxy version.                                                                                                            | -              | `<version>`       |
| MAIN_APP_TAG      | rda-madmp-api's backend/frontend version.                                                                                       | -              | `<version>`       |
| POSTGRES_TAG      | Postgresql database version.                                                                                                    | -              | `<version>`       |
| ELK_TAG           | Elasticsearch database version.                                                                                                 | -              | `<version>`       |
| KEYCLOAK_TAG      | Keycloak authentication service version.                                                                                        | -              | `<version>`       |
| RABBITMQ_TAG      | RabbitMQ message-broker service version.                                                                                        | -              | `<version>`       |
| PROFILE           | rda-madmp-api profile. (Changing this requires you to also change the "test" prefix in your rda-madmp-api configuration files.) | test           | production        |
| SERVER_PORT       | Internal container ports for the rda-madmp-api services.                                                                        | 8080           | `<port>`          |
| PROXY_APP_PORT    | External container port for the Nginx proxy service.                                                                            | 8081           | `<port>`          |
| POSTGRES_PORT     | External container port for the Postgresql database service.                                                                    | 5432           | `<port>`          |
| CONFIG_FILES_PATH | Bind mount base path for all service configuration.                                                                             | ./config-files | `<path>`          |
| LOG_FILES_PATH    | Bind mount base path for all service logging.                                                                                   | ./logs         | `<path>`          |
| STORAGE_PATH      | Bind mount base path for all service storage.                                                                                   | ./storage      | `<path>`          |
| ELASTIC_PASSWORD  | Elasticsearch default password for the rda-madmp-api.elasticsearch.setup container.                                             | elastic        | `<your_password>` |

---

## Elasticsearch

### Configuring the `elk.env` File

The `elk.env` file is a crucial component for deploying **rda-madmp-api** using Docker Compose. It contains environment variables that specify:

- Elasticsearch's deployment settings. More configuration can be added from the official documentation of Elasticsearch.

This file is located in the [`config-files/elk`](https://github.com/OpenCDMP/rda-madmp-api/tree/main/deployment/config-files/elk) directory.

| Env Variable     | Description                                                                   | Default Value | Example      |
|------------------|-------------------------------------------------------------------------------|---------------|--------------|
| ELASTIC_PASSWORD | Elasticsearch default password for the rda-madmp-api.elasticsearch container. | elastic       | `<password>` |

---

## Keycloak

### Configuring the `keycloak.env` File

The `keycloak.env` file is a crucial component for deploying **rda-madmp-api** using Docker Compose. It contains environment variables that specify:

- Keycloak's database settings.
- Keycloak's deployment.

This file is located in the [`config-files/keycloak`](https://github.com/OpenCDMP/rda-madmp-api/tree/main/deployment/config-files/keycloak) directory.

| Env Variable            | Description                           | Default Value | Example                                      |
|-------------------------|---------------------------------------|---------------|----------------------------------------------|
| KC_DB                   | Database server used.                 | -             | postgres                                     |
| KC_DB_URL_HOST          | Database server host.                 | -             | `<container_name>`                           |
| KC_DB_SCHEMA            | Database schema.                      | -             | `<schema_name>`                              |
| KC_DB_URL_DATABASE      | Keycloak database name.               | -             | keycloak-database                            |
| KC_DB_PORT              | Database server port.                 | -             | `<port>`                                     |
| KC_DB_USERNAME          | Keycloak database owner name.         | -             | keycloak-db-admin                            |
| KC_DB_PASSWORD          | Keycloak database owner password.     | -             | `<your_password>`                            |
| KEYCLOAK_ADMIN          | Keycloak app admin user.              | -             | keycloak-app-admin                           |
| KEYCLOAK_ADMIN_PASSWORD | Keycloak app admin password.          | -             | `<your_password>`                            |
| KC_HOSTNAME             | Keycloak URL.                         | -             | `http://<host>:<port>/<keycloak_path>`       |
| KC_HOSTNAME_ADMIN       | Keycloak admin console URL.           | -             | `http://<host>:<port>/<keycloak_path>/admin` |
| KC_HTTP_RELATIVE_PATH   | Keycloak relative base path.          | /             | `/<keycloak_path>`                           |
| KC_PROXY_HEADERS        | Type of proxy header.                 | -             | xforwarded                                   |
| KC_HEALTH_ENABLED       | Enable/Disable healthcheck endpoints. | false         | true                                         |
| KC_METRICS_ENABLED      | Enable/Disable collection of metrics. | false         | true                                         |

---

## Postgresql

#### Configuring the `postgres.env` File

The `postgres.env` file is a crucial component for deploying **rda-madmp-api** using Docker Compose. It contains environment variables that specify:

- Postgres' admin user & password.

This file is located in the [`config-files/postgres`](https://github.com/OpenCDMP/rda-madmp-api/tree/main/deployment/config-files/postgres) directory.

| Env Variable      | Description              | Default Value | Example           |
|-------------------|--------------------------|---------------|-------------------|
| POSTGRES_USER     | Postgres admin user.     | postgres      | postgres          |
| POSTGRES_PASSWORD | Postgres admin password. | -             | `<your_password>` |


### Initialization Database Scripts

Alongside the `postgres.env` file, there is a directory with **initialization scripts** that are executed when the PostgreSQL container is created for the first time. This scripts are located in the [`config-files/postgres/init-scripts`](https://github.com/OpenCDMP/docker-deployment/tree/main/config-files/postgres/init-scripts) directory.


#### How It Works

When the container is launched for the first time, all the provided SQL scripts are executed in sequence.

#### Purpose of the Scripts

The init scripts are used to:

- Create the required **databases** and **schemas**.
- Insert initial configuration data necessary for rda-madmp-api to run.

### Important Notes

- These scripts only run when the database volume is empty (first container initialization).
  If the database already exists, the scripts will **not** be executed again.
---

## RabbitMQ

### Configuring the `rabbitmq.env` File

The `rabbitmq.env` file is a crucial component for deploying **rda-madmp-api** using Docker Compose. It contains environment variables that specify:

- RabbitMQ's default user & password.
- RabbitMQ's base URL.

This file is located in the [`config-files/rabbitmq`](https://github.com/OpenCDMP/rda-madmp-api/tree/main/deployment/config-files/rabbitmq) directory.

| Env Variable           | Description              | Default Value | Example          |
|------------------------|--------------------------|---------------|------------------|
| RABBITMQ_DEFAULT_USER  | RabbitMQ admin user.     | guest         | rabbit-admin     |
| RABBITMQ_DEFAULT_PASS  | RabbitMQ admin password. | guest         | `<your_passwrd>` |
| RABBITMQ_DEFAULT_VHOST | RabbitMQ base path.      | /             | `/<path>`        |

## See Also

For complete documentation on configuration, integration, and usage:

- **Keycloak Overview**: [Setting Up Keycloak](KEYCLOAK_OVERVIEW.md)
- **Deployemnt**: [Quick Start](/deployment/README.md)
