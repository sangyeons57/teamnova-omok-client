# Repository Guidelines

## Project Structure & Module Organization
- `app/` hosts the launcher UI and navigation shell, wiring feature modules into `MainActivity`.
- `feature_auth/` and `feature_home/` compose presentation + domain flows via contracts from `core-api/` and bindings in `core-di/`.
- `domain/` keeps pure entities and use cases; mirror their tests under `domain/src/test` for fast feedback.
- `data/` provides repositories and mappers, delegating platform specifics to `infra/` adapters (e.g., networking, storage).
- Shared UI tokens and widgets live in `designsystem/` and `ui-widgets/`; reuse styles from `designsystem/src/main/res` before adding new ones.

## Build, Test, and Development Commands
- `./gradlew assembleDebug` builds the full debug APK; run before proposing PRs to catch integration issues.
- `./gradlew testDebugUnitTest` executes JVM unit tests across modules via JUnit and coroutines test rules.
- `./gradlew connectedDebugAndroidTest` runs instrumentation suites when a device/emulator is attached.
- `./gradlew :<module>:lint` spot-checks Android lint for a specific module, e.g., `./gradlew :feature_home:lint`.

## Coding Style & Naming Conventions
- Follow Android Studio defaults: 4-space indentation, trailing commas optional, no wildcard imports.
- Use `PascalCase` for classes/objects, `camelCase` for members, and `snake_case` for resources (drawables, layouts).
- Keep mapper classes adjacent to the models they translate (e.g., `data/.../mapper/UserMapper.kt`).
- Preserve backend payload keys such as `user_id`, `display_name`, `score`, and `rank`; never localize them.

## Testing Guidelines
- Prefer JUnit4 with Mockito or the Kotlin coroutines testing utilities already on the classpath.
- Name test files after the subject with `Test` suffix, e.g., `RankingUseCaseTest` in `src/test/kotlin/...`.
- Cover domain rules, mapper behaviour, and remote parsing whenever adding new logic or integrations.
- Mark long-running instrumentation suites with `@LargeTest` under `src/androidTest` to aid CI filtering.

## Commit & Pull Request Guidelines
- Write focused commits in imperative mood (e.g., `domain: 랭킹 엔티티 정리`) and keep noise squashed locally.
- PR descriptions should outline the problem, the solution, and verification steps; attach screenshots for UI work.
- Link related issues, confirm `assembleDebug` and relevant tests pass, and call out any follow-up tasks explicitly.

## Security & Configuration Tips
- Do not commit secrets from `local.properties`, `.env`, or private fixtures; provide templates if needed.
- Strip sensitive logging before release builds and ensure only expected ranking fields persist in serialized models.
- Centralize credentials and endpoints through `core-di/` providers so feature modules rely on typed contracts.
