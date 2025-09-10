## 🛠️ Tech Stack

### Backend
- **Kotlin** – 간결하고 안정적인 JVM 언어
- **Spring Boot** – RESTful API 제공 및 서버 구조
- **Spring Data MongoDB** – MongoDB와의 간단한 연결
- **Spring Security** (향후 예정) – 사용자 인증 및 권한 관리

### Database
- **MongoDB Atlas** – 클라우드 기반 NoSQL 데이터베이스
  - 일정 데이터 및 유저 정보 저장
  - 자유로운 구조와 확장성

### Deployment & DevOps
- **Render** – 백엔드 배포 및 CI/CD 지원
- **GitHub** – 버전 관리 및 자동 배포 트리거

---

이 스택은 간단한 2인용 일정 공유 앱을 MVP 형태로 빠르게 개발하고, 필요 시 확장 가능한 구조로 설계되어 있습니다.

---

## 📲 PWA Support (Progressive Web App)

ShareDay는 **PWA(Progressive Web App)**를 지원하도록 설계되었습니다. 백엔드에서 제공하는 RESTful API와 함께, 브라우저에서 앱처럼 사용할 수 있어, 앱스토어 없이도 쉽게 접속하고 사용할 수 있습니다.

### PWA 특징
- **홈 화면에 설치 가능** (모바일/PC 모두)
- **전체화면 모드**로 앱처럼 작동
- **인터넷 연결 없이도 작동 가능** (옵션)
- **앱스토어 등록 없이 개인 간 공유 용이**

### 개발 예정 스택
- **`vite-plugin-pwa`** – PWA 설정을 위한 Vite 플러그인
- **Custom Manifest (`manifest.json`)** 설정
- **Service Worker** – 자동 생성 및 캐싱 지원

> 설치 아이콘은 `public/pwa-icon-192.png`, `public/pwa-icon-512.png`에 위치  
> **Vercel** 또는 **Netlify**로 배포 시 자동으로 설치 가능 상태 감지

🚧 현재는 백엔드의 기본 API가 준비되어 있으며, **PWA** 기능은 향후 클라이언트와 연동하여 구현될 예정입니다.
