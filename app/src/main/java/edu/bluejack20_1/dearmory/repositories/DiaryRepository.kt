package edu.bluejack20_1.dearmory.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DiaryRepository private constructor() {
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().reference
    private var diary: Diary = Diary()
    private var diaryLiveData: MutableLiveData<Diary> = MutableLiveData()
    private var diaryModels: ArrayList<Diary> = ArrayList()
    private var diariesLiveData: MutableLiveData<ArrayList<Diary>> = MutableLiveData()
    private var totalModels: ArrayList<HashMap<String, ExpenseIncome>> = ArrayList()
    private var totalLiveData: MutableLiveData<ArrayList<HashMap<String, ExpenseIncome>>> = MutableLiveData()

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

    fun getTotals(): MutableLiveData<ArrayList<HashMap<String, ExpenseIncome>>>{
        totalLiveData.value = totalModels

        return totalLiveData
    }

    private fun loadDiaries(userId: String) {
        if (diaryModels.size > 0)
            diaryModels.clear()
        if (totalModels.size > 0)
            totalModels.clear()
        refsDB.child(Diary.DIARY).child(userId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var key = snapshot.key as String
                diaryModels.add(
                    Diary()
                        .setId(snapshot.child("id").value.toString())
                        .setDate(snapshot.child("date").value.toString())
                        .setText(snapshot.child("text").value.toString())
                        .setMood(snapshot.child("mood").value.toString())
                )
                diariesLiveData.postValue(diaryModels)

                var position = diaryModels.size
                refsDB.child(ExpenseIncome.EXPENSE_INCOME).child(key).addChildEventListener(object : ChildEventListener{
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if(totalModels.size < position)
                            totalModels.add(HashMap())
                        var totalKey = snapshot.key.toString()
                        totalModels[position-1].put(
                            totalKey,
                            ExpenseIncome().setId(snapshot.child("id").value.toString())
                                .setNotes(snapshot.child("notes").value.toString())
                                .setTime(snapshot.child("time").value.toString())
                                .setAmount(snapshot.child("amount").value.toString().toLong())
                        )
                        totalLiveData.postValue(totalModels)
                    }

                    override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                        var totalKey = snapshot.key.toString()
                        totalModels[position-1].get(totalKey)
                            ?.setId(snapshot.child("id").value.toString())
                            ?.setNotes(snapshot.child("notes").value.toString())
                            ?.setTime(snapshot.child("time").value.toString())
                            ?.setAmount(snapshot.child("amount").value.toString().toLong())
                        totalLiveData.postValue(totalModels)
                    }

                    override fun onChildRemoved(snapshot: DataSnapshot) {
                        var totalKey = snapshot.key.toString()
                        totalModels[position-1].remove(totalKey)
                        totalLiveData.postValue(totalModels)
                    }

                    override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var idx = diaryModels.indexOfFirst { diary -> diary.getId() == snapshot.key }
                diaryModels[idx]
                    .setId(snapshot.child("id").value.toString())
                    .setDate(snapshot.child("date").value.toString())
                    .setText(snapshot.child("text").value.toString())
                    .setMood(snapshot.child("mood").value.toString())
                diariesLiveData.postValue(diaryModels)
            }

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

    private fun loadDiary(userId: String) {
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