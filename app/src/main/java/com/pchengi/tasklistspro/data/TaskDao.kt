package com.pchengi.tasklistspro.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks ORDER BY parentId IS NOT NULL, parentId, sortOrder, id")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks ORDER BY parentId IS NOT NULL, parentId, sortOrder, id")
    suspend fun getAllTasks(): List<TaskEntity>

    @Query("SELECT COALESCE(MAX(sortOrder), -1) + 1 FROM tasks WHERE parentId IS :parentId")
    suspend fun nextSortOrder(parentId: Long?): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(tasks: List<TaskEntity>): List<Long>

    @Update
    suspend fun update(task: TaskEntity)

    @Update
    suspend fun updateAll(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE id IN (:ids)")
    suspend fun deleteIds(ids: List<Long>)

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(tasks: List<TaskEntity>) {
        deleteAll()
        insertAll(tasks)
    }
}
