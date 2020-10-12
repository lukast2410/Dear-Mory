package edu.bluejack20_1.dearmory.repositories

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import kotlinx.android.synthetic.main.activity_diary.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryRepository private constructor(){
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference(Diary.DIARY)
    private var diary: Diary = Diary()
    private var diaryLiveData: MutableLiveData<Diary> = MutableLiveData()

    companion object{
        var instance: DiaryRepository? = null
        @JvmName("getInstance1")
        fun getInstance(): DiaryRepository{
            if(instance == null){
                instance = DiaryRepository()
            }
            return instance as DiaryRepository
        }
    }

    fun getDiary(userId: String): MutableLiveData<Diary>{
        loadDiary(userId)

        diaryLiveData.value = diary

        return diaryLiveData
    }

    fun loadDiary(userId: String){
        val dateNow: String = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
        } else {
            "Error"
        }
        var checkToday: Boolean = false
        refsDB.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.children) {
                    if (data.child("date").value.toString() == dateNow) {
                        checkToday = true
                        diary.setId(data.child("id").getValue().toString())
                            .setText(data.child("text").getValue().toString())
                            .setMood(data.child("mood").getValue().toString())
                            .setDate(data.child("date").getValue().toString())
                        diaryLiveData.postValue(diary)
                        break
                    }
                }
                if(!checkToday){ createDiary(userId) }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun createDiary(userId: String){
        val diaryDate: String = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
        } else { "Error" }
        val diaryText: String = ""
        val diaryMood: String = Diary.HAPPY_MOOD
        val diaryId: String = refsDB.push().key.toString()
        diary.setId(diaryId)
            .setMood(diaryMood)
            .setText(diaryText)
            .setDate(diaryDate)
        refsDB.child(userId).child(diaryId).setValue(diary)
        diaryLiveData.postValue(diary)
    }

    fun saveDiary(userId: String, diary: Diary){
        refsDB.child(userId).child(diary.getId()).setValue(diary)
    }
}