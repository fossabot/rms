#!/bin/bash
#-----------------------
# import function
#-----------------------
source ../def_function.sh

#-----------------------
# execute cases
#-----------------------
case "$1" in
  "test")
    maven_build_test
    ;;
  "build")
    maven_build
    ;;
  "java")
    test 1 -eq 1
    ;;
  "generateOas")
    maven_generateOas
    exit $?
    ;;
  "generateKey")
    # ./startup.sh generateKey aes passphrase text
    # "generateKey" and "aes" is fix value
    java -cp "target/libs/*" io.helidon.config.encryption.Main $2 $3 $4
    exit $?
    ;;
  "help")
    echo "test  -> maven_build_test->execute java"
    echo "build -> maven_build->execute java"
    echo "java  -> execute java"
    echo "generateOas -> generate OAS filte"
    echo "generateKey -> generate secret key"
    exit 0
    ;;
  *)
    maven_build
    ;;
esac

if [ $? -eq 0 ]; then
  exec_java rms-service-server.jar
fi

exit
