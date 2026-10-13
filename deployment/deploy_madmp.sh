#!/bin/bash
set -e
storage_dir=./storage
logs_dir=./logs
docker compose down
docker compose pull

sudo chmod 744 ./config-files/elk/elasticsearch/index_init.sh
sed -i -e 's/\r$//' ./config-files/elk/elasticsearch/index_init.sh

sudo chmod 744 ./config-files/keycloak/user_init.sh
sed -i -e 's/\r$//' ./config-files/keycloak/user_init.sh

sudo chmod 744 ./config-files/keycloak/health-check.sh
sed -i -e 's/\r$//' ./config-files/keycloak/health-check.sh

if [[ $1 == "-r" ]]; then
    echo "Resetting PostgreSQL and Elasticsearch storage..."
    sudo rm -rf ./storage/postgres/data
    mkdir -p ./storage/postgres/data

    sudo rm -rf ./storage/elasticsearch
    mkdir -p ./storage/elasticsearch
fi

folders_to_create=(
    $storage_dir
    $storage_dir/elasticsearch
    $storage_dir/postgres
    $storage_dir/rabbitmq
    $storage_dir/madmp
    $storage_dir/madmp/backend
    $logs_dir
    $logs_dir/elasticsearch
    $logs_dir/keycloak
    $logs_dir/proxy
    $logs_dir/madmp
    $logs_dir/madmp/backend
)
echo "Creating storage and log folders if not exist"
for path in "${folders_to_create[@]}"
do
    if [ ! -d $path ]; then
        echo $path
        mkdir -p $path
    fi
done

echo "Deploying Elasticsearch Container"
docker compose up -d madmp.elasticsearch

function loading_icon() {
    local load_interval="${1}"
    local loading_message="${2}"
    local elapsed=0
    local loading_animation=( '—' "\\" '|' '/' )

    echo -n "${loading_message} "

    tput civis
    trap "tput cnorm" EXIT
    while [ "${load_interval}" -ne "${elapsed}" ]; do
        for frame in "${loading_animation[@]}" ; do
            printf "%s\b" "${frame}"
            sleep 0.25
        done
        elapsed=$(( elapsed + 1 ))
    done
    printf " \b\n"
}

echo "Waiting for Elasticsearch to Startup..."
until docker exec madmp.elasticsearch curl -XGET "http://madmp.elasticsearch:9200/_cluster/health?pretty&wait_for_status=green&master_timeout=-1&timeout=-1" -u elastic:elastic 2>/dev/null | grep -q "green"; do sleep 15; done;
docker compose up -d madmp.elasticsearch.setup

docker exec madmp.elasticsearch.setup /usr/share/elasticsearch/config/index_init.sh || true
docker exec madmp.elasticsearch elasticsearch-plugin install analysis-icu || echo "Plugin install failed, but continuing..."

docker restart madmp.elasticsearch
docker rm madmp.elasticsearch.setup

docker compose up -d madmp.postgres

loading_icon 20 "Waiting for PostgreSQL to Initialize..."

docker compose up -d madmp.keycloak

echo "Waiting for Keycloak to Startup..."
until docker exec madmp.keycloak /tmp/health-check.sh 2>/dev/null | grep -q "UP"; do sleep 15; done;
docker exec madmp.keycloak /tmp/user_init.sh

docker compose up -d

loading_icon 60 "Waiting for the Backend to Startup..."
docker rm madmp.elasticsearch.setup