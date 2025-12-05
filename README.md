# 🔔 Shared Timer App - Vollständige Android App für Grundschulen

## 📋 Übersicht

Diese App ermöglicht es Lehrern, Abhol-Timer für Kinder einzustellen. **Alle Geräte klingeln gleichzeitig**, wenn der Timer abläuft – auch im Doze Mode und Silent Mode!

### ✨ Features
- ✅ Präzise Alarme (auch im Doze Mode)
- ✅ Sound im Silent Mode
- ✅ Echtzeit-Synchronisation über Firebase Firestore
- ✅ Push-Notifications via OneSignal
- ✅ Fullscreen-Alarm-UI
- ✅ Timer-Übersicht mit RecyclerView
- ✅ Automatische Wiederherstellung nach Reboot

---

## 🚀 Installation & Setup

### 1. Projekt in Android Studio öffnen

1. **Android Studio öffnen**
2. **File → Open**
3. Wähle den `SharedTimerApp` Ordner
4. Warte bis Gradle synchronisiert

---

### 2. Firebase einrichten

#### 2.1 Firebase-Projekt erstellen
1. Gehe zu https://console.firebase.google.com
2. **"Projekt hinzufügen"** klicken
3. Projektname: `SharedTimerApp`
4. Google Analytics: Optional
5. **"Projekt erstellen"**

#### 2.2 Android-App hinzufügen
1. Im Firebase Console: Klicke auf **Android-Symbol**
2. **Android-Paketname:** `com.example.sharedtimer`
3. **App-Spitzname:** `SharedTimerApp` (optional)
4. **"App registrieren"**

#### 2.3 google-services.json herunterladen
1. **Lade die Datei herunter**
2. Kopiere `google-services.json` in: `SharedTimerApp/app/`

**Wichtig:** Die Datei muss in `app/` liegen, NICHT in `app/src/`!

#### 2.4 Firestore aktivieren
1. In Firebase Console: **Build → Firestore Database**
2. **"Datenbank erstellen"**
3. **Testmodus** wählen (für Entwicklung)
4. Region: **europe-west3 (Frankfurt)**
5. **"Aktivieren"**

#### 2.5 Firestore-Regeln (für Entwicklung)
```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    match /timers/{document=**} {
      allow read, write: if true; // NUR FÜR ENTWICKLUNG!
    }
  }
}
```

---

### 3. OneSignal einrichten

#### 3.1 OneSignal-Account erstellen
1. Gehe zu https://onesignal.com
2. **"Get Started Free"** klicken
3. Account erstellen

#### 3.2 App hinzufügen
1. **"New App/Website"**
2. Name: `SharedTimerApp`
3. **"Android (Google FCM)"** auswählen

#### 3.3 Firebase Server Key eingeben
1. Gehe zurück zur **Firebase Console**
2. **Projekteinstellungen** (Zahnrad-Symbol oben links)
3. **Cloud Messaging** Tab
4. Falls nicht aktiviert: **"Cloud Messaging API (Legacy)" aktivieren**
5. Kopiere **Server Key**
6. Füge ihn in OneSignal ein

#### 3.4 OneSignal App ID & REST API Key kopieren
1. In OneSignal: **Settings → Keys & IDs**
2. Kopiere:
   - **OneSignal App ID**
   - **REST API Key**

#### 3.5 IDs in den Code einfügen

**Öffne diese 3 Dateien und ersetze die Platzhalter:**

**1. `TimerApplication.kt`** (Zeile 22):
```kotlin
OneSignal.initWithContext(this, "DEINE_ONESIGNAL_APP_ID")
```

**2. `OneSignalApiService.kt`** (Zeile 21):
```kotlin
private const val ONESIGNAL_REST_API_KEY = "DEIN_REST_API_KEY"
```

**3. `OneSignalHelper.kt`** (Zeile 13):
```kotlin
private const val ONESIGNAL_APP_ID = "DEINE_ONESIGNAL_APP_ID"
```

---

### 4. Alarm-Sound hinzufügen

**WICHTIG:** Du musst eine MP3-Alarm-Sound-Datei hinzufügen!

1. **Lade eine Alarm-Sound-Datei herunter:**
   - https://pixabay.com/sound-effects/search/alarm/ (Kostenlos)
   - https://freesound.org/ (Kostenlos)

2. **Benenne die Datei um zu:** `alarm_sound.mp3`

3. **Kopiere sie in:** `app/src/main/res/raw/`

   Falls der Ordner `raw` nicht existiert:
   - Rechtsklick auf `res`
   - **New → Android Resource Directory**
   - **Directory name:** `raw`
   - **Resource type:** `raw`
   - **OK**

---

### 5. App Icons hinzufügen (Optional)

**Standard-Icons sind bereits vorhanden**, aber du kannst eigene Icons erstellen:

1. Gehe zu https://romannurik.github.io/AndroidAssetStudio/
2. Erstelle deine Icons
3. Ersetze die Dateien in `app/src/main/res/mipmap-xxxhdpi/`

---

### 6. Build & Installation

#### 6.1 Gradle Sync
```
File → Sync Project with Gradle Files
```

Warte bis die Synchronisation abgeschlossen ist (kann 2-5 Minuten dauern).

#### 6.2 Build
```
Build → Make Project
```

Oder über Terminal:
```bash
./gradlew assembleDebug
```

#### 6.3 Auf Gerät installieren

1. **USB-Debugging aktivieren** auf deinem Android-Gerät:
   - Einstellungen → Über das Telefon
   - 7x auf "Build-Nummer" tippen
   - Entwickleroptionen → USB-Debugging aktivieren

2. **Gerät per USB verbinden**

3. In Android Studio:
   - **Run → Run 'app'**
   - Wähle dein Gerät

---

## ⚙️ Berechtigungen nach Installation

Nach der Installation musst du folgende Berechtigungen erteilen:

### 1. Benachrichtigungen zulassen
Die App fragt automatisch nach der Berechtigung.

### 2. Exakte Alarme erlauben (Android 12+)
1. Einstellungen → Apps → SharedTimerApp
2. **"Alarme & Erinnerungen"** aktivieren

### 3. Akku-Optimierung deaktivieren
1. Einstellungen → Akku → Akku-Optimierung
2. Suche "SharedTimerApp"
3. **"Nicht optimieren"** auswählen

**Ohne diese Einstellungen funktionieren Timer im Standby nicht zuverlässig!**

---

## 📱 Verwendung

### Timer erstellen
1. Öffne die App
2. Gib den Namen des Kindes ein
3. Wähle Datum & Uhrzeit
4. Klicke auf **"Timer erstellen"**

### Timer löschen
1. Klicke auf das **Papierkorb-Symbol** neben dem Timer

### Alarm stoppen
Wenn der Alarm klingelt:
- **"Alarm stoppen"** → Beendet den Alarm
- **"5 Minuten Schlummern"** → Alarm wird in 5 Min. erneut ausgelöst

---

## 🔧 Projektstruktur

```
SharedTimerApp/
├── app/
│   ├── src/main/
│   │   ├── java/com/example/sharedtimer/
│   │   │   ├── adapters/          # RecyclerView Adapter
│   │   │   ├── models/            # Datenmodelle
│   │   │   ├── network/           # OneSignal API
│   │   │   ├── repository/        # Firebase Repository
│   │   │   ├── utils/             # Hilfsklassen
│   │   │   ├── viewmodel/         # ViewModels
│   │   │   ├── MainActivity.kt    # Haupt-Activity
│   │   │   ├── AlarmReceiver.kt   # Alarm BroadcastReceiver
│   │   │   └── ...
│   │   ├── res/
│   │   │   ├── layout/            # UI Layouts
│   │   │   ├── drawable/          # Icons
│   │   │   ├── values/            # Strings, Colors, Themes
│   │   │   └── raw/               # Alarm-Sound
│   │   └── AndroidManifest.xml
│   ├── build.gradle               # App Dependencies
│   └── google-services.json       # Firebase Config (muss hinzugefügt werden)
├── build.gradle                   # Project Config
└── settings.gradle
```

---

## 🐛 Häufige Fehler & Lösungen

### ❌ "google-services.json not found"
**Lösung:** Datei muss in `app/` liegen, NICHT in `app/src/`

### ❌ "Unresolved reference: OneSignal"
**Lösung:** Gradle Sync durchführen: `File → Sync Project with Gradle Files`

### ❌ "alarm_sound.mp3 not found"
**Lösung:** 
1. Ordner `res/raw/` erstellen
2. MP3-Datei mit Namen `alarm_sound.mp3` hinzufügen

### ❌ App stürzt beim Start ab
**Lösung:**
1. Logcat prüfen (unten in Android Studio)
2. Sicherstellen dass `google-services.json` korrekt ist
3. OneSignal App ID korrekt eingefügt?

### ❌ Timer klingelt nicht im Standby
**Lösung:**
1. Akku-Optimierung deaktivieren
2. "Exakte Alarme" Berechtigung erteilen
3. App nicht aus "Recent Apps" schließen

---

## 📚 Technische Details

### Verwendete Technologien
- **Sprache:** Kotlin
- **UI:** XML Views mit ViewBinding
- **Architektur:** MVVM (ViewModel + LiveData)
- **Backend:** Firebase Firestore
- **Push-Notifications:** OneSignal
- **Networking:** Retrofit + OkHttp
- **Alarm-System:** AlarmManager mit `setExactAndAllowWhileIdle()`

### Warum funktioniert der Alarm im Doze Mode?
- Verwendung von `setExactAndAllowWhileIdle()` (Android 6+)
- `WAKE_LOCK` hält CPU wach
- `USE_FULL_SCREEN_INTENT` zeigt UI über Lockscreen
- `AudioAttributes.USAGE_ALARM` spielt Sound im Silent Mode

---

## 📝 TODO / Erweiterungen

- [ ] Benutzer-Login mit Firebase Authentication
- [ ] Mehrere Lehrer/Klassen Support
- [ ] Statistiken (wie oft kam Kind zu spät?)
- [ ] Recurring Timer (täglich wiederholen)
- [ ] Benutzerdefinierte Sounds
- [ ] Dunkles Theme
- [ ] Tablet-optimierte Layouts

---

## 📄 Lizenz

Dieses Projekt ist für Bildungszwecke erstellt. Frei verwendbar.

---

## 🤝 Support

Bei Fragen oder Problemen:
1. Prüfe die **"Häufige Fehler"** Sektion
2. Schaue in die Android Studio Logcat
3. Überprüfe Firebase & OneSignal Konfiguration

---

**Viel Erfolg mit der App! 🎉**
