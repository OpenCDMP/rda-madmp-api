#!/bin/bash
set -e

echo "Creating Elasticsearch roles, and users..."

# Command to create the role
curl -XPOST "madmp.elasticsearch:9200/_security/role/madmp-api-user/" -u elastic:elastic -H 'Content-Type: application/json' -d '{
  "cluster": ["monitor"],
  "indices": [
    {
      "names": [ "madmp-test" ],
      "privileges": [ "all" ]
    }
  ]
}' || { echo "Failed to create the role"; exit 1; }

# Command to create the user
curl -XPOST "madmp.elasticsearch:9200/_security/user/madmp-api-user/" -u elastic:elastic -H 'Content-Type: application/json' -d '
{
  "password" : "madmp-api-pass",
  "roles" : [ "madmp-api-user" ]
}' || { echo "Failed to create the user"; exit 1; }

echo "Initialization complete."
