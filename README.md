# **Itda, a tool that connects the world.**

## **📗 목차**

<b>

- 📝 [프로젝트 개요](#-프로젝트-개요)
- 🛠 [기술 및 도구](#-기술-및-도구)
- 👨🏻‍💻 [기능 구현](#-기능-구현)
  - [어플 실행&메인 화면](#1-어플-실행&메인-화면)
  - [로그인&회원가입 화면](#2-로그인&회원가입-화면)
  - [가게 상세 화면](#3-가게-상세-화면)
    - [가게 정보 조회 화면](#1-가게-정보-조회-화면)
    - [리뷰 화면](#1-리뷰-화면)
    - [주문/결제 화면](#1-주문/결제-화면)
  - [협업 화면](#4-협업-화면)
  - [지도 화면](#5-지도-화면)
  - [가방 화면](#5-가방-화면)
  - [마이페이지 화면](#5-마이페이지-화면)
    - [내 정보 조회 화면](#1-내-정보-조회-화면)
    - [내 정보 수정 화면](#1-내-정보-수정-화면)
  - [기타](#6-기타-기능)
- 🚀 [배포](#-배포)
- ⏰ [커밋 히스토리](#-커밋-히스토리)

</b>

## **📝 프로젝트 개요**

<img width="200" height="200" alt="메인 페이지" src="https://github.com/60162143/itda/assets/33407087/3342b10b-9b62-4ca2-bb5c-3fe686861326" />

> **프로젝트:** 세상을 연결해주는 도구 **잇다(Itda**)
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

### **1. 어플 실행&메인 화면**
<img width="300" height="600" alt="어플 실행 화면" src="https://github.com/60162143/itda/assets/33407087/ec581a5c-64d0-480e-96a8-95024a8de0a3" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="메인 가게 검색 화면" src="https://github.com/60162143/itda/assets/33407087/74f64b89-7c9f-4dce-b23d-8ffef49233fc" />

- 스플래시 화면 실행 후 메인 화면 전환
  
- 상단 검색어 입력후 **조회 기능** 구현
- 카테고리 별로 **조회 기능** 구현
- 간단 가게 정보 **조회 기능** 구현

<br />

### **2. 로그인&회원가입 화면**
<img width="300" height="600" alt="로그인 화면" src="https://github.com/60162143/itda/assets/33407087/e6f0263a-90e6-44e9-a332-bde8da0e009c" /> <img width="300" height="600" alt="회원가입 화면" src="https://github.com/60162143/itda/assets/33407087/c579eeae-1aee-4485-9833-280f4d078933" />

  - 일반 로그인 / 카카오 소셜 로그인 기능 구현 ( 카카오 로그인 API 활용 )
  
  - 비밀번호 변경, 찾기 기능 구현 ( SHA-256 알고리즘으로 암호화된 비밀번호 저장 )
  - 일반 회원가입 / 카카오 소셜 회원가입 기능 구현
  - Gmail SMTP 방식을 이용한 인증 메일 전송 기능 구현

<br />

### **3. 가게 상세 화면**
  - #### **3-1. 가게 정보 조회 화면**
    <img width="300" height="600" alt="가게 상세 조회 화면" src="https://github.com/60162143/itda/assets/33407087/0710e018-c1ce-4bb7-bfc3-971be221a8f6" />

    - 가게 정보 **조회 기능** 구현( **데이터는 카카오 맵에서 크롤링** )

    - **카카오맵 API 라이브러리를 이용한 가게 위치 조회 기능** 구현
    - 협업된 가게 정보 **조회 기능** 구현
    - 가게 찜 기능 구현
    - 리뷰 **조회 기능** 구현
    - 리뷰 작성 시 업로드 한 사진 **조회 기능** 구현

<br />

  - #### **3-2. 리뷰 화면**
    <img width="300" height="600" alt="리뷰 작성 화면" src="https://github.com/60162143/itda/assets/33407087/53e04e9e-3dce-4b9f-8029-948ec915818a" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="리뷰 댓글 작성 화면" src="https://github.com/60162143/itda/assets/33407087/8511b860-c9ac-43d0-8f3c-1fae652f5160" />

    - 리뷰 **추가, 삭제, 조회 기능** 구현

    - 리뷰 작성 시 사진 **업로드 기능** 구현 ( **ftp4j-1.6 라이브러리를 이용한 ftp 파일 업로드** )
    - 작성된 리뷰에 댓글 작성 기능 구현

<br />

  - #### **3-3. 주문/결제 화면**
    <img width="300" height="600" alt="주문 화면" src="https://github.com/60162143/itda/assets/33407087/213e3c46-3485-44c5-9c45-61f1338fe56c" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="결제 화면" src="https://github.com/60162143/itda/assets/33407087/44838570-8d4d-4d04-9a87-cc2c8baa87af" />

    - 선택 메뉴 주문 기능 구현

    - Boot Pay 라이브러리를 이용한 결제 기능 구현 ( 카카오 결제 or 카드 결제 )
    - 결제 시 쿠폰 사용 기능 구현
    - 협업된 가게에 한해서 협업 조건에 따른 할인 쿠폰 발급 기능 구현

<br />

### **4. 협업 화면**
<img width="300" height="600" alt="협업 화면" src="https://github.com/60162143/itda/assets/33407087/077262bd-d100-46c8-a34f-6d7c7068b74f" />

  - 협업된 가게 정보 조회 기능 구현
  
  - 협업한 가게 찜 기능 구현
  - 가게 이미지 클릭 시 가게 상세 정보 조회 기능 구현

<br />

### **5. 지도 화면**

<img width="300" height="600" alt="지도 화면" src="https://github.com/60162143/itda/assets/33407087/d9a54d75-6e1d-4fb6-8e02-693028373a8e" />

  - 카카오 지도 API를 이용한 지도 화면 구현
  
  - 현재 위치 표시 + Heading 기능 구현
  - 검색어로 가게 조회 기능 구현
  - 가게 이미지 클릭 시 가게 상세 정보 조회 기능 구현
  - 가게 찜 기능 구현

<br />

### **6. 가방 화면**

<img width="300" height="600" alt="가방 화면" src="https://github.com/60162143/itda/assets/33407087/91e34258-b8e4-4bab-a8ae-f77d2ebd3f39" />

  - 사용 가능한 쿠폰 내역 조회 기능 구현
  
  - 주문/결제 내역 조회 기능 구현

<br />

### **7. 마이페이지 화면**

  - #### **3-1. 내 정보 조회 화면**
    <img width="300" height="600" alt="찜 목록 조회 화면" src="https://github.com/60162143/itda/assets/33407087/8eac6822-4ccd-477f-80ec-13ab024d292f" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="작성한 리뷰 목록 조회 화면" src="https://github.com/60162143/itda/assets/33407087/4c0b53b5-4f4a-4245-bc73-c1e779f81a7a" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="업로드한 사진 목록 조회 화면" src="https://github.com/60162143/itda/assets/33407087/5506779e-528f-4193-8557-3988714d2588" />

    - 내정보 조회 화면

    - 찜한 가게, 협업 목록 조회, 삭제 기능 구현
    - 작성한 리뷰 조회, 삭제 기능 구현
    - 업로드한 사진 조회, 삭제 기능 구현

<br />

  - #### **3-2. 내 정보 수정 화면**
    <img width="300" height="600" alt="프로필 변경 화면" src="https://github.com/60162143/itda/assets/33407087/57bb4774-5d9b-42b5-98ef-26d3772fafeb" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="내 정보 변경 화면" src="https://github.com/60162143/itda/assets/33407087/f7475274-3136-4961-948c-ce1063a40183" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="비밀번호 찾기 화면" src="https://github.com/60162143/itda/assets/33407087/8df6d924-e2bd-42ea-a393-303b2ce76d03" />

    - 프로필 이미지 변경 및 업로드 기능 구현 ( **ftp4j-1.6 라이브러리를 이용한 ftp 파일 업로드** )

    - 이름, 휴대폰 번호, 생일 변경 기능 구현
    - 비밀번호 변경, 찾기 기능 구현 ( SHA-256 알고리즘으로 암호화된 비밀번호 저장 )

<br />

### **8. 기타**

  - #### **8-1. 사용 라이브러리**

    - Glide Library : 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
      ```java
        Glide.with(holder.itemView)                     // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(photo.getPhotoPath()))  // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.preImage)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.errorImage)           // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.nullImage)         // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.photoImage);               // 이미지를 보여줄 View를 지정

    - Styleable Toast Library : 폰트, 배경색, 아이콘 등 토스트의 전반적인 디자인을 themes.xml에서 원하는 대로 지정해 줄 수 있는 라이브러
      ```java
        // themes.xml
        <style name="orangeToast">
            <item name="stTextBold">텍스트 스타일 Bold 유무</item>
            <item name="stTextColor">텍스트 색상</item>
            <item name="stFont">폰트</item>
            <item name="stTextSize">텍스트 사이즈</item>
            <item name="stColorBackground">배경색</item>
            <item name="stStrokeWidth">테두리 두께</item>
            <item name="stStrokeColor">테두리 색상</item>
            <item name="stIconStart">왼쪽에 나타날 아이콘</item>
            <item name="stIconEnd">오른쪽에 나타날 아이콘</item>
            <item name="stLength">지속 시간  LONG or SHORT</item>
            <item name="stGravity">위치  top or center</item>
            <item name="stRadius">가장자리 둥글게</item>
        </style>

    - Volley Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

    - Material Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

    - Material Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

    - Material Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

    - Material Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

    - Material Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

    - Material Library : Google에서 공식적으로 제공하는 디자인 라이브러리
      ```java
      dependencies {
        implementation 'com.google.android.material:material:1.8.0'
      }

<br />

  - #### **3-2. 내 정보 수정 화면**
    <img width="300" height="600" alt="프로필 변경 화면" src="https://github.com/60162143/itda/assets/33407087/57bb4774-5d9b-42b5-98ef-26d3772fafeb" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="내 정보 변경 화면" src="https://github.com/60162143/itda/assets/33407087/f7475274-3136-4961-948c-ce1063a40183" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="비밀번호 찾기 화면" src="https://github.com/60162143/itda/assets/33407087/8df6d924-e2bd-42ea-a393-303b2ce76d03" />

    - 프로필 이미지 변경 및 업로드 기능 구현 ( **ftp4j-1.6 라이브러리를 이용한 ftp 파일 업로드** )

    - 이름, 휴대폰 번호, 생일 변경 기능 구현
    - 비밀번호 변경, 찾기 기능 구현 ( SHA-256 알고리즘으로 암호화된 비밀번호 저장 )

<br />
