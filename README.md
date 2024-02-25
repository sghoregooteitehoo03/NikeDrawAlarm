## 스니커즈 어시스턴트
![그래픽 이미지](https://github.com/sghoregooteitehoo03/NikeDrawAlarm/blob/master/image/graphic_image.png)

스니커즈의 정보들을 손쉽게 얻을 수 있도록 도와드리겠습니다.

**스니커즈 어시스턴트**는 사용자들에게 필요한 스니커즈 정보만을 제공하여 원하는 제품에 대한 정보를 편리하게 얻을 수 있도록 도움을 드립니다.  
뿐만 아니라, 알림 기능을 통해 제품 출시에 대한 소식도 놓치지 않도록 지원하고 있습니다. 이제 더욱 편리하고 즐거운 스니커즈 탐험을 시작해보세요!

## 다운로드
<a href='https://play.google.com/store/apps/details?id=com.nikealarm.nikedrawalarm&hl=ko-KR&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='다운로드하기 Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/ko_badge_web_generic.png' height="80"/></a>

## 기능
- **카테고리 기능**  
다양한 스니커즈 목록을 카테고리로 나눠 원하는 제품을 손쉽게 찾아보세요.

- **UPCOMING 기능**  
앞으로 출시될 스니커즈들의 일정을 미리 확인하세요.

- **제품 알림 기능**  
곧 출시 예정인 제품에 대한 알림을 설정하여 제품 출시 소식을 신속하게 받아보세요.

- **DRAW 신제품 출시 알림 기능**  
응모 확인의 번거로움을 덜어드릴게요.
새로운 응모 제품이 나올 때 푸시 알림으로 알려드립니다.

## 스크린샷
![스크린샷](https://github.com/sghoregooteitehoo03/NikeDrawAlarm/blob/master/image/screenshot.png)

## 아키텍쳐 및 라이브러리
- 아키텍처
   - MVVM 패턴: (View - ViewModel - Model)
   - [App Architecture 패턴](https://developer.android.com/topic/architecture/intro): (UI Layer - Domain Layer - Data Layer)
     
- Jetpack
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.
  - WorkManager: 안정적인 백그라운드 작업을 처리하도록 도와줍니다.
  - Paging3: 로컬 데이터베이스나 네트워크에서 가져온 데이터를 페이징하여 데이터를 처리합니다.
  - Navigation: 화면 구성 및 화면전환에 관련된 다양한 기능을 제공합니다.
  - Browser: 앱 내에서 외부 브라우저를 호출하거나 웹뷰를 제공합니다.
  - Room: SQL 기능을 이용하여 데이터베이스를 이용합니다.
  - Datastore: 키-값 유형의 데이터를 읽고 저장하는 데이터 저장소입니다.
  - Compose: 기존의 XML레이아웃을 이용하지 않고, Kotlin 코드를 통해 UI 화면을 제작합니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입을 통해 보일러플레이트 코드를 줄여줍니다.
    
- [Retrofit](https://github.com/square/retrofit): Android 및 Java를 위한 HTTP 클라이언트입니다.

- [Coil](https://github.com/coil-kt/coil), [Picasso](https://github.com/square/picasso): 네트워크로부터 이미지를 로드합니다.

- Custom Views
  - [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar): Jetapck Compose용 Collapsing Toolbar를 제공합니다.
  - [compose-shimmer](https://github.com/valentinilk/compose-shimmer): Jetpack Compose에 shimmer 효과를 제공합니다.
