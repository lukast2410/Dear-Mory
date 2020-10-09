package edu.bluejack20_1.dearmory.models

import java.util.*

class Diary() {
    private lateinit var diaryId: String
    private lateinit var diaryText: String
    private lateinit var diaryMood: String
    private lateinit var diaryDate: String

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