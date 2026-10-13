# rda-madmp-api documentation

Reference pages for the RDA maDMP API. For what the service is and how to build and run it, start with the [main README](../README.md); to run it with Docker Compose, see the [deployment guide](../deployment/README.md).

- [Configuration overview](CONFIGURATION_OVERVIEW.md): the environment variables for authentication with Keycloak, the PostgreSQL database, Elasticsearch, RabbitMQ, the Keycloak admin client, logging and Swagger.
- [Integration overview](INTEGRATION_OVERVIEW.md): how the service exchanges plans with [OpenCDMP](https://github.com/OpenCDMP/OpenCDMP) over RabbitMQ. OpenCDMP sends each changed plan with its RDA JSON export, and the service stores it as an RDA maDMP; maDMPs changed through the API are sent back to OpenCDMP.
- [Authentication overview](AUTHORIZATION_OVERVIEW.md): sign-in through Keycloak, and the users and organisations the service has seen, kept in PostgreSQL:
  - `UserEntity`: an authenticated user
  - `UserCredentialEntity`: links a user to their Keycloak identity
  - `TenantEntity`: an organisation
  - `TenantUserEntity`: which users belong to which organisations
- [Keycloak overview](KEYCLOAK_OVERVIEW.md): how to set up the Keycloak realm, clients and roles that the service expects.
- [Model overview](MODEL_OVERVIEW.md): how a maDMP is stored in Elasticsearch. `DmpRootElasticEntity` holds the RDA maDMP document (`DmpElasticEntity`) together with the fields used for access control.
