# Bengals & Seahawks News

A small, open-source Android news reader for fans of the Cincinnati Bengals and
Seattle Seahawks. It collects recent stories for both teams in a single app and
keeps previously loaded articles available when the device is offline.

## Features

- Separate Bengals and Seahawks feeds
- News aggregation through Google News RSS
- Local article cache powered by Room
- Saved articles that persist across app launches
- Offline access to previously downloaded headlines
- Links open in the device's default browser
- Manual refresh with clear loading and error feedback

## Screenshots

Screenshots will be added after the first device-tested release.

## Getting started

### Requirements

- Android Studio with the Android SDK installed
- JDK 11 or newer
- An Android device or emulator running Android 7.0 (API 24) or newer

### Run the app

1. Clone the repository and open it in Android Studio.
2. Allow the Gradle project to sync.
3. Select an emulator or connected Android device.
4. Run the `app` configuration.

Or build from the command line:

```bash
./gradlew assembleDebug
```

## Architecture

The app is written in Java and uses a small repository-based architecture:

- **UI:** `MainActivity`, `ArticleAdapter`, and `NewsViewModel`
- **Remote data:** an `HttpURLConnection` RSS client and pull parser
- **Local data:** Room database and DAO
- **Repository:** coordinates refreshes, cached articles, and bookmarks

Room is the source of truth: network refreshes update the database and the UI
observes database changes through LiveData.

## Data sources

Headlines are discovered through Google News RSS searches for each team. Articles
remain the property of their respective publishers and open on the publisher's
website. This project is not affiliated with the Cincinnati Bengals, Seattle
Seahawks, NFL, or Google.

## Contributing

Bug reports and focused pull requests are welcome. Please run the checks below
before submitting a change:

```bash
./gradlew test
./gradlew lint
./gradlew assembleDebug
```

## License

No license has been selected yet. Until one is added, all rights are reserved by
the repository owner.
