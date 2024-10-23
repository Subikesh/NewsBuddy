package com.spacey.newsbuddy.persistance

import androidx.room.TypeConverter
import com.spacey.newsbuddy.genai.ChatType

class RoomTypeConverters {

    @TypeConverter
    fun fromChatType(value: String): ChatType {
        return ChatType.valueOf(value)
    }

    @TypeConverter
    fun dateToTimestamp(chatType: ChatType): String {
        return chatType.name
    }
}