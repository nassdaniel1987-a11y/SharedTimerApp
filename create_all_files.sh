#!/bin/bash

echo "🚀 Erstelle alle fehlenden Dateien..."

# Erstelle alle Verzeichnisse
mkdir -p app/src/main/res/{layout,drawable,values,raw,mipmap-xxxhdpi}

# ===== LAYOUT FILES =====
echo "📄 Erstelle Layout-Dateien..."

# activity_alarm_fullscreen.xml
cat > app/src/main/res/layout/activity_alarm_fullscreen.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/alarm_red"
    tools:context=".AlarmFullscreenActivity">

    <ImageView
        android:id="@+id/ivAlarmIcon"
        android:layout_width="120dp"
        android:layout_height="120dp"
        android:src="@drawable/ic_alarm_large"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:layout_constraintBottom_toTopOf="@id/tvChildName"
        app:layout_constraintVertical_chainStyle="packed"
        android:contentDescription="Alarm Icon" />

    <TextView
        android:id="@+id/tvChildName"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="32dp"
        android:textColor="@android:color/white"
        android:textSize="32sp"
        android:textStyle="bold"
        tools:text="Max Mustermann"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/ivAlarmIcon"
        app:layout_constraintBottom_toTopOf="@id/tvMessage" />

    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginTop="16dp"
        android:layout_marginStart="32dp"
        android:layout_marginEnd="32dp"
        android:gravity="center"
        android:textColor="@android:color/white"
        android:textSize="20sp"
        tools:text="Es ist Zeit, Max abzuholen!"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvChildName"
        app:layout_constraintBottom_toTopOf="@id/btnStopAlarm" />

    <Button
        android:id="@+id/btnStopAlarm"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginEnd="32dp"
        android:layout_marginTop="48dp"
        android:backgroundTint="@color/white"
        android:text="Alarm stoppen"
        android:textColor="@color/alarm_red"
        android:textSize="18sp"
        android:textStyle="bold"
        android:padding="16dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/tvMessage"
        app:layout_constraintBottom_toTopOf="@id/btnSnooze" />

    <Button
        android:id="@+id/btnSnooze"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_marginStart="32dp"
        android:layout_marginEnd="32dp"
        android:layout_marginTop="16dp"
        android:backgroundTint="#80FFFFFF"
        android:text="5 Minuten Schlummern"
        android:textColor="@android:color/white"
        android:textSize="16sp"
        android:padding="12dp"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@id/btnStopAlarm"
        app:layout_constraintBottom_toBottomOf="parent" />

</androidx.constraintlayout.widget.ConstraintLayout>
EOF

# item_timer.xml
cat > app/src/main/res/layout/item_timer.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<com.google.android.material.card.MaterialCardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_marginBottom="12dp"
    app:cardCornerRadius="12dp"
    app:cardElevation="2dp"
    app:strokeColor="@color/card_stroke"
    app:strokeWidth="1dp">

    <androidx.constraintlayout.widget.ConstraintLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="16dp">

        <ImageView
            android:id="@+id/ivTimerIcon"
            android:layout_width="48dp"
            android:layout_height="48dp"
            android:src="@drawable/ic_child"
            android:contentDescription="Kind Icon"
            app:tint="@color/primary"
            app:layout_constraintStart_toStartOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

        <TextView
            android:id="@+id/tvChildName"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_marginStart="12dp"
            android:textColor="@color/text_primary"
            android:textSize="18sp"
            android:textStyle="bold"
            tools:text="Max Mustermann"
            app:layout_constraintEnd_toStartOf="@id/btnDelete"
            app:layout_constraintStart_toEndOf="@id/ivTimerIcon"
            app:layout_constraintTop_toTopOf="@id/ivTimerIcon" />

        <TextView
            android:id="@+id/tvDate"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="4dp"
            android:drawablePadding="4dp"
            android:textColor="@color/text_secondary"
            android:textSize="14sp"
            tools:text="15.12.2024"
            app:drawableStartCompat="@drawable/ic_calendar_small"
            app:drawableTint="@color/text_secondary"
            app:layout_constraintStart_toStartOf="@id/tvChildName"
            app:layout_constraintTop_toBottomOf="@id/tvChildName" />

        <TextView
            android:id="@+id/tvTime"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginStart="16dp"
            android:drawablePadding="4dp"
            android:textColor="@color/text_secondary"
            android:textSize="14sp"
            tools:text="14:30 Uhr"
            app:drawableStartCompat="@drawable/ic_clock_small"
            app:drawableTint="@color/text_secondary"
            app:layout_constraintBottom_toBottomOf="@id/tvDate"
            app:layout_constraintStart_toEndOf="@id/tvDate"
            app:layout_constraintTop_toTopOf="@id/tvDate" />

        <TextView
            android:id="@+id/tvRemainingTime"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:background="@drawable/bg_badge"
            android:paddingHorizontal="12dp"
            android:paddingVertical="4dp"
            android:textColor="@android:color/white"
            android:textSize="12sp"
            android:textStyle="bold"
            tools:text="In 2 Std. 30 Min."
            app:layout_constraintStart_toStartOf="@id/tvChildName"
            app:layout_constraintTop_toBottomOf="@id/tvDate" />

        <ImageButton
            android:id="@+id/btnDelete"
            android:layout_width="40dp"
            android:layout_height="40dp"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="Timer löschen"
            android:src="@drawable/ic_delete"
            app:tint="@color/error"
            app:layout_constraintEnd_toEndOf="parent"
            app:layout_constraintTop_toTopOf="parent" />

    </androidx.constraintlayout.widget.ConstraintLayout>

</com.google.android.material.card.MaterialCardView>
EOF

echo "✓ Layout-Dateien erstellt"

# ===== VALUES FILES =====
echo "📄 Erstelle Values-Dateien..."

# colors.xml
cat > app/src/main/res/values/colors.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="primary">#1976D2</color>
    <color name="primary_dark">#0D47A1</color>
    <color name="primary_light">#BBDEFB</color>
    <color name="accent">#FF5722</color>
    
    <color name="background_color">#F5F5F5</color>
    <color name="card_stroke">#E0E0E0</color>
    
    <color name="text_primary">#212121</color>
    <color name="text_secondary">#757575</color>
    
    <color name="alarm_red">#D32F2F</color>
    <color name="white_transparent">#80FFFFFF</color>
    
    <color name="error">#F44336</color>
    <color name="success">#4CAF50</color>
</resources>
EOF

# strings.xml
cat > app/src/main/res/values/strings.xml << 'EOF'
<resources>
    <string name="app_name">Abhol-Timer</string>
    
    <string name="child_name_hint">Name des Kindes</string>
    <string name="select_date_time">Datum &amp; Uhrzeit wählen</string>
    <string name="create_timer">Timer erstellen</string>
    <string name="empty_state">Keine Timer vorhanden\n\nErstelle einen neuen Timer\num loszulegen</string>
    
    <string name="stop_alarm">Alarm stoppen</string>
    <string name="snooze_5_min">5 Minuten Schlummern</string>
    <string name="alarm_icon">Alarm Icon</string>
    
    <string name="child_icon">Kind Icon</string>
    <string name="delete_timer">Timer löschen</string>
    
    <string name="permission_notification_title">Benachrichtigungen erlauben</string>
    <string name="permission_notification_message">Diese App benötigt die Berechtigung für Benachrichtigungen, um dich an Timer zu erinnern.</string>
    
    <string name="error_no_name">Bitte gib einen Namen ein</string>
    <string name="error_no_time">Bitte wähle Datum und Uhrzeit</string>
    <string name="error_time_past">Die Zeit muss in der Zukunft liegen</string>
</resources>
EOF

# themes.xml
cat > app/src/main/res/values/themes.xml << 'EOF'
<resources xmlns:tools="http://schemas.android.com/tools">
    <style name="Theme.SharedTimerApp" parent="Theme.Material3.Light.NoActionBar">
        <item name="colorPrimary">@color/primary</item>
        <item name="colorPrimaryVariant">@color/primary_dark</item>
        <item name="colorOnPrimary">@android:color/white</item>
        
        <item name="colorSecondary">@color/accent</item>
        <item name="colorSecondaryVariant">@color/accent</item>
        <item name="colorOnSecondary">@android:color/white</item>
        
        <item name="android:statusBarColor">@color/primary_dark</item>
        <item name="android:windowBackground">@color/background_color</item>
    </style>
    
    <style name="Theme.SharedTimerApp.Fullscreen" parent="Theme.Material3.Light.NoActionBar">
        <item name="android:windowFullscreen">true</item>
        <item name="android:windowContentOverlay">@null</item>
        <item name="android:windowBackground">@color/alarm_red</item>
        <item name="android:windowShowWallpaper">false</item>
        <item name="android:windowIsTranslucent">false</item>
    </style>
</resources>
EOF

echo "✓ Values-Dateien erstellt"

echo ""
echo "✅ Alle Dateien erfolgreich erstellt!"
echo ""
echo "Nächste Schritte:"
echo "1. Drawable-Icons müssen noch erstellt werden"
echo "2. AndroidManifest.xml muss erstellt werden"
echo "3. build.gradle Dateien müssen erstellt werden"
echo "4. google-services.json muss von Firebase heruntergeladen werden"
echo "5. alarm_sound.mp3 muss in res/raw/ eingefügt werden"

