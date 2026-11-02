# CineStream Native Android Application

A professional native Android movie and TV series streaming application inspired by Netflix, connected directly to your existing website database and utilizing your original website video player.

- **Website:** [https://cinestream-movie-str-4phw.bolt.host/](https://cinestream-movie-str-4phw.bolt.host/)
- **Technology Stack:** Kotlin, Jetpack Compose, Material Design 3, Retrofit + Moshi, Supabase PostgREST & Auth, Dedicated Android WebView.

---

## 🎬 Key Features

1. **Native Netflix-Style Dark Interface:**
   - Cinematic hero banner with featured backdrop, title, tags, play, and watchlist quick-actions.
   - Categorized horizontal scrolling rows: **Trending Now**, **Latest Releases**, **Popular Movies**, **Top Series & Shows**, and **Recommended For You**.
   - Genre filter chips (Action, Drama, Sci-Fi, Comedy, Fantasy, Animation, etc.).

2. **Live Supabase Database Integration:**
   - Live query of the `movies` table (`published = true`).
   - Dynamically loads title, year, rating, runtime, overview, cast members, director, backdrop, and poster images.
   - Dual-mode favorites: guest local persistence and cloud synchronization via the `favorites` table when logged in.

3. **Original Website Video Player Integration:**
   - Dedicated full-featured Android WebView hosting `https://cinestream-movie-str-4phw.bolt.host/watch/{id}`.
   - Real-time cookie management, DOM Storage, and JavaScript execution.
   - True native fullscreen video playback with `WebChromeClient` (`onShowCustomView` / `onHideCustomView`).
   - Landscape auto-orientation support and manual orientation switcher.
   - Fallback to direct stream embed if ever required.
   - Native back navigation to return directly to movie details.

4. **Instant Dynamic Search:**
   - Real-time search across movie titles, original titles, cast, directors, and genres.
   - Interactive quick search suggestion pills.
   - Dynamic adaptive multi-column grid with empty states.

5. **User Authentication & Profile:**
   - Supabase user registration and login (`/auth/v1/token`, `/auth/v1/signup`).
   - Remember login session with local token storage.
   - Seamless guest viewing mode so users can browse and watch immediately without an account.

6. **Android TV & Large Screen Ready:**
   - Fully focusable elements (`Modifier.focusable()`) for D-pad remote navigation.
   - Responsive layouts adapted for phones, foldables, and large screen displays.

---

## 🚀 Building APK & AAB Online (No Android Studio Required)

This repository includes a pre-configured **GitHub Actions** workflow (`.github/workflows/build.yml`) that automatically builds both **Release APK** and **Release AAB (App Bundle)** online whenever you push code or trigger the workflow manually.

### How to Build on GitHub:
1. Push this project to your GitHub repository.
2. (Optional) In your GitHub repository settings, go to **Settings > Secrets and variables > Actions** and add:
   - `SUPABASE_URL` (optional, default provided)
   - `SUPABASE_ANON_KEY` (optional, default provided)
   - `KEYSTORE_BASE64` (Base64-encoded upload keystore for Play Store release signing)
   - `STORE_PASSWORD`
   - `KEY_PASSWORD`
3. Go to the **Actions** tab on GitHub, select **Build CineStream Android App**, and click **Run workflow**.
4. When the build finishes, scroll down to the **Artifacts** section to download:
   - `CineStream-Release-APK` (installable directly on your Android phone or Android TV)
   - `CineStream-Release-AAB` (ready for Google Play Console submission)

---

## ⚙️ Configuration & Environment

Configuration is stored in `.env` and `.env.example`:

```env
SUPABASE_URL=https://atzrtfdeokapvrwilobu.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImF0enJ0ZmRlb2thcHZyd2lsb2J1Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTAwNzI2NzIsImV4cCI6MjEwNTY0ODY3Mn0._MHwNJLIbZkj-eVy0aNHw7YjOpjYwQbhQ6sh2BTqgH8
WEBSITE_WATCH_BASE_URL=https://cinestream-movie-str-4phw.bolt.host/watch
```

---

## 📁 Project Structure

```
├── .github/workflows/build.yml    # Automated CI/CD pipeline for APK & AAB
├── app/
│   ├── build.gradle.kts           # App Gradle configuration & dependencies
│   ├── src/main/
│   │   ├── AndroidManifest.xml    # Permissions, hardware acceleration & configChanges
│   │   ├── java/com/example/
│   │   │   ├── MainActivity.kt    # Root Compose Activity & Navigation
│   │   │   ├── data/
│   │   │   │   ├── model/         # Movie, Profile, Favorite, WatchHistory models
│   │   │   │   ├── remote/        # Supabase Retrofit client & endpoints
│   │   │   │   └── repository/    # MovieRepository & AuthRepository
│   │   │   ├── ui/
│   │   │   │   ├── components/    # MovieCard, FeaturedMovieBanner, BottomNavBar
│   │   │   │   ├── screens/       # HomeScreen, DetailsScreen, PlayerScreen, SearchScreen, MyListScreen, ProfileScreen
│   │   │   │   ├── theme/         # Color, Theme, Type (Netflix dark theme)
│   │   │   │   └── viewmodel/     # CineStreamViewModel
│   │   └── res/                   # Adaptive icons, drawables, strings, colors
└── README.md
```
