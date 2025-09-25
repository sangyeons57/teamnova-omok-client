# Repository Guidelines

## Project Structure & Module Organization
TeamnovaOmok is an Android multi-module workspace. `app/` hosts the launcher UI and navigation shell. Business rules live in `domain/`; concrete data sources belong in `data/` and infrastructure adapters in `infra/`. Feature slices (`feature_auth/`, `feature_home/`) depend on `domain` plus shared contracts from `core-api/` and bindings in `core-di/`. Reusable widgets and theming are under `designsystem/`. Keep module boundaries directional: UI → domain → data/infra.

## Build, Test, and Development Commands
Use `./gradlew assembleDebug` for a full debug build and to catch compilation issues. Run `./gradlew testDebugUnitTest` to execute JVM tests across modules. `./gradlew connectedDebugAndroidTest` runs instrumentation tests on an attached device or emulator. Spot-check with `./gradlew :module:lint` before pushing to keep Android Lint clean. Android Studio run configs are fine for rapid iteration, but verify these Gradle tasks before opening a pull request.

## Coding Style & Naming Conventions
Write Kotlin with 4-space indentation and the default Android Studio formatter; apply the same rules to Java code where present. Classes and objects use `PascalCase`, methods and properties use `camelCase`, and resources stay `snake_case`. Mapper classes live beside the target model (e.g., `data/.../mapper`). When touching backend bridge code, respect PSR-12 formatting and the fixed payload keys returned by the PHP endpoints (`user_id`, `display_name`, `score`, `rank`).

## Testing Guidelines
Place unit tests under `src/test` within the same package as the code under test, suffixing files with `Test` (`RankingUseCaseTest`). Prefer JUnit4 with Mockito or the Kotlin coroutines test utilities already on the classpath. Instrumentation tests stay under `src/androidTest`; tag slow suites with `@LargeTest` when relevant. Cover new domain logic, mapper behaviour, and any parsing tied to backend contracts.

## Commit & Pull Request Guidelines
Keep commits focused and write short, imperative summaries—mention the touched module when helpful (예: `domain: 랭킹 엔티티 정리`). Squash noise before pushing. Pull requests should describe the problem, solution, and how you validated it; include screenshots or recordings for UI updates and link issues when available. Confirm the Gradle build and relevant test tasks pass locally.

## Security & Configuration Tips
Do not commit secrets from `local.properties`, `.env`, or test fixtures. Provide redacted examples when documenting credentials. Validate that only the expected ranking fields are persisted and remove sensitive logging before release builds.
