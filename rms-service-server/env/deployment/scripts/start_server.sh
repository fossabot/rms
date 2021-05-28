#!/bin/bash -x
if [ -f /home/ec2-user/private/ers-secret ]; then
    . /home/ec2-user/private/ers-secret
fi

cd /home/ec2-user/ersServiceApp
export JWT_FILTER_ENABLE=true

echo "[ersServiceApp]STARTING..."
java -Dlogback.configurationFile=./logback-production.xml -jar ers-service.jar > /dev/null 2>&1 < /dev/null &

exit $?
