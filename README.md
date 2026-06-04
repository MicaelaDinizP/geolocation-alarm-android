# 🚀 Geolocation Alarm App

An Android application that allows users to safely rest or focus on other activities while the app monitors their route and triggers an alert upon approaching a destination.

---
## 🧩 Current Status

This project is currently in its early stage.

- ✅ Core domain model: `Trip`
- 🚧 Remaining components (repository, services, UI) are under development
---

## ✨ Features

- 📍 Real-time geolocation tracking  
- ⏰ Distance/time-based alarm triggers  
- 🔔 Background notifications  
- 💤 Designed for passive usage (e.g., sleeping during commute)

---

## 🏗️ Tech Stack

- **Language:** Java 11  
- **Platform:** Android  
- **minSdk:** 26  
- **Architecture:** MVVM (planned / in progress)  
- **Testing:** JUnit 5 + Allure Reports

---
## 📁 Project Structure

```plaintext
geolocation-alarm-app/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── br.com.micaeladinizp.chegometro
│   │   │   │   ├── data/
│   │   │   │   │   └── model/
│   │   │   │   │       └── Trip.java
│   │   │   │   │       └── Location.java
│   │   │   │   │       └── RouteResult.java
│   │   │   │   │       └── TripState.java
│   │   │   │   │   └── remote/
│   │   │   │   │       └── OsrmResponseParser.java
│   │   │   │   │       └── OsrmRouteClient.java
│   │   │   │   │       └── RouteClient.java
│   │   │   │   │   └── RouteCallback
│   │   │   │   ├── ui/
|   |   |   |   ├── domain/
|   |   |   |   ├── app/
│   │   │   │   └── service/
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   └── androidTest/
│   └── build.gradle
├── gradle/
├── .gitignore
├── README.md
└── settings.gradle
```

## 🧩 Architecture Overview

- **app/** → Application-level configuration and initialization (`MyApp`)
- **data/** → models and data handling
- **domain/** → business logic (planned)
- **ui/** → presentation layer (planned)
- **service/** → background processes (planned) 
---
## 📱 Permissions

| Permission | Type | Reason |
|------------|--------|--------|
| `INTERNET` | Normal | Requests to the OSRM API |
| `ACCESS_NETWORK_STATE` | Normal | Check network connectivity |
| `VIBRATE` | Normal | Vibrate when the alarm is triggered |
| `ACCESS_FINE_LOCATION` | Dangerous | Required for precise location tracking |
| `ACCESS_COARSE_LOCATION` | Dangerous | Fallback for approximate location |
| `FOREGROUND_SERVICE` | Normal | Required for background tracking |
| `POST_NOTIFICATIONS` | Dangerous | Notify user when nearing destination |

---

## 🧪 Testing Strategy

- **Unit Tests:** Business logic (Trip, TripState, etc.)
- **Future Work:** Integration and instrumentation tests

---
## 📊 Test Reports (Allure)

This project uses **Allure Report** to generate rich and interactive test reports.

Allure provides:
- 📈 Visual test execution overview  
- ❌ Detailed failure analysis  
- 🧩 Structured test steps  (planned)
- 📎 Attachments (logs, screenshots, etc.) (planned)  

---
### ⚠️ Prerequisites

Install Allure Commandline to view test reports:  
https://github.com/allure-framework/allure2/releases

---

### ▶️ How to generate the report


Run the tests and generate allure report:

```bash
./gradlew testDebugUnitTest --rerun-tasks
```
Open the report in your browser:
```bash
allure serve app/build/allure-results
```
---

## 🌿 Branching Strategy

- `main` → stable code  
- `feature/*` → new features  
- `fix/*` → bug fixes  
- `docs/*` → documentation
- `chore/*` → maintenance / setup tasks 

---

## 📝 Commit Convention

This project follows **Conventional Commits**:

- `feat:` new feature  
- `fix:` bug fix  
- `docs:` documentation  
- `test:` tests  
- `refactor:` code improvements  
- `chore:` setup / maintenance  

---

## 📌 Roadmap

- [x] Core trip model  
- [ ] Route calculation  
- [ ] Alarm trigger logic  
- [ ] Background tracking  
- [ ] UI implementation  

---

## 🔒 Privacy

This app uses location data strictly for route monitoring.  
No user data is stored or shared externally.

---

## ⚙️ Setup

```bash
git clone https://github.com/MicaelaDinizP/geolocation-alarm-android.git
```
Open the project in Android Studio and run on an emulator or physical device.

---

## 🎯 Use Case

1. User selects a destination on the map  
2. App monitors location in the background  
3. Alarm is triggered when approaching the destination  
