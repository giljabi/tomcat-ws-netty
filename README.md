# Gateway

# README
### [gateway](gateway/README.md)
### [test-client](test-client/README.md)

## connect url
* http://localhost:8082/swagger-ui/index.html
* http://localhost:8082/monitor/list
* http://localhost:8082/actuator/prometheus


## 개요
* web socket을 이용한 서버-클라이언트 통신
  * client --> /api/token : tomcat 접속을 위한 
* netty를 이용한 서버-클라이언트 통신
  * netty: 8090


## 프로젝트 구성
- gateway (서버-부스 TCP, 확장가능성 있음)


## 개발환경
* JDK 1.8
* Spring boot 2.4.0
* PostgreSQL 9.6 이상

## Active profile
* local


## Build
- 메이븐 프로젝트 빌드
```
mvn clean package -pl gateway -am -DskipTests
mvn clean package -pl test-client -am -DskipTests
```
