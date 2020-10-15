package edu.bluejack20_1.dearmory.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.Diary
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryRepository private constructor() {
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().getReference()
    private var diary: Diary = Diary()
    private var diaryLiveData: MutableLiveData<Diary> = MutableLiveData()
    private var diaryModels: ArrayList<Diary> = ArrayList()
    private var diariesLiveData: MutableLiveData<ArrayList<Diary>> = MutableLiveData()

    companion object {
        var instance: DiaryRepository? = null

        @JvmName("getInstance1")
        fun getInstance(): DiaryRepository {
            if (instance == null) {
                instance = DiaryRepository()
            }
            return instance as DiaryRepository
        }
    }

    fun getDiaries(userId: String): MutableLiveData<ArrayList<Diary>> {
        loadDiaries(userId)

        diariesLiveData.value = diaryModels

        return diariesLiveData
    }

    private fun loadDiaries(userId: String) {
        if (diaryModels.size > 0)
            diaryModels.clear()
        refsDB.child(Diary.DIARY).child(userId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var key = snapshot.key as String
                diaryModels.add(
                    Diary()
                        .setId(snapshot.child("id").getValue().toString())
                        .setDate(snapshot.child("date").getValue().toString())
                        .setText(snapshot.child("text").getValue().toString())
                        .setMood(snapshot.child("mood").getValue().toString())
                )
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onChildRemoved(snapshot: DataSnapshot) {}

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getDiary(userId: String): MutableLiveData<Diary> {
        loadDiary(userId)

        diaryLiveData.value = diary

        return diaryLiveData
    }

    fun loadDiary(userId: String) {
        val dateNow: String =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
            } else {
                "Error"
            }
        var checkToday: Boolean = false
        refsDB.child(Diary.DIARY).child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
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
                    }
                    if (!checkToday) {
                        createDiary(userId)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun createDiary(userId: String) {
        val diaryDate: String =
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
            } else {
                "Error"
            }
        val diaryText: String = ""
        val diaryMood: String = Diary.HAPPY_MOOD
        val diaryId: String = refsDB.push().key.toString()
        diary.setId(diaryId)
            .setMood(diaryMood)
            .setText(diaryText)
            .setDate(diaryDate)
        refsDB.child(Diary.DIARY)
            .child(userId)
            .child(diaryId)
            .setValue(diary)
        diaryLiveData.postValue(diary)
    }

    fun saveDiary(userId: String, diary: Diary) {
        refsDB.child(Diary.DIARY)
            .child(userId)
            .child(diary.getId())
            .setValue(diary)
    }
}