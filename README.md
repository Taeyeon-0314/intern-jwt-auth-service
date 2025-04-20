# Auth Service - Spring Boot 기반 사용자 인증 시스템(회원가입 ,로그인, 권한 변경)

## 🚀 기술 스택
### Back-end
<div align=Left>
<img src="https://img.shields.io/badge/java-007396?style=for-the-badge&logo=java&logoColor=white">
<img src="https://img.shields.io/badge/spring Boot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
<img src="https://img.shields.io/badge/spring Security-6db33f?style=for-the-badge&logo=springsecurity&logoColor=white">
<img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
<img src="https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white">
<img src="https://img.shields.io/badge/swagger-85EA2D?style=for-the-badge&logo=swagger&logoColor=black">
</div>

### Server
<img src="https://img.shields.io/badge/amazon ec2-FF9900?style=for-the-badge&logo=amazonec2&logoColor=white">

### Tools & Version Control
<div align=Left>
<img src="https://img.shields.io/badge/github-181717?style=for-the-badge&logo=github&logoColor=white">
<img src="https://img.shields.io/badge/git-F05032?style=for-the-badge&logo=git&logoColor=white">
<img src="https://img.shields.io/badge/IntelliJ-181717?style=for-the-badge&logo=IntelliJIDEA&logoColor=white">
<img src="https://img.shields.io/badge/Postman-FF6C37?style=for-the-badge&logo=Postman&logoColor=white">
</div>

## 🧩 주요 기능

### - 회원가입
### - 로그인
### - 권한 변경(user->admin)
### - Spring Security 기반 접근 제어
### -  Swagger를 통한 API 명세서

## 📁 프로젝트 구조
```
 src
  └── main
        └── java
             └── com
                  └── intern
                        └── jwtauthservice
                        ├── JwtAuthServiceApplication.java
                        ├── config
                        │   ├── JwtAuthenticationFilter.java
                        │   ├── SecurityConfig.java
                        │   └── SwaggerConfig.java
                        ├── controller
                        │   └── AuthController.java
                        ├── dto
                        │   ├── GrantAdminResponse.java
                        │   ├── LoginRequest.java
                        │   ├── SignupRequest.java
                        │   ├── SignupResponse.java
                        │   └── User.java
                        ├── security
                        │   └── JwtUtil.java
                        └── service
                            └── UserService.java
```

## 🔑 Swagger API 문서
**`Swagger API 문서 링크`** : [ 🔗 Swagger API 문서](https://todo-lyart-omega.vercel.app/)

## API 명세서

| 기능     | Method | API Path                    | Request                                                           | Response                                                                                                                                                                                                                                                                             |
|----------|--------|-----------------------------|-------------------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| 회원가입 | POST   | `/signup`                   | body {"username": string, "password": string, "nickname": string} | 200 (회원가입 성공) {"username": "string","nickname": "string","roles": [{"role": "string"}]} , 400(회원 가입 실패 - 아이디 중복) {"error": {"code": "USER_ALREADY_EXISTS","message": "이미 가입된 사용자입니다."}}                                                                                                                                                                    |
| 로그인   | POST   | `/login`                    | body {"username": string, "password": string}                     | 200(로그인 성공) {"token": "토큰"} , 400(로그인 실패) {"error": {"code": "INVALID_CREDENTIALS","message": "아이디 또는 비밀번호가 올바르지 않습니다."}}                                                                                                                                                            |
| 권한변경 | PATCH  | `/admin/users/{userId}/roles` | Parameters : userId                                               | 200(권한변경 성공) {"username": "string","nickname": "string","roles": [{"role": "string"}]} , 403(권한부족) {"error": {"code": "ACCESS_DENIED","message": "관리자 권한이 필요한 요청입니다. 접근 권한이 없습니다."}} , 403(토큰 만료 및 유효하지않은 토큰으로 접근) "error": {"code": "INVALID_TOKEN","message": "유효하지 않은 인증 토큰입니다."} |

---

## ⚙️ 실행 방법

```
# 1. 프로젝트 클론
git clone https://github.com/Taeyeon-0314/intern-jwt-auth-service
cd intern-jwt-auth-service

# 2. 환경 변수 설정 (.env)

# 3. 빌드 & 실행
./gradlew bootRun

```
