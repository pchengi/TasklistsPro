# TasklistsPro drag-handle cohesive update

Unzip this archive at the root of the `TasklistsPro` repository and overwrite existing files.

This update implements the next interaction-layer milestone:

- The visible drag handle is the only drag target.
- Vertical drag on the handle reorders a task among siblings.
- Horizontal right drag indents the task under the previous visible task when allowed.
- Horizontal left drag outdents the task one level.
- The three-level subtask limit is enforced in the repository.
- Long-press on task title remains handled by `InlineTaskTitle`.
- The repository guards against cyclic moves.

Suggested verification after applying:

```bash
./gradlew assembleDebug
```

Manual test checklist:

1. Create three top-level tasks.
2. Drag the second task upward using only the handle.
3. Drag it downward using only the handle.
4. Drag it right beneath the previous visible task.
5. Drag it left to promote it back.
6. Confirm title tap/edit and long-press bold still work.
7. Confirm tasks cannot be nested deeper than three subtask levels.
