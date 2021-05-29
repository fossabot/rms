#!/bin/bash

if [ -e ./rms-service-server.jar ]; then
  rm -f ./rms-service-server.jar
fi

if [ -e ./rmsServiceApp ]; then
  rm -rf ./rmsServiceApp
fi

mvn -Pcli,copy-libs,product clean package -DskipTests=true

mkdir rmsServiceApp
cp ./target/rms-service-server.jar ./rmsServiceApp
cp -r ./target/libs ./rmsServiceApp
cp ./env/deployment/appspec.yml ./rmsServiceApp
cp ./env/deployment/logback-production.xml ./rmsServiceApp
cp -r ./env/deployment/scripts ./rmsServiceApp
cp -r ./env/init-data ./rmsServiceApp

ZIP_NAME="rmsServiceApp/rmsServiceApp-"`date "+%Y%m%d_%H%M%S"`.zip

aws deploy push --application-name rmsServiceApplication --s3-location s3://ogiwarat-app-deployment/$ZIP_NAME --ignore-hidden-files --source ./rmsServiceApp
aws deploy create-deployment --application-name rmsServiceApplication --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name rmsServiceApplicationGroup --s3-location bucket=ogiwarat-app-deployment,bundleType=zip,key=$ZIP_NAME
