#!/bin/bash

if [ -e ./ers-service.jar ]; then
  rm -f ./ers-service.jar
fi

if [ -e ./ersServiceApp ]; then
  rm -rf ./ersServiceApp
fi

mvn -PincludeJunit4Libs,copyLibs,product clean package -DskipTests=true

mkdir ersServiceApp
cp ./target/ers-service.jar ./ersServiceApp
cp -r ./target/libs ./ersServiceApp
cp ./env/deployment/appspec.yml ./ersServiceApp
cp ./env/deployment/logback-production.xml ./ersServiceApp
cp -r ./env/deployment/scripts ./ersServiceApp
cp -r ./env/init-data ./ersServiceApp

ZIP_NAME="ersServiceApp/ersServiceApp-"`date "+%Y%m%d_%H%M%S"`.zip

aws deploy push --application-name ersServiceApplication --s3-location s3://ogiwarat-app-deployment/$ZIP_NAME --ignore-hidden-files --source ./ersServiceApp
aws deploy create-deployment --application-name ersServiceApplication --deployment-config-name CodeDeployDefault.OneAtATime --deployment-group-name ersServiceApplicationGroup --s3-location bucket=ogiwarat-app-deployment,bundleType=zip,key=$ZIP_NAME
