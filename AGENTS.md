# MemeQuiz Project Guidelines for AI Agents

이 문서는 MemeQuiz 프로젝트의 아키텍처, 코딩 스타일 및 기술적 제약 사항을 정의하여 AI 에이전트가 일관성 있는 코드를 작성하도록 돕습니다.

## 1. Project Overview
- **Name**: NikeDrawAlarm (스니커즈 어시스턴트)
- **Tech Stack**: Kotlin, Jetpack Compose, Hilt, Coroutines, Navigation 2, Kotlin Serialization.
- **Min SDK**: 24 (Android 7.0)

## 2. Architecture: MVI (Model-View-Intent)
모든 Feature 모듈은 `BaseViewModel`을 상속받아 MVI 패턴을 따릅니다.

### State, SideEffect, Event
- **UiState**: 화면의 상태를 정의하는 `data class`. `updateState { copy(...) }`를 통해 갱신.
- **UiSideEffect**: 토스트, 네비게이션 등 단발성 이벤트를 정의하는 `sealed class`. `sendEffect { ... }`로 발생.
- **UiEvent**: 사용자의 액션을 정의하는 `sealed class`. `handleEvents(event)`에서 처리.
- 만들어지는 State, SideEffect, Event는 [core:ui]/base 패키지에 있는 각 UiState, UiSideEffect, UiEvent 인터페이스를 구현.

### ViewModel Pattern
```kotlin
@HiltViewModel
class ExampleViewModel @Inject constructor(...) : BaseViewModel<ExampleUiState, ExampleUiSideEffect, ExampleUiEvent>(ExampleUiState()) {
    override fun handleEvents(event: ExampleUiEvent) {
        when (event) {
            is ExampleUiEvent.ClickSomething -> { /* logic */ }
        }
    }
}
```

## 3. Coding Standards & Rules

### 3.1 Domain & UseCase
모든 비즈니스 로직은 UseCase를 통해 캡슐화하며 다음 규칙을 준수한다.

- **Execution**: 모든 UseCase는 `suspend operator fun invoke(...)`를 사용하여 함수 호출 형태로 실행 가능해야 한다.
- **Return Type**: 반환값은 반드시 커스텀 `Result` 객체(Success/Error)로 래핑하여 처리한다.
- **Error Handling**: UseCase 내부에서 `try-catch`를 통해 예외를 포착하고, 정의된 `Result.ResultError`와 `Exception`을 반환한다.
- **Dependency Injection**: 반드시 `@Inject constructor`를 통해 Repository를 주입받는다.

**UseCase Pattern Example:**
```kotlin
class CreateQuizBookUseCase @Inject constructor(
    private val quizRepository: QuizRepository,
) {
    suspend operator fun invoke(quizBookInformation: QuizBookInformation): Result<String> {
        return try {
            val result = quizRepository.createQuizBook(quizBookInformation)
            // 성공 시 ResultSuccess로 래핑
            Result.ResultSuccess(result.data)
        } catch (e: Exception) {
            e.printStackTrace()
            // 실패 시 프로젝트 정의 Exception과 함께 ResultError 반환
            Result.ResultError(GenericException())
        }
    }
}
```

### 3.2 UI & Compose
- **Components**: `:core:ui` 모듈에 정의된 공통 컴포넌트를 우선 사용한다.
- **Dialogs**: `bottomsheetdialog-compose`를 활용하여 일관된 바텀시트 경험을 제공한다.
- **Previews**: 모든 Composable은 `@Preview`를 포함하여 렌더링 확인이 가능해야 한다.

### 3.3 Coroutines & Flow
- **State Collection in Compose**: Compose 화면에서 ViewModel의 StateFlow를 구독할 때는 반드시 라이프사이클을 인지하는 `collectAsStateWithLifecycle()`을 사용한다. 기존의 `collectAsState()`는 절대 사용하지 않는다.
- **Scope**: 비동기 작업은 반드시 `viewModelScope` 또는 호출부의 `coroutineScope` 내에서 실행하며, `GlobalScope`는 어떠한 경우에도 사용하지 않는다.
- **Dispatcher**: ViewModel 내부에서 `Dispatchers.IO` 등을 하드코딩하지 않는다. 필요한 경우 외부에서 주입받아 테스트 가능(Testable)하게 설계한다.

### 3.4 Compose Performance
- **Stateless UI**: 재사용 가능한 하위(Child) Composable 함수에는 ViewModel 자체를 파라미터로 넘기지 말고, 필요한 데이터(Primitive type)와 콜백(Lambda)만 넘긴다.
- **Stability**: Composable에 전달되는 모델(Data Class)이 변경 불가능함을 보장하기 위해 필요한 경우 콜렉션은 `kotlinx.collections.immutable`의 `ImmutableList` 등을 사용하거나 `@Stable`, `@Immutable` 어노테이션을 활용한다.
- **Remember**: 렌더링 중 불필요한 재계산이 일어나지 않도록 무거운 연산이나 객체 생성은 반드시 `remember`로 감싼다.

### 3.5 Dependency Injection (Hilt)
- **Injection Type**: 인터페이스 기반의 Repository를 Hilt 모듈에 등록할 때는 `@Provides` 대신 `@Binds`를 사용하여 보일러플레이트를 줄인다.
- **Annotations**: 새로운 화면(Activity/Fragment)이나 ViewModel을 생성할 때 `@AndroidEntryPoint`와 `@HiltViewModel` 어노테이션이 누락되지 않도록 반드시 확인한다.

## 4. Module Structure
우리 프로젝트는 역할과 책임(SRP)을 철저히 분리한 멀티 모듈 아키텍처를 따르며, 각 모듈의 역할은 다음과 같다. 에이전트는 코드를 추가하거나 수정할 때 이 경계를 절대 침범해서는 안 된다.

- **`:app`**: 앱의 진입점(Entry Point)이자 최상위 모듈. 모든 Feature 모듈을 모아 메인 네비게이션 그래프를 구성한다.
- **`:feature:[name]:impl`**: 각 도메인 기능의 UI 화면 및 ViewModel 로직이 구현되는 모듈.
- **`:core:model`**: 전역에서 사용되는 핵심 도메인 데이터 모델(Data Class).
- **`:core:domain`**: 앱의 비즈니스 로직을 담당하는 UseCase 및 Repository 인터페이스.
- **`:core:data`**: Repository의 실제 구현체(`Impl`) 및 원격/로컬 데이터 소스 조율 로직이 위치한다.
- **`:core:network`**: API 통신 인터페이스 및 네트워크 통신용 DTO (Data Transfer Objects).
- **`:core:datastore`**: Jetpack Preferences DataStore를 사용하여 로컬 설정값을 관리한다. 외부 모듈에는 반드시 **Interface 형태**로만 노출되며, 실제 DataStore 구현체는 모듈 내부에 은닉(`internal`)한다.
- **`:core:navigation`**: Jetpack Navigation 3.x 기반의 전역 네비게이션 핵심 로직을 담당한다. 화면 이동을 통제하는 커스텀 `Navigator` 인터페이스/구현체와 상태를 관리하는 `NavigationState` 객체가 위치한다.
- **`:core:designsystem`**: Material 3 기반 프로젝트 기초 디자인 시스템. Color, Typography, Theme 및 순수 기초 단위의 UI 컴포넌트만 위치한다.
- **`:core:ui`**: `:core:designsystem`의 기초 요소들과 `:core:model`을 결합하여 만든, 조금 더 복잡하고 도메인에 종속적인 공통 UI 컴포넌트(예: 전역 바텀시트, 복합 카드 뷰 등)가 위치한다.
- **`:core:common`**: 순수 Kotlin 모듈. Android 프레임워크 의존성을 절대 포함하지 않으며, 전역에서 사용되는 공통 유틸리티 함수와 비즈니스 로직 결과를 래핑하는 커스텀 `Result` 클래스 등이 위치한다.

### 4.1 Feature Module Architecture (`api` vs `impl`)
모든 Feature 모듈은 Now In Android(NIA) 스타일을 차용하여 의존성 결합도를 낮추기 위해 `api`와 `impl` 모듈로 철저히 분리한다.

#### 1. `feature:[name]:api` (Interface & Routing)
- **역할**: 외부 모듈에서 해당 피처로 접근하기 위한 명세 및 라우팅 정보만 제공한다.
- **NavKey (Route)**: 화면 이동 목적지와 전달할 데이터를 담는 객체는 반드시 `@Serializable`을 선언한 `*NavKey`로 정의한다. (예: `QuizNavKey`)
- **Navigation Extension**: 외부에서 접근 가능하도록 `Navigator` 커스텀 객체에 대한 확장 함수를 정의한다.
    - Naming Rule: `fun Navigator.navigateTo[화면이름](...)`

#### 2. `feature:[name]:impl` (Implementation)
- **역할**: 실제 UI 화면과 비즈니스 로직(ViewModel)을 구현한다.
- **접근 제한자**: 화면 단위의 구성요소(`Route`, `Screen`, `ViewModel`)는 외부에서 직접 참조하지 못하도록 반드시 `internal`로 선언한다.
- **진입점 (Entry)**: `api` 모듈에 정의된 `NavKey`를 실제 화면과 연결하는 진입점 함수를 노출한다.
    - pacakge: `navigation`
    - Naming Rule: `[화면이름]EntryProvider`, `fun EntryProviderScope<NavKey>.add[화면이름]Entry(navigator: Navigator)`

#### 3. UI Layer Hierarchy (Entry → Route → Screen)
Feature 내의 새로운 화면을 구현할 때는 반드시 다음의 **3계층 구조**를 엄격하게 분리하여 작성해야 한다.

- **Entry 계층**: `entry<NavKey> { key -> ... }` 블록 내에서 동작. `NavKey` 파라미터를 추출하고, `Route`를 호출하며, `navigator`를 이용한 상위 수준의 네비게이션 콜백(예: `goBack`)을 처리한다.
- **Route 계층**: `ViewModel`을 주입받아 비즈니스 로직과 상태를 관리한다. `uiState`를 구독하고 `SideEffect`를 수집(Collect)하여 네비게이션이나 토스트 등을 처리한 뒤, 순수 데이터와 이벤트 핸들러만 `Screen`으로 전달한다.
- **Screen 계층**: 오직 UI 렌더링만 담당하는 Stateless Composable이다. ViewModel을 직접 주입받지 않으며, `uiState` 모델 객체와 `onEvent: (Event) -> Unit` 람다만을 파라미터로 받는다.

**[UI 계층화 패턴 예시 (AI는 이 패턴을 반드시 따를 것)]**
```kotlin
// 1. Entry: 진입점 및 네비게이션 처리
fun EntryProviderScope<NavKey>.addQuizEntry(navigator: Navigator) {
    entry<QuizNavKey> { key ->
        QuizRoute(
            quizId = key.id,
            onBack = { navigator.goBack() },
            navigateToDetail = { id -> navigator.navigateToQuizDetail(id, popUpTo = key) }
        )
    }
}

// 2. Route: 상태 관리 및 사이드 이펙트 처리
@Composable
internal fun QuizRoute(
    quizId: String,
    onBack: () -> Unit,
    navigateToDetail: (String) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // SideEffect 처리
    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            is QuizSideEffect.NavigateBack -> onBack()
            is QuizSideEffect.NavigateToDetail -> navigateToDetail(effect.id)
        }
    }

    // Screen으로 상태와 이벤트 위임
    QuizScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvents
    )
}

// 3. Screen: 순수 UI 렌더링 (Stateless)
@Composable
internal fun QuizScreen(
    uiState: QuizUiState,
    onEvent: (QuizEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    // Scaffold 및 UI 컴포넌트 구성
    Button(onClick = { onEvent(QuizEvent.OnSubmitClicked) }) {
        Text("제출")
    }
}
```
# Git Commit Message Rules

당신은 이 프로젝트의 코드를 작성하고 커밋 메시지를 생성할 때, 반드시 아래의 규칙(Conventional Commits)을 엄격하게 지켜야 합니다.

## 1. Commit Message Format
커밋 메시지는 다음 구조를 따릅니다:
```
<type>(<optional scope>): <subject>

<body> (optional)

<footer> (optional)
```

## 2. Allowed Types (반드시 소문자 영어로 작성)
- `feat`: 새로운 기능 추가 (Feature)
- `fix`: 버그 수정 (Bug fix)
- `refactor`: 코드 리팩토링 (기능 변경 없이 코드 구조나 로직 개선)
- `design`: UI/UX 디자인 변경 (CSS, Compose UI 수정 등)
- `style`: 코드 포맷팅, 세미콜론 누락 등 (비즈니스 로직 변경 없음)
- `docs`: README 등 문서 수정
- `test`: 테스트 코드 추가 및 리팩토링
- `chore`: 빌드 설정(build.gradle), 패키지 매니저 수정, 라이브러리 업데이트 등

## 3. Subject Rules (제목 규칙)
- **언어:** 제목은 반드시 **한국어**로 작성합니다.
- **길이:** 50자를 넘지 않도록 간결하게 작성합니다.
- **종결어미:** 명사형으로 끝나거나, '~함', '~수정', '~추가' 형태로 작성합니다. (예: `feat: 로그인 팝업 추가`, `fix: 퀴즈 로딩 크래시 해결`)
- **마침표 금지:** 제목 끝에 마침표(.)를 찍지 않습니다.

## 4. Body Rules (본문 규칙 - 필요시 작성)
- 제목만으로 설명이 부족할 때 한 줄 띄우고 본문을 작성합니다.
- 본문은 '어떻게(How)' 코드를 짰는지가 아니라, **'무엇을(What)' 그리고 '왜(Why)' 변경했는지**에 집중해서 작성합니다.

## 5. Scope (선택 사항)
- 멀티 모듈 프로젝트이므로, 변경된 기능이나 모듈의 이름을 괄호 안에 명시하는 것을 권장합니다.
- 예시: `feat(quiz): 퀴즈 추가 바텀시트 구현`, `refactor(core): 다이얼로그 전역 상태 관리 로직 개선`