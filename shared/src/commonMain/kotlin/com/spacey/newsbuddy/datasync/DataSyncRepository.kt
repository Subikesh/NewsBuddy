package com.spacey.newsbuddy.datasync

class DataSyncRepository(private val syncDao: SyncDao) {

    fun getSyncData(): List<SyncEntry> {
        return syncDao.select()
    }

    fun insert(syncEntry: SyncEntry) {
        syncDao.insert(syncEntry)
    }

}