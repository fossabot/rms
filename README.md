[![rms-build](https://github.com/ozytso/rms/actions/workflows/rms-build.yml/badge.svg)](https://github.com/ozytso/rms/actions/workflows/rms-build.yml)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=ozytso_rms&metric=sqale_rating)](https://sonarcloud.io/dashboard?id=ozytso_rms)
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fozytso%2Frms.svg?type=shield)](https://app.fossa.com/projects/git%2Bgithub.com%2Fozytso%2Frms?ref=badge_shield)
# Rental Management System Reports
アプリケーション
 - サーバーアプリケーション
   - REST API(Redoc)は[こちら](http://rms.ext-act.io/static/)
 - クライアントアプリケーション
   - REST APIを操作するコンソールアプリケーション。jpackageでOSごとの実行形式ファイルに変換している。zipファイルを解凍するだけで利用可能
     - Windows版は[こちら](https://ozytso.github.io/rms/binary/RmsConsoleWin.zip)
     - Mac版は[こちら](https://ozytso.github.io/rms/binary/RmsConsoleMac.zip)


Rental Management Systemのビルドやテスト、APIドキュメントなどの情報一覧

| module | information | report |
| ------ | ------ | ---- |
| rms-all | REST API Document | [Redoc](http://rms.ext-act.io/static/)|
|  | Java API Document | [Javadoc](https://ozytso.github.io/rms/site/modules/apidocs/index.html) |
|  | Code Coverage | [JaCoCo](https://ozytso.github.io/rms/site/modules/rms-coverage/jacoco-aggregate/index.html) |
|  | Code Duplication | [CDC](https://ozytso.github.io/rms/site/modules/cpd.html) |
| rms-platform | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-platform/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-platform/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-platform/spotbugs.html) |
| rms-service | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-service/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-service/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-service/spotbugs.html) |
| rms-service-server | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-service-server/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-service-server/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-service-server/spotbugs.html) |
| rms-client-api | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-client-api/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-client-api/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-client-api/spotbugs.html) |
| rms-client-api-remote | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-client-api-remote/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-client-api-remote/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-client-api-remote/spotbugs.html) |
| rms-client-api-local | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-client-api-local/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-client-api-local/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-client-api-local/spotbugs.html) |
| rms-console-ui | Generate maven-site | [Project Information Top](https://ozytso.github.io/rms/site/modules/rms-client-ui-console/project-info.html) |
| | JUnit Test Result | [Surefire Report](https://ozytso.github.io/rms/site/modules/rms-client-ui-console/surefire-report.html) |
| | Static Code Analysis | [SpotBubgs](https://ozytso.github.io/rms/site/modules/rms-client-ui-console/spotbugs.html) |


## License
[![FOSSA Status](https://app.fossa.com/api/projects/git%2Bgithub.com%2Fozytso%2Frms.svg?type=large)](https://app.fossa.com/projects/git%2Bgithub.com%2Fozytso%2Frms?ref=badge_large)