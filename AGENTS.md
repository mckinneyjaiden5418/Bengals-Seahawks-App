# Codex repository guidance

## Purpose

This is a learning-focused Android application. Prefer small, reviewable vertical
slices and explain non-obvious Java or Android decisions in the change summary.

## Technical constraints

- Write application code in Java unless the task explicitly calls for Kotlin.
- Keep activities and fragments focused on rendering and user interaction.
- Put feed/cache coordination behind a repository and persistence behind Room DAOs.
- Keep network and database work off the main thread.
- Do not add a dependency-injection framework or a new Gradle module without a
  concrete need and an explanation of the trade-off.
- Never commit API keys, signing credentials, `local.properties`, or machine-local
  Android SDK paths.

## Verification

Run the narrowest relevant tests while iterating. Before completing a change, run:

```bash
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

For UI or device-dependent behavior, also describe the manual Android Studio or
physical-device check that remains to be performed.

## Change discipline

- Keep each task to one milestone or behavior.
- Add or update tests for business logic and regressions.
- Do not silently replace an RSS source or change its team assignment.
- Keep documentation honest about implemented versus planned functionality.
