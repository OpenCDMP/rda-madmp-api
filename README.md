# RDA maDMP API for OpenCDMP

**rda-madmp-api** is a standalone service that publishes machine-actionable Data Management Plans (maDMPs) through the [Common maDMP API](https://github.com/RDA-DMP-Common/common-madmp-api) of the RDA DMP Common Standards working group. Other DMP tools, repositories and research systems can list, read, create, update and delete plans through it, in the JSON format of the [RDA DMP Common Standard](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard), version 1.3. Clients that ask for version 1.2 get it through its own media type.

It works with [OpenCDMP](https://github.com/OpenCDMP/OpenCDMP), a platform for creating and managing Data Management Plans and other Output Management Plans. OpenCDMP sends its plans to the service as they change, and maDMPs created or changed through the API are sent back to OpenCDMP. The service can also run on its own, as a store for maDMPs from any tool.

The API follows the [Common maDMP API specification](https://rda-dmp-common.github.io/common-madmp-api/); the version it implements is in [`openapi.yaml`](openapi.yaml).

## How it works

```text
OpenCDMP  ◄── RabbitMQ ──►  rda-madmp-api  ──►  Elasticsearch (maDMP documents)
                                 │
                                 ├──────────►  PostgreSQL (users, organisations, message inbox and outbox)
                                 └──────────►  Keycloak (sign-in and tokens)
```

- **From OpenCDMP**: when integration events are switched on in OpenCDMP (`INTEGRATION_EVENT_ENABLED`, see [Integration event configuration](https://opencdmp.github.io/deployment/configuration/backend/#integration-event-configuration)), OpenCDMP publishes an event to RabbitMQ whenever a plan changes or is removed. The event carries the plan's RDA JSON export, made by the [RDA JSON file transformer](https://github.com/OpenCDMP/file-transformer-rda-json). The service stores that export as a maDMP, with the plan's visibility, finalized status, users and organisation. OpenCDMP also sends user and organisation changes.
- **Back to OpenCDMP**: a maDMP created or changed through the API is published to RabbitMQ, and OpenCDMP receives it as a plan update request, to create a new plan or to update the plan the maDMP came from. Deleting a maDMP through the API does not change OpenCDMP.
- **Through the API**: anyone can read the maDMPs of public, finalized plans without signing in. Clients that sign in with Keycloak also see the maDMPs they are users of, and users with an administrator role see all of them. Creating a maDMP needs a signed-in user; changing or deleting one needs its owner or an administrator. Signed-in requests name their organisation in the `x-tenant` header (`default` for the default organisation), unless the token already carries one or the user is an administrator.

Elasticsearch stores and searches the maDMP documents. PostgreSQL holds the users and organisations synchronised from Keycloak and OpenCDMP, and the inbox and outbox of RabbitMQ messages.

## API

| Method | Path | Description |
| --- | --- | --- |
| `GET` | `/dmps` | List and search maDMPs |
| `POST` | `/dmps` | Create a maDMP |
| `GET` | `/dmps/{id}` | Get one maDMP. The response has a `Last-Modified` header. |
| `PUT` | `/dmps/{id}` | Replace a maDMP. Send `If-Unmodified-Since` with the `Last-Modified` value you read to get a `409` when someone else changed it in the meantime. |
| `DELETE` | `/dmps/{id}` | Delete a maDMP |
| `GET` | `/principal/me` | The signed-in user |

Request and response bodies are maDMP JSON documents of the form `{ "dmp": { ... } }`. The running service serves its OpenAPI description at `/v3/api-docs` and a Swagger UI at `/swagger-ui`.

## Requirements

- Java 21 and Maven to build. The service is a Spring Boot 4.0 application.
- PostgreSQL, Elasticsearch with the `analysis-icu` plugin, RabbitMQ and Keycloak. The [Docker Compose example](deployment/README.md) runs all of them.
- OpenCDMP's [common-models](https://opencdmp.github.io/developers/plugins/common-models/) package, which describes the plans that OpenCDMP sends. Its version is in [`rda-madmp/pom.xml`](rda-madmp/pom.xml).
- To exchange plans with OpenCDMP: access to the RabbitMQ exchange that OpenCDMP uses, and integration events switched on in OpenCDMP.

## Build and run

```bash
mvn clean package
java -jar rdamadmp-web/target/rdamadmp-web-1.0.0-SNAPSHOT.jar
```

Set the database, Elasticsearch, RabbitMQ and Keycloak environment variables before you start it (see [Configuration](#configuration)). Run this way, the service listens on port `8088` (set `WEB_PORT` to change it) and writes its logs to `/logs` unless `LOGGING_PATH` names another folder.

The service creates its Elasticsearch index on first use, but not its database tables. Create them first with [`01.01.000-seed.schema.sql`](deployment/config-files/postgres/init-scripts/01.01.000-seed.schema.sql), a `psql` script that creates them in a database named `madmp`, or point the service at the OpenCDMP database, whose migrations create the same `madmp_` tables.

To build a Docker image:

```bash
mvn clean package
docker build -t rda-madmp-api .
```

The image is [`opencdmp/rda-madmp-api`](https://hub.docker.com/r/opencdmp/rda-madmp-api) on Docker Hub. The container listens on port `8080`, reads `app-<PROFILE>.env` from `/config` (set the `PROFILE` environment variable) and writes its logs to `/logs`.

### Docker Compose example

The [`deployment`](deployment/README.md) folder runs the service with PostgreSQL, Elasticsearch, RabbitMQ, Keycloak and an nginx proxy on one machine, with demo users and passwords. Use it to try the API; it is not a production setup.

## Configuration

The service reads its settings from environment variables:

- `IDP_ISSUER_URI`, `IDP_CLAIMS_ROLES_PATH`, `SECURITY_AUDIENCE`: the Keycloak realm and client whose tokens the API accepts
- `DB_CONNECTION_STRING`, `DB_USER`, `DB_PASSWORD`: PostgreSQL
- `ELASTIC_HOST`, `ELASTIC_USE_SSL`, `ELASTIC_USER`, `ELASTIC_PASS`, `ELASTIC_RDA_INDEX`: Elasticsearch and the index that holds the maDMPs
- `RABBIT_HOST`, `RABBIT_PORT`, `RABBIT_USER`, `RABBIT_PASS`, `QUEUE_EXCHANGE`, `QUEUE_NAME`, `QUEUE_APP_ID`: RabbitMQ. To receive plans from OpenCDMP, use the same RabbitMQ and `QUEUE_EXCHANGE` as the OpenCDMP API service (see its [RabbitMQ configuration](https://opencdmp.github.io/deployment/configuration/backend/#rabbitmq-configuration)) and a `QUEUE_NAME` of its own.
- `OPENCDMP_QUEUE_APP_ID`: the OpenCDMP API service's `QUEUE_APP_ID`. Messages with that application id may create, change and delete maDMPs, users and organisations.
- `KEYCLOAK_API_*`: the Keycloak admin client the service uses to manage users' group memberships
- `QUEUE_ENABLED` (default `true`): exchange messages with OpenCDMP over RabbitMQ
- `TENANT_IS_MULTITENANT`, `TENANT_SUPPORT_EXPANSION` (both default `true`): support for several organisations and for the shared default organisation
- `CORS_ALLOWED_ORIGIN`, `SWAGGER_*`, `WEB_PORT`, `WEB_CONTEXT_PATH`, logging, and `RDA_MADMP_APPLY_INTEGRATION_ACTION`

The [Configuration overview](docs/CONFIGURATION_OVERVIEW.md) describes most of them, with examples, and the [Keycloak overview](docs/KEYCLOAK_OVERVIEW.md) the realm, clients and roles the service expects.

## Documentation

- [Deployment guide](deployment/README.md): run the service with Docker Compose
- [Configuration overview](docs/CONFIGURATION_OVERVIEW.md): environment variables
- [Integration overview](docs/INTEGRATION_OVERVIEW.md): how plans move between OpenCDMP and the service
- [Authentication overview](docs/AUTHORIZATION_OVERVIEW.md) and [Keycloak overview](docs/KEYCLOAK_OVERVIEW.md): users, organisations and Keycloak setup
- [Model overview](docs/MODEL_OVERVIEW.md): how maDMPs are stored
- [Common maDMP API specification](https://rda-dmp-common.github.io/common-madmp-api/) and [RDA DMP Common Standard](https://github.com/RDA-DMP-Common/RDA-DMP-Common-Standard)
- [OpenCDMP documentation](https://opencdmp.github.io/)

Related OpenCDMP services: the [RDA JSON file transformer](https://github.com/OpenCDMP/file-transformer-rda-json), which exports plans to RDA JSON, and the [RDA maDMP evaluator](https://github.com/OpenCDMP/evaluator-rda-madmp), which checks an export against the maDMP schema.

## Contributing

Report bugs and suggest changes in [GitHub Issues](https://github.com/OpenCDMP/rda-madmp-api/issues); pull requests are welcome.

## License

rda-madmp-api is licensed under the [European Union Public Licence (EUPL) v1.2](LICENSE).

## Contact

Questions: email opencdmp at cite.gr.
