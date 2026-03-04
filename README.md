## 스니커즈 어시스턴트
![그래픽 이미지](https://github.com/sghoregooteitehoo03/NikeDrawAlarm/blob/master/image/graphic_image.png)

스니커즈의 정보들을 손쉽게 얻을 수 있도록 도와드리겠습니다.

**스니커즈 어시스턴트**는 사용자들에게 필요한 스니커즈 정보만을 제공하여 원하는 제품에 대한 정보를 편리하게 얻을 수 있도록 도움을 드립니다.  
뿐만 아니라, 알림 기능을 통해 제품 출시에 대한 소식도 놓치지 않도록 지원하고 있습니다. 이제 더욱 편리하고 즐거운 스니커즈 탐험을 시작해보세요!

## 다운로드
<a href='https://play.google.com/store/apps/details?id=com.nikealarm.nikedrawalarm&hl=ko-KR&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='다운로드하기 Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/ko_badge_web_generic.png' height="80"/></a>

## 아키텍쳐
![아키텍쳐](https://github.com/sghoregooteitehoo03/NikeDrawAlarm/blob/master/image/architecture.png)
- ### UI Layer ###
  사용자에게 화면을 그리는 역할을 담당합니다 ViewModel을 통해 전달받은 UI State를 렌더링합니다.  
  사용자 입력은 Event 형태로 받아 ViewModel에 적절한 함수를 호출합니다.
- ### Domain Layer ###
  앱의 핵심 비즈니스 로직을 담당하는 영역입니다. UseCase를 통해 데이터를 UI에서 사용 가능한 형태로 정제하여 UI Layer로 전달합니다.
- ### Data Layer ###
  Repository 패턴을 적용하여 Network 및 Room DB에 접근합니다. 외부 데이터 소스로부터 데이터를 가져온 뒤 상위 계층에 전달합니다.

## 외부 라이브러리
- Jetpack
  - Browser: 앱 내에서 외부 브라우저를 호출하거나 웹뷰를 제공합니다.
  - Compose: 기존 XML 레이아웃을 선언형 UI로 리팩토링하여 직관적이고 유연한 화면을 구현했습니다.
  - Datastore: 유저의 알림 설정 값 등 가벼운 환경설정 데이터를 비동기적으로 안전하게 저장합니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입(DI)을 통해 객체 간의 결합도를 낮추고 유연한 아키텍처를 설계했습니다.
  - Navigation: 단일 액티비티 구조에서 Compose 기반의 화면 구성 및 원활한 전환을 담당합니다.
  - Paging3: 스니커즈 리스트를 한 번에 요청오지 않고, 페이징 처리하여 메모리 사용량을 최적화했습니다.
  - Room: 좋아요 및 최근에 본 상품들을 저장시킵니다.
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.
  - WorkManager: 앱이 종료된 상태에서도 신규 DRAW 일정을 자동 탐색하고 푸시 알림을 안정적으로 발송합니다.
    
- [Retrofit](https://github.com/square/retrofit): 외부 API와의 안정적인 HTTP 통신을 수행합니다.

- [Coil](https://github.com/coil-kt/coil): Compose 환경에 최적화된 비동기 이미지 로딩을 처리합니다.

- Custom Views
  - [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar): Jetapck Compose용 Collapsing Toolbar를 제공합니다.
  - [compose-shimmer](https://github.com/valentinilk/compose-shimmer): Jetpack Compose에 shimmer 효과를 제공합니다.

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
