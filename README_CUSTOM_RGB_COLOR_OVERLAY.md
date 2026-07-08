# Custom RGB background color overlay

Base commit:

`8c668c173607f3da059b202293bbab29de255c8d`

Files included:

- `app/src/main/java/com/pchengi/tasklistspro/ui/TaskListScreen.kt`

Changes:

1. Adds `Post-it Yellow` as the first default bright-mode background option.
2. Adds `Add custom color` as the final background picker option.
3. Custom color opens an RGB slider dialog.
4. Applying a custom color saves it through the existing background color persistence path.

Manual test:

1. Build:
   - `./gradlew assembleDebug`
2. Switch to bright mode.
3. Open the palette.
4. Confirm `Post-it Yellow` appears first.
5. Choose `Post-it Yellow` and confirm the background changes.
6. Open the palette again and choose `Add custom color`.
7. Adjust R/G/B sliders and apply.
8. Restart the app and confirm the custom color persists.
