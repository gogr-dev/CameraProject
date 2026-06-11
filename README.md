# CameraProject

An Android app for **geotagged, commented photos**. Snap a picture, add a note, and it's
saved with your GPS location and dropped as a pin on a Google Map. Tap a pin to see the
photo, its comment, when it was taken, and where; tap the info window to delete it.

Built in Java with an MVP architecture, Room for persistence, and the Google Maps and
Location SDKs.

## Features

- **Map of your photos.** Every saved photo appears as a marker at the location it was
  taken; the map centres on your current position.
- **Capture flow.** A floating button opens the camera, then prompts for a comment and
  tags the photo with the current location before saving.
- **Rich info windows.** Markers show a thumbnail of the photo plus comment, timestamp and
  coordinates.
- **Delete.** Tapping a marker's info window offers to remove the photo.
- **Offline storage.** Photos and metadata persist in a local Room (SQLite) database;
  image files live in app-scoped external storage.

## Architecture

```
MapsActivity ──────────── shows photos as map markers, hosts the “add” button
  └─ InfoWindowAdapter ─── renders the photo + metadata bubble

TakePhotoActivity ─────── MVP host
  ├─ TakePhotoContract ── View/Presenter boundary
  ├─ TakePhotoPresenter ─ builds the CommentedPhoto and saves it
  └─ TakePhotoFragmentView (View) — camera intent, comment dialog, location

data/  (repository pattern over Room)
  ├─ CommentedPhoto ───── @Entity (id, filename, comment, lat, lng, timestamp)
  ├─ CommentedPhotoDao ── insert / findAll / findById / update / delete
  ├─ CommentedPhotoDatabase
  ├─ CommentedPhotoDataSource ── interface + callbacks
  └─ CommentedPhotoRepository ── singleton, runs DB work off the main thread

util/  AppExecutors — disk-IO / main-thread executors
```

The build uses AGP 8.2 / Gradle 8.2 with a version catalog
(`gradle/libs.versions.toml`), `compileSdk`/`targetSdk` 34, and Java 17.

## Setup

You need **Android Studio** (Hedgehog or newer) with an SDK that supports **API 34**.

### 1. Add a Google Maps API key

The Maps key is **not** stored in version control. It's injected at build time from a
`secrets.properties` file by the
[Secrets Gradle Plugin](https://developers.google.com/maps/documentation/android-sdk/secrets-gradle-plugin).

Create `secrets.properties` in the project root (it's git-ignored):

```properties
MAPS_API_KEY=YOUR_GOOGLE_MAPS_ANDROID_API_KEY
```

Get a key from the [Google Cloud console](https://developers.google.com/maps/documentation/android-sdk/get-api-key)
(enable the *Maps SDK for Android*). Without a real key the app still builds, but the map
shows no tiles.

### 2. Build & run

```bash
./gradlew assembleDebug      # or press Run in Android Studio
```

Install on a device/emulator with Google Play services and a camera. On first launch,
grant the location permission, tap the camera button, take a photo, add a comment — it
appears on the map.

## Limitations / next steps

- Comments are write-once (no edit) — `savePhoto`/`update` exist if you want to add editing.
- Location uses last-known fix; a freshly-booted device may briefly have none, in which
  case the photo is saved without a pin.
- No automated test coverage yet — the data layer is structured to make the repository
  unit-testable with a fake DAO.

## License

MIT.
