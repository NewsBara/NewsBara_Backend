# 📰 해외 뉴스 기반 영어 학습 서비스 NEWSBARA 

해외 뉴스 기반 영어 학습 플랫폼의 백엔드 레포지토리입니다.  
Spring Boot 기반 REST API 서버로, 회원 관리, GPT 문제 생성, 친구 기능 등 다양한 기능을 제공합니다.

## 📌 주요 기능

| 기능 구분 | 상세 설명 |
|-----------|------------|
| 회원 | 회원가입, 로그인/로그아웃, 회원 탈퇴 |
| 학습 | 동영상 자막, 스크립트 해석, 쉐도잉 제공 |
| GPT 문제 | GPT를 활용한 퀴즈 생성 및 해설 제공 |
| 포인트 | 포인트 지급 및 기록 저장 |
| 친구 | 친구 요청/수락/거절, 친구 목록 조회 |
| 마이페이지 | 뱃지, 프로필 사진, 닉네임 관리 |

## 🛠️ 기술 스택

- **Framework**: Spring Boot 3.4  
- **DB**: MySQL  
- **ORM**: Spring Data JPA  
- **CI/CD**: GitHub Actions + Docker + Nginx (Blue-Green 배포)  
- **API Docs**: Swagger (springdoc-openapi)

## ⚙️ 로컬 실행 방법

1. 프로젝트 클론

```bash
git clone https://github.com/NewsBara/NewsBara_Backend.git
cd NewsBara
```

2. application-secret.yml 설정

```yaml
spring:
  config:
    activate:
      on-profile: common
  application:
    name: newsbara
  datasource:
    url: ""
    username: ""
    password: ""
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQLDialect
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: false
        use_sql_comments: true

  ai:
    openai:
      api-key: ""
      model: gpt-4
      temperature: 0.7
      max-tokens: 800

jwt:
  secret: ""
  access-token-validity-in-milliseconds: 3600000

springdoc:
  swagger-ui:
    path: /swagger-ui/index.html
    enabled: true
  api-docs:
    enabled: true

youtube:
  api:
    key: ""
    url: https://www.googleapis.com/youtube/v3

cloud:
  aws:
    s3:
      bucket: ""
      path:
        profile: profile
    region:
      static: ""
    stack:
      auto: false
    credentials:
      accessKey: ""
      secretKey: ""

---

# Local 환경용 Redis 설정
spring:
  config:
    activate:
      on-profile: local
  data:
    redis:
      host: localhost
      port: 6379

---

# Blue/Green 배포용 Redis 설정
spring:
  config:
    activate:
      on-profile: blue, green
  data:
    redis:
      host: redis  # Docker 컨테이너명
      port: 6379

```

## 🧪 API 문서
Swagger UI: http://localhost:8080/swagger-ui.html

## ✅ 협업 및 관리
버전 관리: Git + GitHub

이슈 트래킹: GitHub Issues

배포: AWS EC2 + Docker + Nginx + GitHub Actions (Blue/Green)

## 🙋 기여자
|이름	| 역할|
|--|--|
| [<img src="https://avatars.githubusercontent.com/bigtr3" alt="" style="width:100px;100px;">](https://github.com/bigtr3) <br/><div align="center">전승은</div>|	백엔드 개발, API 설계, 배포 자동화, GPT 문제 생성 기능 구현|
