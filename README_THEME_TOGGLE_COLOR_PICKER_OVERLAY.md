# Theme toggle + bright background color overlay

Base commit:

`0e9eb8fd062fff6903728d7d9b192a708dac0316`

Files included:

- `app/src/main/java/com/pchengi/tasklistspro/MainActivity.kt`
- `app/src/main/java/com/pchengi/tasklistspro/ui/TaskListScreen.kt`
- `app/src/main/java/com/pchengi/tasklistspro/ui/theme/Theme.kt`

Changes:

1. Adds a toolbar button to switch between bright and dark mode.
2. In bright mode, adds a palette button for choosing the background color.
3. Saves both preferences in SharedPreferences:
   - dark/bright mode
   - selected bright-mode background color
4. Keeps existing restore/save/share/delete/add behavior unchanged.

Manual test:

1. Build:
   - `./gradlew assembleDebug`
2. Confirm the mode toggle appears in the toolbar.
3. Switch to dark mode.
4. Switch back to bright mode.
5. Confirm the palette button appears only in bright mode.
6. Choose a background color.
7. Restart the app and confirm the selected mode/color persists.
8. Confirm restore/save/share still work.
