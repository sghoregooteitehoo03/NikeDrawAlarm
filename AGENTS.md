# NikeDrawAlarm Project Guidelines for AI Agents

This document defines the architecture, coding style, and technical constraints of the NikeDrawAlarm project to guide AI agents in writing consistent code.

## 1. Project Overview

* **Name**: NikeDrawAlarm (Sneakers Assistant)
* **Tech Stack**: Kotlin, Jetpack Compose, Hilt, Coroutines, Navigation 2, Kotlin Serialization.
* **Min SDK**: 24 (Android 7.0)

## 2. Architecture: MVI (Model-View-Intent)

All Feature modules must inherit `BaseViewModel` and follow the MVI pattern.

### State, SideEffect, Event

* **UiState**: A `data class` defining the UI state. Update via `updateState { copy(...) }`.
* **UiSideEffect**: A `sealed class` for one-off events (e.g., toasts, navigation). Trigger via `sendEffect { ... }`.
* **UiEvent**: A `sealed class` defining user actions. Process via `handleEvents(event)`.
* All generated State, SideEffect, and Event classes must implement their respective interfaces located in the `[core:ui]/base` package.

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

All business logic must be encapsulated in UseCases following these rules:

* **Execution**: Must be callable as a function using `suspend operator fun invoke(...)`.
* **Return Type**: Must return a custom wrapped `Result` object (`Success`/`Error`).
* **Error Handling**: Use `try-catch` blocks internally. Return `Result.ResultError` with a defined custom `Exception` on failure.
* **Dependency Injection**: Repositories must be injected via `@Inject constructor`.

**UseCase Pattern Example:**

```kotlin
class CreateQuizBookUseCase @Inject constructor(
    private val quizRepository: QuizRepository,
) {
    suspend operator fun invoke(quizBookInformation: QuizBookInformation): Result<String> {
        return try {
            val result = quizRepository.createQuizBook(quizBookInformation)
            Result.ResultSuccess(result.data)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.ResultError(GenericException())
        }
    }
}

```

### 3.2 UI & Compose

* **Components**: Prioritize using common components defined in the `:core:ui` module.
* **Dialogs**: Use `bottomsheetdialog-compose` for consistent bottom sheet UX.
* **Previews**: All Composables must include a `@Preview` annotation for rendering checks.

### 3.3 Coroutines & Flow

* **State Collection in Compose**: Always use `collectAsStateWithLifecycle()` when observing a ViewModel's StateFlow in Compose. **Never** use `collectAsState()`.
* **Scope**: Execute asynchronous tasks within `viewModelScope` or the caller's `coroutineScope`. **Never** use `GlobalScope`.
* **Dispatcher**: Do not hardcode dispatchers (e.g., `Dispatchers.IO`) inside ViewModels. Inject them externally to ensure testability.

### 3.4 Compose Performance

* **Stateless UI**: Do not pass ViewModels directly into reusable child Composables. Pass only required primitive data and callbacks (lambdas).
* **Stability**: Ensure models passed to Composables are immutable. Use `@Stable`, `@Immutable` annotations, or `kotlinx.collections.immutable` (e.g., `ImmutableList`) where necessary.
* **Remember**: Always wrap heavy computations or object creations in `remember` to prevent unnecessary recalculations during recomposition.

### 3.5 Dependency Injection (Hilt)

* **Injection Type**: Use `@Binds` instead of `@Provides` when registering interface-based Repositories in Hilt modules to reduce boilerplate.
* **Annotations**: Always ensure `@AndroidEntryPoint` and `@HiltViewModel` annotations are present when creating new Activities, Fragments, or ViewModels.

## 4. Module Structure

This project follows a strict multi-module architecture based on the Single Responsibility Principle (SRP). Agents **must not** violate these module boundaries when adding or modifying code.

* **`:app`**: The entry point. Assembles all feature modules into the main navigation graph.
* **`:feature:[name]:impl`**: Contains UI screens and ViewModel logic for specific domain features.
* **`:core:model`**: Global core domain data models (`data class`).
* **`:core:domain`**: Business logic, UseCases, and Repository interfaces.
* **`:core:data`**: Actual Repository implementations (`Impl`) and data source orchestration.
* **`:core:network`**: API communication interfaces and Data Transfer Objects (DTOs).
* **`:core:datastore`**: Manages local settings via Jetpack Preferences DataStore. Must be exposed to external modules strictly as an **Interface**. The actual implementation must remain hidden (`internal`) within the module.
* **`:core:navigation`**: Global navigation logic based on Jetpack Navigation 3.x. Contains custom `Navigator` interfaces/impls and the `NavigationState` object.
* **`:core:designsystem`**: Base design system (Material 3). Contains only Colors, Typography, Themes, and pure foundational UI components.
* **`:core:ui`**: Domain-dependent common UI components (e.g., global bottom sheets, complex cards) combining `:core:model` and `:core:designsystem`.
* **`:core:common`**: Pure Kotlin module (No Android framework dependencies). Contains global utility functions and custom `Result` wrapper classes.

### 4.1 Feature Module Architecture (`api` vs `impl`)

Following the Now In Android (NIA) style, all feature modules are strictly separated into `api` and `impl` modules to reduce dependency coupling.

#### 1. `feature:[name]:api` (Interface & Routing)

* **Role**: Provides specifications and routing information for external module access.
* **NavKey (Route)**: Navigation destinations and arguments must be defined as `*NavKey` objects annotated with `@Serializable`. (e.g., `QuizNavKey`)
* **Navigation Extension**: Define extension functions on the custom `Navigator` object for external access.
* Naming Rule: `fun Navigator.navigateTo[ScreenName](...)`



#### 2. `feature:[name]:impl` (Implementation)

* **Role**: Implements the actual UI screens and business logic (ViewModel).
* **Access Modifiers**: Screen-level components (`Route`, `Screen`, `ViewModel`) must be declared as `internal` to prevent direct external access.
* **Entry Point**: Exposes functions connecting the `NavKey` defined in `api` to the actual screen.
* Package: `navigation`
* Naming Rule: `[ScreenName]EntryProvider`, `fun EntryProviderScope<NavKey>.add[ScreenName]Entry(navigator: Navigator)`



#### 3. UI Layer Hierarchy (Entry → Route → Screen)

When implementing new screens within a Feature, you **must strictly enforce** the following 3-layer architecture:

* **Entry Layer**: Operates within the `entry<NavKey> { key -> ... }` block. Extracts `NavKey` parameters, calls the `Route`, and handles high-level navigation callbacks using the `navigator` (e.g., `goBack`).
* **Route Layer**: Injects the `ViewModel` to manage business logic and state. Observes `uiState`, collects `SideEffect` for navigation/toasts, and passes only pure data and event handlers to the `Screen`.
* **Screen Layer**: A Stateless Composable responsible purely for UI rendering. Does not inject ViewModels. Accepts only the `uiState` model and an `onEvent: (Event) -> Unit` lambda as parameters.

**[UI Layer Pattern Example (Agents MUST follow this pattern)]**

```kotlin
// 1. Entry: Entry point and navigation handling
fun EntryProviderScope<NavKey>.addQuizEntry(navigator: Navigator) {
    entry<QuizNavKey> { key ->
        QuizRoute(
            quizId = key.id,
            onBack = { navigator.goBack() },
            navigateToDetail = { id -> navigator.navigateToQuizDetail(id, popUpTo = key) }
        )
    }
}

// 2. Route: State management and SideEffect handling
@Composable
internal fun QuizRoute(
    quizId: String,
    onBack: () -> Unit,
    navigateToDetail: (String) -> Unit,
    viewModel: QuizViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    
    // Handle SideEffects
    CollectSideEffect(viewModel.sideEffect) { effect ->
        when (effect) {
            is QuizSideEffect.NavigateBack -> onBack()
            is QuizSideEffect.NavigateToDetail -> navigateToDetail(effect.id)
        }
    }

    // Delegate state and events to Screen
    QuizScreen(
        uiState = uiState,
        onEvent = viewModel::handleEvents
    )
}

// 3. Screen: Pure UI Rendering (Stateless)
@Composable
internal fun QuizScreen(
    uiState: QuizUiState,
    onEvent: (QuizEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Button(onClick = { onEvent(QuizEvent.OnSubmitClicked) }) {
        Text("Submit")
    }
}

```

# Git Commit Message Rules

When writing code and generating commit messages for this project, you must strictly adhere to the following Conventional Commits guidelines.

## 1. Commit Message Format

```text
<type>(<optional scope>): <subject>

<body> (optional)

<footer> (optional)

```

## 2. Allowed Types (Must be lowercase English)

* `feat`: Add new features
* `fix`: Bug fixes
* `refactor`: Code refactoring (structure/logic improvements without feature changes)
* `design`: UI/UX design changes (CSS, Compose UI, etc.)
* `style`: Code formatting, missing semicolons, etc. (no business logic changes)
* `docs`: Documentation updates (e.g., README)
* `test`: Add or refactor test code
* `chore`: Build configuration (build.gradle), package managers, library updates, etc.

## 3. Subject Rules

* **Language:** The subject line must be written in **Korean**.
* **Length:** Keep it concise, under 50 characters.
* **Ending:** End with a noun form or suffixes like '~함', '~수정', '~추가'. (e.g., `feat: 로그인 팝업 추가`, `fix: 퀴즈 로딩 크래시 해결`)
* **No Period:** Do not put a period (.) at the end of the subject line.

## 4. Body Rules (Optional)

* Leave a blank line after the subject before writing the body.
* Focus on **'What'** and **'Why'** the code was changed, not 'How'.

## 5. Scope (Optional)

* Given the multi-module nature of the project, specifying the modified feature or module name in parentheses is highly recommended.
* Example: `feat(quiz): 퀴즈 추가 바텀시트 구현`, `refactor(core): 다이얼로그 전역 상태 관리 로직 개선`