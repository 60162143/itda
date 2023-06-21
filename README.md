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
- **Framework :**
  <img align="center" src="https://img.shields.io/badge/Android Studio-3DDC84?style=flat&logo=Android Studio&logoColor=white"> <img align="center" src="https://img.shields.io/badge/Eclipse-2C2255?style=flat&logo=eclipseide&logoColor=white"> <img align="center" src="https://img.shields.io/badge/Visual Studio Code-007ACC?style=flat&logo=visualstudiocode&logoColor=white">
- **Language :**
  &nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Java-007396?style=flat&logo=Backblaze&logoColor=white"> <img align="center" src="https://img.shields.io/badge/Php-777BB4?style=flat&logo=php&logoColor=white"> <img align="center" src="https://img.shields.io/badge/Python-3776AB?style=flat&logo=python&logoColor=white">
- **Server :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Apache Tomcat-F8DC75?style=flat&logo=apachetomcat&logoColor=white"> <img align="center" src="https://img.shields.io/badge/Ivy Hosting-DB3552?style=flat&logo=askfm&logoColor=white">
- **Database :**
  &nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Mysql-4479A1?style=flat&logo=mysql&logoColor=white">
- **SCM :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Github-181717?style=flat&logo=github&logoColor=white">
- **Build :**
  &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<img align="center" src="https://img.shields.io/badge/Gradle-02303A?style=flat&logo=gradle&logoColor=white">
<br />

## **👨🏻‍💻 기능 구현**

### **1. 어플 실행 & 메인 화면**

<img width="300" height="600" alt="어플 실행 화면" src="https://github.com/60162143/itda/assets/33407087/ec581a5c-64d0-480e-96a8-95024a8de0a3" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="메인 가게 검색 화면" src="https://github.com/60162143/itda/assets/33407087/74f64b89-7c9f-4dce-b23d-8ffef49233fc" />

- 스플래시 화면 실행 후 메인 화면 전환
  
- 상단 검색어 입력후 **조회 기능** 구현
- 카테고리 별로 **조회 기능** 구현
- 간단 가게 정보 **조회 기능** 구현

<br />

### **2. 가게 상세 화면**
  - #### **2-1. 가게 정보 조회 화면**
    <img width="300" height="600" alt="가게 상세 조회 화면" src="https://github.com/60162143/itda/assets/33407087/0710e018-c1ce-4bb7-bfc3-971be221a8f6" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="리뷰 작성 화면" src="https://github.com/60162143/itda/assets/33407087/53e04e9e-3dce-4b9f-8029-948ec915818a" />
![리뷰 댓글 작성 화면](https://github.com/60162143/itda/assets/33407087/8511b860-c9ac-43d0-8f3c-1fae652f5160)

    - 가게 정보 **조회 기능** 구현( **데이터는 카카오 맵에서 크롤링** )

    - **카카에 맵 라이브러리를 이용한 가게 위치 조회 기능** 구현
    - 협업된 가게 정보 **조회 기능** 구현
    - 리뷰 **추가, 삭제, 조회 기능** 구현
    - 리뷰 작성 시 업로드 한 사진 **조회 기능** 구현 ( **ftp4j-1.6 라이브러리를 이용한 ftp 파일 업로드** )

<br />

  - #### **2-2. 주문/결제 화면**
    <img width="300" height="600" alt="주문 화면" src="https://github.com/60162143/itda/assets/33407087/213e3c46-3485-44c5-9c45-61f1338fe56c" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="결제 화면" src="https://github.com/60162143/itda/assets/33407087/44838570-8d4d-4d04-9a87-cc2c8baa87af" />

    - 선택 메뉴 주문 기능 구현

    - Boot Pay 라이브러리를 이용한 결제 기능 구현 ( 카카오 결제 or 카드 결제 )
    - 결제 시 쿠폰 사용 기능 구현
    - 협업된 가게에 한해서 협업 조건에 따른 할인 쿠폰 발급 기능 구현

<br />

### **3. 협업 화면**
<img width="300" height="600" alt="협업 화면" src="https://github.com/60162143/itda/assets/33407087/077262bd-d100-46c8-a34f-6d7c7068b74f" />

  - 협업된 가게 정보 조회 기능 구현
  
  - 협업한 가게 찜 기능 구현
  - 가게 이미지 클릭 시 가게 상세 정보 조회 기능 구현

### **4. 지도 화면**

<img width="300" height="600" alt="지도 화면" src="https://github.com/60162143/itda/assets/33407087/077262bd-d100-46c8-a34f-6d7c7068b74f" />
![지도 화면](https://github.com/60162143/itda/assets/33407087/d9a54d75-6e1d-4fb6-8e02-693028373a8e)

  - 협업된 가게 정보 조회 기능 구현
  
  - 협업한 가게 찜 기능 구현
  - 가게 이미지 클릭 시 가게 상세 정보 조회 기능 구현

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
