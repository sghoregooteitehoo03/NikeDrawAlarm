## 침투브월드 - CHIMHA
![그래픽 이미지](https://github.com/sghoregooteitehoo03/ChimtubeWorld/blob/master/image/graphic_image.png)

침착맨님의 모든 콘텐츠를 한 곳에서 쉽고 편하게 무한으로 즐겨보세요!


**침투브월드 - CHIMHA** 를 통해 침착맨님의 다양한 매력 넘치는 콘텐츠들을 모두 한 곳에서 손쉽게 발견하고, 편리하게 즐겨보세요!  
침착맨의 트위치, 유튜브, 웹툰, 카페, 그리고 다양한 굿즈까지 한눈에 확인하고 놓치지 않아요.

## 다운로드
<a href='https://play.google.com/store/apps/details?id=com.sghore.chimtubeworld&hl=ko-KR&pcampaignid=pcampaignidMKT-Other-global-all-co-prtnr-py-PartBadge-Mar2515-1'><img alt='다운로드하기 Google Play' src='https://play.google.com/intl/en_us/badges/static/images/badges/ko_badge_web_generic.png' height="80"/></a>

## 기능
- 침착맨님의 다양한 유튜브 채널과 그동안 출연한 모든 영상들을 간편하게 찾아볼 수 있습니다.  

- 침착맨님의 현재 방송 여부뿐만 아니라, 배도라지 크루 멤버들의 트위치 방송도 함께 확인하고 손쉽게 시청하실 수 있습니다.

- 이말년 작가로 활동하던 시기에 네이버에서 연재된 웹툰을 한데 모아 쉽게 즐길 수 있습니다.

- 침착맨님의 팬카페의 다양한 게시글을 카테고리별로 편리하게 탐색하며, 침하하와 함께 즐겨보세요.

- 침착맨님의 다양한 굿즈에 대한 정보를 확인하고, 마음에 드는 굿즈를 찾아보세요.

- 유튜브 영상을 시청하다가 이어보고 싶은 부분이나 특별한 순간이 있다면, 해당 앱으로 손쉽게 공유하고 북마크를 만들어 콘텐츠를 더욱 편리하게 즐겨보세요.

## 스크린샷
![스크린샷](https://github.com/sghoregooteitehoo03/ChimtubeWorld/blob/master/image/screenshot.png)

## 아키텍쳐 및 라이브러리
- 아키텍처
   - MVVM 패턴: (View - ViewModel - Model)
   - [App Architecture 패턴](https://developer.android.com/topic/architecture/intro): (UI Layer - Domain Layer - Data Layer)
     
- Jetpack
  - ViewModel: UI의 상태값을 관리하며 UI의 이벤트들을 처리합니다.
  - Paging3: 로컬 데이터베이스나 네트워크에서 가져온 데이터를 페이징하여 데이터를 처리합니다.
  - Navigation: 화면 구성 및 화면전환에 관련된 다양한 기능을 제공합니다.
  - Browser: 앱 내에서 외부 브라우저를 호출하거나 웹뷰를 제공합니다.
  - Room: SQL 기능을 이용하여 데이터베이스를 이용합니다.
  - Compose: 기존의 XML레이아웃을 이용하지 않고, Kotlin 코드를 통해 UI 화면을 제작합니다.
  - [Hilt](https://dagger.dev/hilt/): 의존성 주입을 통해 보일러플레이트 코드를 줄여줍니다.
    
- [Retrofit](https://github.com/square/retrofit): Android 및 Java를 위한 HTTP 클라이언트입니다.

- Firebase
  - Firestore: Firebase에서 제공하는 NoSql 기반의 클라우드 데이터베이스를 이용할 수 있는 기능을 제공합니다.

- [Jsoup](https://jsoup.org/): HTML 및 XML 작업을 간단하게 만드는 라이브러리로 데이터 파싱, 추출 등의 다양한 기능을 이용할 수 있습니다.

- [Coil](https://github.com/coil-kt/coil): 네트워크로부터 이미지를 로드합니다.

- Custom Views
  - [compose-collapsing-toolbar](https://github.com/onebone/compose-collapsing-toolbar): Jetapck Compose용 Collapsing Toolbar를 제공합니다.
  - [compose-shimmer](https://github.com/valentinilk/compose-shimmer): Jetpack Compose에 shimmer 효과를 제공합니다.

## API
### [Twitch API](https://dev.twitch.tv/)
### [Youtube API](https://developers.google.com/youtube)
