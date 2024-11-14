package com.spacey.newsbuddy.datasync

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class DataSyncRepository(private val syncDao: SyncDao) {

    suspend fun getSyncData(): List<SyncEntry> = withContext(Dispatchers.IO) {
        syncDao.select()
    }

    suspend fun insert(syncEntry: SyncEntry) = withContext(Dispatchers.IO) {
        syncDao.insert(syncEntry)
    }

}