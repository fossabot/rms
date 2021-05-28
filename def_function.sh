#-----------------------
# define function
#-----------------------
maven_build() {
  mvn -Peclipse,copy-libs clean package
}
maven_build_test() {
  mvn -Pcli,copy-libs clean package
}
maven_generateOas() {
  mvn -PgenerateOas,cli clean test
  if [ -e ./target/generated-oas/openapi.yml ]; then
    cp -f ./target/generated-oas/openapi.yml ./docs
  fi
}
exec_java() {
  java -jar target/$1
}
