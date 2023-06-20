**Itda**

## **📗 목차**
Itda, a tool that connects the world.


<b>

- 📝 [개요](#-포트폴리오-개요)
- 🛠 [기술 및 도구](#-기술-및-도구)
- 🔗 [링크](#-링크)
- ✨ [업데이트](#-업데이트)
- 👨🏻‍💻 [기능 구현](#-기능-구현)
  - [라이트/다크 모드](#1-라이트/다크-모드)
  - [Hash Link](#2-Hash-Link)
  - [반응형 웹](#3-반응형-웹)
  - [Email 전송](#4-Email-전송)
  - [Open graph](#5-Open-graph)
  - [기타](#6-기타-기능)
- 🚀 [배포](#-배포)
- ⏰ [커밋 히스토리](#-커밋-히스토리)

</b>

## **📝 포트폴리오 개요**

<img width="300" height="300" alt="메인 페이지" src="https://github.com/60162143/itda/assets/33407087/3342b10b-9b62-4ca2-bb5c-3fe686861326" />

> **프로젝트:** 세상을 연결해주는 도구 잇다(**Itda**)
>
> **기획 및 제작:** 오태근
>
> **분류:** 개인 모바일 프로젝트 (Android Ver.)
>
> **제작 기간:** 23.02 ~ 23.06
>
> **주요 기능:**
- 주변 맛집 정보 조회

- 가게 간 협업을 통한 맛집 추천
- 지도 API를 활용한 가게 위치 조회
- 이미지 파일 업로드
- 메일 전송
- 주문/결제
>
> **문의:** no2955922@naver.com

<br />

## **🛠 기술 및 도구**

- Framework

    <img src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white">
    <img src="https://img.shields.io/badge/Eclipse-2C2255?style=flat&logo=eclipseide&logoColor=white">
  
- Language

  <img src="https://img.shields.io/badge/Java-007396?style=flat&logo=Backblaze&logoColor=white">
  <img src="https://img.shields.io/badge/Php-777BB4?style=flat&logo=php&logoColor=white">
  <img src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white">
  
- Server

    <img src="https://img.shields.io/badge/Apache Tomcat-F8DC75?style=flat&logo=apachetomcat&logoColor=white">
    <img src="https://img.shields.io/badge/Ivy Hosting-DB3552?style=flat&logo=askfm&logoColor=white">
    
- Database

  <img src="https://img.shields.io/badge/Mysql-4479A1?style=flat&logo=mysql&logoColor=white">
  
- SCM

  <img src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white">

- 빌드/배포

  <img src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
  
<br />

## **👨🏻‍💻 기능 구현**

### **1. 어플 실행 & 메인 화면**

<img width="400" height="800" alt="어플 실행 화면" src="https://github.com/60162143/itda/assets/33407087/ec581a5c-64d0-480e-96a8-95024a8de0a3" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="400" height="800" alt="메인 가게 검색 화면" src="https://github.com/60162143/itda/assets/33407087/74f64b89-7c9f-4dce-b23d-8ffef49233fc" />

- 스플래시 화면 실행 후 메인 화면 전환
  
- 상단 검색어 입력후 **조회 기능** 구현
- 카테고리 별로 **조회 기능** 구현
- 간단 가게 정보 **조회 기능** 구현

### **2. 협업 화면**

<img width="100%" alt="hashlink" src="https://user-images.githubusercontent.com/51189962/136143186-aeb70c36-8e21-40e7-b937-deea0e66ad18.gif" />
![협업 화면](https://github.com/60162143/itda/assets/33407087/7e28375a-2fb1-4d97-a4f7-94ac206f4d6a)

- Hash Link링크를 이용하여 네비게이션에서 메뉴 클릭시 해당 영역으로 스크롤되도록 구현
- 해당하는 메뉴의 영역은 Full page.js와 유사하게 스타일링함
  
### **3. 지도 화면**

<img width="100%" alt="반응형" src="https://user-images.githubusercontent.com/51189962/136144110-0a5cb56e-1dcf-4bc8-b7d8-b93bbb100744.gif" />

- 5개의 endpoint를 두고 반응형을 구현함

```javascript
// media.js
const deviceSizes = {
  desktop: '1440px',
  laptop: '1280px',
  tablet: '1024px',
  mobile: '768px',
  phone: '480px',
};

const media = {
  desktop: `screen and (max-width: ${deviceSizes.desktop})`,
  laptop: `screen and (max-width: ${deviceSizes.laptop})`,
  tablet: `screen and (max-width: ${deviceSizes.tablet})`,
  mobile: `screen and (max-width: ${deviceSizes.mobile})`,
  phone: `screen and (max-width: ${deviceSizes.phone})`,
};

export { deviceSizes, media };
```

<img width="100%" alt="반응형 네비게이션" src="https://user-images.githubusercontent.com/51189962/136144313-2a67d258-3ec1-4517-80fc-3f67b957dff5.gif" />

- 네비게이션 메뉴의 경우 mobile(768px)을 기준으로 그 이상일 경우 상단바, 이하일 경우 햄버거메뉴로 변경

### **4. 가방 화면**

<img width="100%" alt="이메일 발신" src="https://user-images.githubusercontent.com/51189962/136146784-b8b42395-8a05-402a-b393-d0aa95580c7f.gif" />
<img width="100%" alt="이메일 수신" src="https://user-images.githubusercontent.com/51189962/136147118-ae829b7e-7ca5-4ef0-92e2-f7adc70ddb29.png" />

- EmailJS를 이용하여 서버없이 메일 서비스를 이용할 수 있도록 구현
- Sweetalert를 이용하여 커스텀 alert를 구현
- https://emailjs.com
- https://sweetalert2.github.io/

### **5. 마이페이지 화면**

<img width="100%" alt="스크린샷 2021-10-06 15 02 30" src="https://user-images.githubusercontent.com/51189962/136148865-7b6cfd30-ae66-410f-89fa-16f9ad883c74.png" />

<img width="100%" alt="스크린샷 2021-10-06 15 03 15" src="https://user-images.githubusercontent.com/51189962/136148961-28e8c84b-b5fb-4052-9150-7c20e6af3cbc.png" />

```html
<!-- index.html -->
<meta property="og:title" content="김태진 • Frontend Developer" />
<meta property="og:description" content="프론트엔드 개발자 김태진입니다." />
<meta property="og:image" content="%PUBLIC_URL%/thumb.png" />
<meta property="og:url" content="https://keemtj.com/" />
<meta property="og:type" content="website" />
```

- meta tags를 통해 Facebook, twitter, linkedin, discord, kakao talk 등 링크를 전달 했을 때 링크에 대한 정보를 볼 수 있도록 구현
- https://www.opengraph.xyz
