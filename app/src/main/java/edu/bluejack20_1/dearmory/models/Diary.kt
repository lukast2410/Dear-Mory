package edu.bluejack20_1.dearmory.models

import java.io.Serializable
import java.util.*

class Diary(): Serializable {
    private lateinit var diaryId: String
    private lateinit var diaryText: String
    private lateinit var diaryMood: String
    private lateinit var diaryDate: String

    companion object{
        const val DIARY = "Diary"
        const val DIARY_ID = "diaryId"
        const val ANGRY_MOOD = "Angry"
        const val HAPPY_MOOD = "Happy"
        const val SAD_MOOD = "Sad"
        const val SEND_DIARY_TYPE = "DiaryType"
        const val WRITE_DIARY = "Write"
        const val SELECT_DIARY = "Select"
        const val DATE_DIARY = "DiaryDate"
    }

    init {
        diaryId = "false"
        diaryMood = Diary.HAPPY_MOOD
        diaryText = ""
        diaryDate = ""
    }

    fun setId(id: String): Diary{
        diaryId = id
        return this
    }

    fun setText(text: String): Diary{
        diaryText = text
        return this
    }

    fun setMood(mood: String): Diary{
        diaryMood = mood
        return this
    }

    fun setDate(date: String): Diary{
        diaryDate = date
        return this
    }

    fun getId(): String{
        return diaryId
    }

    fun getText(): String{
        return diaryText
    }

    fun getMood(): String{
        return diaryMood
    }

    fun getDate(): String{
        return diaryDate
    }
}