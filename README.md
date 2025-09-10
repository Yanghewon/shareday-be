## 🛠️ Tech Stack

### Backend
- **Java 21** – 최신 LTS, Virtual Thread 지원
- **Spring Boot 3.5.5** – REST API 및 서버 구성
- **Spring Data JPA** – 관계형 DB 매핑
- **Thymeleaf** – 서버사이드 템플릿 (선택)
- **springdoc-openapi 2.7.+** – Swagger UI & OpenAPI 문서 자동화  
  → [Swagger UI](http://localhost:8080/swagger-ui/index.html) / [OpenAPI JSON](http://localhost:8080/v3/api-docs)

### Database
- **H2 Database** – 개발/테스트 인메모리 DB
- (향후) **MongoDB Atlas** – 일정 데이터 및 유저 정보 저장

### Build & Dependency
- **Gradle (Groovy DSL)** – `build.gradle`, `settings.gradle`
- **io.spring.dependency-management** 플러그인

### Deployment & DevOps
- **Render** – 백엔드 배포 및 CI/CD
- **GitHub Actions** – 버전 관리 및 자동 배포 트리거

### Test
- **JUnit 5 (JUnit Platform)** – 단위/통합 테스트
- **spring-boot-starter-test** – MockMvc 포함 통합 테스트
