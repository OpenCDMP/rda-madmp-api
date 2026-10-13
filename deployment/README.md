# RDA maDMP API: Docker Compose example

This folder runs the RDA maDMP API on one machine with everything it needs: PostgreSQL, Elasticsearch, RabbitMQ, Keycloak with a ready-made realm, and an nginx proxy in front of the API and Keycloak. Use it to try the API or as a starting point for your own deployment. It ships with demo users and passwords, so it is not a production setup.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) with [Docker Compose](https://docs.docker.com/compose/install/)
- [Git](https://git-scm.com/downloads)
- A Linux shell for `deploy_madmp.sh`, which uses `sudo` to set file permissions
- The host name `host.docker.internal` resolving to your machine, both on the machine and inside the API container, which reaches Keycloak through the proxy at that address. Docker Desktop provides both. With Docker Engine on Linux, add `127.0.0.1 host.docker.internal` to `/etc/hosts`, and add `extra_hosts: ["host.docker.internal:host-gateway"]` to the `madmp.backend` service.

## What is in this folder

```text
deployment/
├── .env                          image versions, ports, and host folders
├── docker-compose.yml            the services
├── docker-compose.override.yml   ports, volumes, networks, resources, health checks
├── deploy_madmp.sh               first-time setup and start
└── config-files/                 configuration of each service
    ├── madmp/backend/config/     app-test.env: settings of the API (PROFILE=test)
    ├── keycloak/                 realm export, admin settings, and setup scripts
    ├── elk/                      Elasticsearch settings and index setup
    ├── postgres/                 database settings and init scripts
    ├── rabbitmq/                 RabbitMQ settings
    └── proxy/                    nginx configuration
```

## Configure

1. Clone the repository and open this folder:

   ```bash
   git clone https://github.com/OpenCDMP/rda-madmp-api.git
   cd rda-madmp-api/deployment
   ```

2. Review `.env`: image versions, the port of the proxy (`PROXY_APP_PORT`, default `8081`), the host port of PostgreSQL (`POSTGRES_PORT`, default `5432`), and the folders for storage and logs.
3. Review `config-files/`. The settings of the API are in `config-files/madmp/backend/config/app-test.env` and are described in the [Configuration overview](../docs/CONFIGURATION_OVERVIEW.md). Keep them in line with the other services: the database password in `postgres/postgres.env`, the Elasticsearch user, password and index in `elk/elasticsearch/index_init.sh`, the RabbitMQ user in `rabbitmq/rabbitmq.env`, and the Keycloak clients and users in the realm export.
4. Change the demo passwords before you expose the stack: the Keycloak administrator, the Keycloak users and client secrets in the realm export, PostgreSQL, Elasticsearch and RabbitMQ. Some of them are repeated in the setup scripts: the Elasticsearch `elastic` password in `deploy_madmp.sh`, `index_init.sh` and `docker-compose.override.yml`, and the Keycloak administrator password in `keycloak/user_init.sh`.

To receive plans from an OpenCDMP installation, point the RabbitMQ settings in `app-test.env` at the RabbitMQ and exchange that OpenCDMP uses, and switch on integration events in OpenCDMP. See [How it works](../README.md#how-it-works).

## Deploy

```bash
chmod 700 deploy_madmp.sh
./deploy_madmp.sh
```

The script pulls the images and creates the storage and log folders. It starts Elasticsearch, creates the index user and installs the `analysis-icu` plugin that the API needs, starts PostgreSQL and Keycloak and prepares the Keycloak user of the API, and then starts the rest. Run it with `-r` to delete the PostgreSQL and Elasticsearch data and start from scratch.

If the script fails with `cannot execute: required file not found`, it has Windows (CRLF) line endings. Convert it to Unix (LF) line endings and run it again:

```bash
sed -i 's/\r$//' deploy_madmp.sh
```

Check that the containers are running:

```bash
docker compose ps
```

## Use it

- API: `http://host.docker.internal:8081/`
- Swagger UI: `http://host.docker.internal:8081/swagger-ui`. Authorize as the demo user `madmp-admin` with password `admin`.
- Keycloak administration console: `http://host.docker.internal:8081/auth/`, user `keycloak-admin`, password `admin`. Manage realms, clients, users and roles here, and change this password first.

If you use another host name or port, change it in `.env` and in the `config-files/` that contain `host.docker.internal:8081`.

## Storage and logs

Data is kept on the host, in the `storage/` folder. The API, Keycloak, Elasticsearch and the proxy write their logs to their own folders under `logs/`. Change the paths in `.env` and `docker-compose.override.yml` to suit your machine.
