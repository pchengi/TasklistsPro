# Share XML backup overlay

Base commit:

`1bd492d4dfaeb9f61e3d44a443acc9b9b4856c05`

Files included:

- `app/src/main/java/com/pchengi/tasklistspro/ui/TaskListScreen.kt`
- `app/src/main/AndroidManifest.xml`
- `app/src/main/res/xml/file_paths.xml`

Changes:

1. Toolbar order is now:
   - Restore
   - Save
   - Share
2. Save uses filename:
   - `TaskListPro_YYYY-MM-DD.xml`
3. Share writes the current XML backup to app cache as:
   - `TaskListPro_YYYY-MM-DD.xml`
4. Share opens Android's native share sheet with the XML attached.
5. Adds a `FileProvider` with authority:
   - `com.pchengi.tasklistspro.fileprovider`

Manual test:

1. Build:
   - `./gradlew assembleDebug`
2. Confirm toolbar order: Restore, Save, Share.
3. Tap Save and verify the default filename format.
4. Tap Share and confirm Android share sheet opens.
5. Share via email or another available app and verify the XML file is attached.
6. Confirm Restore and Delete-all still work.
