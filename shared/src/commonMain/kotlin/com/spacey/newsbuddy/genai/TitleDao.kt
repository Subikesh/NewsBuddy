package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Upsert

@Dao
interface TitleDao {
    @Upsert
    fun addChatTitle(title: ChatTitle)

    @Upsert
    fun addSummaryTitle(title: SummaryTitle)
}