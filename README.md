# Spring-batch-inflearn
[**스프링 배치 - Spring Boot 기반으로 개발하는 Spring Batch**](https://www.inflearn.com/course/%EC%8A%A4%ED%94%84%EB%A7%81-%EB%B0%B0%EC%B9%98)

## 설명
inflearn 스프링 배치 강의를 듣고 정리한 예제 코드

## 초기 확인 사항
1. MariaDB 구성 필요 (docker 사용)
2. schema-mysql.sql 기본 스키마 생성 확인 (현재 자동 생성으로 설정되어 있음)
3. 전체 테이블 목록
* address, customer, customer3, customer4, customer5, product
* 대부분 JPA에 의해 자동 생성되나, 생성되지 않는 경우는 /resource/chunk 에 있는 schema.sql 파일로 직접 생성 (customer, customer4)

## Program arguments
1. 기본적으로 --job.name 설정 필요. 
   <p>(application-prod.yml 에 spring.batch.enabled: true, spring.batch.job.names: ${job.name:NONE} 설정 시)
   <p>예시 : <code>--job.name=fileJob requestDate=20210101</code>
2. Quartz 스케쥴러 등록된 경우에는 배치 자동수행을 막아야 하므로 application-prod.yml 에 spring.batch.enabled: false 설정해야함.
   <p>그러면 program arguments에도 --job.name 설정할 필요 없음 (스케쥴링에 의해 수행되므로)
   <p>예시 : <code>20210101</code>