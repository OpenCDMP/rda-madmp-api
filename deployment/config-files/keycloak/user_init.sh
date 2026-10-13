#!/bin/bash

/opt/keycloak/bin/kcadm.sh config credentials --server http://localhost:8080/auth/ --realm master --user keycloak-admin --password admin --client admin-cli
/opt/keycloak/bin/kcadm.sh add-roles -r madmp --uusername dmp-keycloak-api --cclientid realm-management --rolename realm-admin
