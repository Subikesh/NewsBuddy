package com.spacey.newsbuddy.datasync

import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query

@Dao
interface SyncDao {
    @Insert
    fun insert(entry: SyncEntry)

    @Query("SELECT * FROM SyncEntry ORDER BY syncTime DESC LIMIT :limit OFFSET :offset")
    fun select(offset: Int = 0, limit: Int = 200): List<SyncEntry>
}

@Entity
data class SyncEntry(
    @PrimaryKey val syncTime: Long,
    val newsResult: String,
    val summaryResult: String,
    val chatResult: String
)