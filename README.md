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
<img width="300" height="600" alt="로그인 화면" src="https://github.com/60162143/itda/assets/33407087/e6f0263a-90e6-44e9-a332-bde8da0e009c" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="회원가입 화면" src="https://github.com/60162143/itda/assets/33407087/c579eeae-1aee-4485-9833-280f4d078933" />

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

    - **Glide Library** : 이미지를 빠르고 효율적으로 불러올 수 있게 도와주는 라이브러리
      ```java
        Glide.with(holder.itemView)                     // View, Fragment 혹은 Activity로부터 Context를 GET
                .load(Uri.parse(photo.getPhotoPath()))  // 이미지를 로드, 다양한 방법으로 이미지를 불러올 수 있음
                .placeholder(R.drawable.preImage)       // 이미지가 로드되기 전 보여줄 이미지 설정
                .error(R.drawable.errorImage)           // 리소스를 불러오다가 에러가 발생했을 때 보여줄 이미지 설정
                .fallback(R.drawable.nullImage)         // Load할 URL이 null인 경우 등 비어있을 때 보여줄 이미지 설정
                .into(holder.photoImage);               // 이미지를 보여줄 View를 지정
      ```
    - **Styleable Toast Library** : 폰트, 배경색, 아이콘 등 토스트의 전반적인 디자인을 themes.xml에서 원하는 대로 지정해 줄 수 있는 라이브러리
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
      ```
    - **Volley Library** : 네트워킹을 보다 쉽고 빠르게 만들어주는 HTTP 라이브러리
      ```java
        public void sendRequest(){
          String url = "https://www.google.co.kr";
  
          //StringRequest를 만듬 (파라미터구분을 쉽게하기위해 엔터를 쳐서 구분하면 좋다)
          //StringRequest는 요청객체중 하나이며 가장 많이 쓰인다고한다.
          //요청객체는 다음고 같이 보내는방식(GET,POST), URL, 응답성공리스너, 응답실패리스너 이렇게 4개의 파라미터를 전달할 수 있다.(리퀘스트큐에 ㅇㅇ)
          //화면에 결과를 표시할때 핸들러를 사용하지 않아도되는 장점이있다.
          StringRequest request = new StringRequest(
                  Request.Method.GET,
                  url,
                  new Response.Listener<String>() {  //응답을 문자열로 받아서 여기다 넣어달란말임(응답을 성공적으로 받았을 떄 이메소드가 자동으로 호출됨
                      @Override
                      public void onResponse(String response) {
                          println("응답 => " + response);
                      }
                  },
                  new Response.ErrorListener(){ //에러발생시 호출될 리스너 객체
                      @Override
                      public void onErrorResponse(VolleyError error) {
                          println("에러 => "+ error.getMessage());
                      }
                  }
          ){
              //만약 POST 방식에서 전달할 요청 파라미터가 있다면 getParams 메소드에서 반환하는 HashMap 객체에 넣어줍니다.
              //이렇게 만든 요청 객체는 요청 큐에 넣어주는 것만 해주면 됩니다.
              //POST방식으로 안할거면 없어도 되는거같다.
              @Override
              protected Map<String, String> getParams() throws AuthFailureError {
                  Map<String, String> params = new HashMap<String, String>();
                  return params;
              }
          };
  
          //아래 add코드처럼 넣어줄때 Volley라고하는게 내부에서 캐싱을 해준다, 즉, 한번 보내고 받은 응답결과가 있으면
          //그 다음에 보냈을 떄 이전 게 있으면 그냥 이전거를 보여줄수도  있다.
          //따라서 이렇게 하지말고 매번 받은 결과를 그대로 보여주기 위해 다음과같이 setShouldCache를 false로한다.
          //결과적으로 이전 결과가 있어도 새로 요청한 응답을 보여줌
          request.setShouldCache(false);
          AppHelper.requestQueue.add(request);
          println("요청 보냄!!");
        }
      ```
    - **TedPermission Library** : 안드로이드에서 퍼미션 권한 관리에 도움을 주는 라이브러리
      ```java
        PermissionListener permissionlistener = new PermissionListener() {
              @Override
              public void onPermissionGranted() {
                  Toast.makeText(MainActivity.this, "권한 허가", Toast.LENGTH_SHORT).show();
              }
  
              @Override
              public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                  Toast.makeText(MainActivity.this, "권한 거부\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
              }
  
          };
  
          TedPermission.with(this)
                  .setPermissionListener(permissionlistener)
                  .setRationaleMessage("구글 로그인을 하기 위해서는 주소록 접근 권한이 필요해요")
                  .setDeniedMessage("왜 거부하셨어요...\n하지만 [설정] > [권한] 에서 권한을 허용할 수 있어요.")
                  .setPermissions(Manifest.permission.READ_CONTACTS)
                  .check();
      ```
    - **Kakao Login API** : 카카오에서 제공하는 카카오 로그인 API
      ```java
        // 카카오톡이 설치되어 있는지 확인하는 메서드 , 카카오에서 제공함. 콜백 객체를 이용합.
        Function2<OAuthToken,Throwable,Unit> callback =new Function2<OAuthToken, Throwable, Unit>() {
            @Override
            // 콜백 메서드 ,
            public Unit invoke(OAuthToken oAuthToken, Throwable throwable) {
                Log.e(TAG,"CallBack Method");
                //oAuthToken != null 이라면 로그인 성공
                if(oAuthToken!=null){
                    // 토큰이 전달된다면 로그인이 성공한 것이고 토큰이 전달되지 않으면 로그인 실패한다.
                    updateKakaoLoginUi();

                }else {
                    //로그인 실패
                    Log.e(TAG, "invoke: login fail" );
                }

                return null;
            }
        };
      ```
    - **Kakao Map API** : 카카오에서 제공하는 카카오 지도 API
      ```java
        // 지도 띄우기
        mapView = new MapView(this);
        mapViewContainer = (ViewGroup) findViewById(R.id.map_view);
        mapViewContainer.addView(mapView);
        mapView.setMapViewEventListener(this);
        mapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading);
      ```

    - **BootPay Payment API** : 부트페이에서 제공하는 PG 결제 연동 API
      ```java
        // 결제호출
        BootUser bootUser = new BootUser().setPhone("010-1234-5678");
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});

        Bootpay.init(getFragmentManager())
                .setApplicationId([ Android SDK용 Application ID ]) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(PG.) // 결제할 PG 사
                .setMethod(Method.) // 결제수단
                .setContext(this)
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
                .setUX(UX.PG_DIALOG)
                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setName("맥북프로's 임다") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
                .setPrice(10000) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        if (0 < stuck) Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {

                        Log.d("cancel", message);
                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {
                        Log.d("error", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                    @Override
                    public void onClose(String message) {
                        Log.d("close", "close");
                    }
                })
                .request();
      ```
    - **SMTP Mail Library** : Javax의 기본 Mail 라이브러리
      ```java
        public GMailSender(String user, String password) {
          this.user = user;
          this.password = password;
          emailCode = createEmailCode();
          Properties props = new Properties();
          props.setProperty("mail.transport.protocol", "smtp");
          props.setProperty("mail.host", mailhost);
          props.put("mail.smtp.auth", "true");
          props.put("mail.smtp.port", "465");
          props.put("mail.smtp.socketFactory.port", "465");
          props.put("mail.smtp.socketFactory.class",
                  "javax.net.ssl.SSLSocketFactory");
          props.put("mail.smtp.socketFactory.fallback", "false");
          props.setProperty("mail.smtp.quitwait", "false");
  
          //구글에서 지원하는 smtp 정보를 받아와 MimeMessage 객체에 전달해준다.
          session = Session.getDefaultInstance(props, this);
      }
      ```
    - **ftp4j-1.6 Library** : Ftp 파일 전송 라이브러
      ```java
        public void uploadFile(File fileName){
 
          FTPClient client = new FTPClient();
   
          try {
              client.connect(FTP_HOST,21);//ftp 서버와 연결, 호스트와 포트를 기입
              client.login(FTP_USER, FTP_PASS);//로그인을 위해 아이디와 패스워드 기입
              client.setType(FTPClient.TYPE_BINARY);//2진으로 변경
              client.changeDirectory("uploadtest/");//서버에서 넣고 싶은 파일 경로를 기입
   
              client.upload(fileName, new MyTransferListener());//업로드 시작
   
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(getApplicationContext(),"성공",Toast.LENGTH_SHORT).show();
                  }
              });
   
          } catch (Exception e) {
   
              handler.post(new Runnable() {
                  @Override
                  public void run() {
                      Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_SHORT).show();
                  }
              });
   
              e.printStackTrace();
              try {
                  client.disconnect(true);
              } catch (Exception e2) {
                  e2.printStackTrace();
              }
          }
      }
    ```
<br />

  - #### **3-2. 데이터 크롤링**
    <img width="300" height="600" alt="프로필 변경 화면" src="https://github.com/60162143/itda/assets/33407087/57bb4774-5d9b-42b5-98ef-26d3772fafeb" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="내 정보 변경 화면" src="https://github.com/60162143/itda/assets/33407087/f7475274-3136-4961-948c-ce1063a40183" /> &nbsp;&nbsp;&nbsp;&nbsp; <img width="300" height="600" alt="비밀번호 찾기 화면" src="https://github.com/60162143/itda/assets/33407087/8df6d924-e2bd-42ea-a393-303b2ce76d03" />

    - 프로필 이미지 변경 및 업로드 기능 구현 ( **ftp4j-1.6 라이브러리를 이용한 ftp 파일 업로드** )

    - 이름, 휴대폰 번호, 생일 변경 기능 구현
    - 비밀번호 변경, 찾기 기능 구현 ( SHA-256 알고리즘으로 암호화된 비밀번호 저장 )

<br />
