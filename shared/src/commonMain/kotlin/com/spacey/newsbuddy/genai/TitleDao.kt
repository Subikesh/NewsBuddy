package com.spacey.newsbuddy.genai

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface TitleDao {
    @Insert
    fun addChatTitle(title: ChatTitle)

    @Insert
    fun addSummaryTitle(title: SummaryTitle)
}