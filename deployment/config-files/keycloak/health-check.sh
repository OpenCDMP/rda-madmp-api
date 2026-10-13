#!/bin/bash
exec 3<>/dev/tcp/localhost/9000

echo -e "GET /auth/health/ready HTTP/1.1\nhost: localhost:9000\n" >&3

timeout --preserve-status 1 cat <&3 | grep -m 1 status | grep -m 1 UP
ERROR=$?

exec 3<&-
exec 3>&-

echo $ERROR
exit $ERROR