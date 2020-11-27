package edu.bluejack20_1.dearmory.repositories

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome

class DiaryRepository private constructor() {
    private val refsDB: DatabaseReference = FirebaseDatabase.getInstance().reference
    private lateinit var diary: Diary
    private var diaryLiveData: MutableLiveData<Diary> = MutableLiveData()
    private var diaryModels: ArrayList<Diary> = ArrayList()
    private var diariesLiveData: MutableLiveData<ArrayList<Diary>> = MutableLiveData()
    private var totalModels: ArrayList<HashMap<String, ExpenseIncome>> = ArrayList()
    private var totalLiveData: MutableLiveData<ArrayList<HashMap<String, ExpenseIncome>>> = MutableLiveData()
    private lateinit var diaryChild: ChildEventListener
    private lateinit var totalsChild: ChildEventListener

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

    fun getDiaries(userId: String, date: String): MutableLiveData<ArrayList<Diary>> {
        loadDiaries(userId, date)

        diariesLiveData.value = diaryModels

        return diariesLiveData
    }

    fun getTotals(): MutableLiveData<ArrayList<HashMap<String, ExpenseIncome>>>{
        totalLiveData.value = totalModels

        return totalLiveData
    }

    private fun loadDiaries(userId: String, date: String) {
        if (diaryModels.size > 0)
            diaryModels.clear()
        if (totalModels.size > 0)
            totalModels.clear()

        diaryChild = refsDB.child(Diary.DIARY).child(userId).addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val getDate = snapshot.child("date").value.toString()

                if(getDate.contains(date)){
                    val diaryDate = snapshot.child("date").value.toString()
                    val diaryIndex = diaryModels.indexOfFirst { diary -> diary.getDate() == diaryDate }
                    val key = snapshot.key as String

                    if(diaryIndex < 0){
                        diaryModels.add(
                            Diary()
                                .setId(snapshot.child("id").value.toString())
                                .setDate(snapshot.child("date").value.toString())
                                .setText(snapshot.child("text").value.toString())
                                .setMood(snapshot.child("mood").value.toString())
                        )
                        diariesLiveData.postValue(diaryModels)

                        val position = diaryModels.size
                        if (totalModels.size < position)
                            totalModels.add(HashMap())

                        sortDiaryDesc()
                        totalsChild = refsDB.child(ExpenseIncome.EXPENSE_INCOME).child(key).addChildEventListener(object :
                            ChildEventListener {
                            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                                val totalKey = snapshot.key.toString()
                                val idx = diaryModels.indexOfFirst { diary -> diary.getDate() == diaryDate }

                                totalModels[idx].put(
                                    totalKey,
                                    ExpenseIncome().setId(snapshot.child("id").value.toString())
                                        .setNotes(snapshot.child("notes").value.toString())
                                        .setTime(snapshot.child("time").value.toString())
                                        .setAmount(snapshot.child("amount").value.toString().toLong())
                                )
                                totalLiveData.postValue(totalModels)
                            }

                            override fun onChildChanged(
                                snapshot: DataSnapshot,
                                previousChildName: String?
                            ) {
                                val totalKey = snapshot.key.toString()
                                val idx = diaryModels.indexOfFirst { diary -> diary.getDate() == diaryDate }

                                totalModels[idx].get(totalKey)
                                    ?.setId(snapshot.child("id").value.toString())
                                    ?.setNotes(snapshot.child("notes").value.toString())
                                    ?.setTime(snapshot.child("time").value.toString())
                                    ?.setAmount(snapshot.child("amount").value.toString().toLong())
                                totalLiveData.postValue(totalModels)
                            }

                            override fun onChildRemoved(snapshot: DataSnapshot) {
                                val totalKey = snapshot.key.toString()
                                val idx = diaryModels.indexOfFirst { diary -> diary.getDate() == diaryDate }

                                totalModels[idx].remove(totalKey)
                                totalLiveData.postValue(totalModels)
                            }

                            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
                            override fun onCancelled(error: DatabaseError) {}
                        })
                    }
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                var idx = diaryModels.indexOfFirst { diary -> diary.getId() == snapshot.key }
                if(idx >= 0){
                    diaryModels[idx]
                        .setId(snapshot.child("id").value.toString())
                        .setDate(snapshot.child("date").value.toString())
                        .setText(snapshot.child("text").value.toString())
                        .setMood(snapshot.child("mood").value.toString())
                    diariesLiveData.postValue(diaryModels)
                }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun removeEventListener(){
        refsDB.removeEventListener(diaryChild)
        refsDB.removeEventListener(totalsChild)
    }

    fun getDiary(userId: String, date: String): MutableLiveData<Diary> {
        diary = Diary()
        loadDiary(userId, date)

        diaryLiveData.value = diary

        return diaryLiveData
    }

    private fun loadDiary(userId: String, date: String) {
        var checkToday: Boolean = false
        refsDB.child(Diary.DIARY).child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (data: DataSnapshot in snapshot.children) {
                            if (data.child("date").value.toString() == date) {
                                checkToday = true
                                diary.setId(data.child("id").value.toString())
                                    .setText(data.child("text").value.toString())
                                    .setMood(data.child("mood").value.toString())
                                    .setDate(data.child("date").value.toString())
                                diaryLiveData.postValue(diary)
                                break
                            }
                        }
                    }
                    if (!checkToday) {
                        createDiary(userId, date)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun createDiary(userId: String, diaryDate: String) {
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

    private fun sortDiaryDesc(){
        for (i in 0 until diaryModels.size - 1) {
            var m = i
            for (j in i + 1 until diaryModels.size) {
                if (diaryModels.get(m).getDate() < diaryModels.get(j).getDate()) m = j
            }

            val tempDiary: Diary = diaryModels[i]
            diaryModels[i] = diaryModels[m]
            diaryModels[m] = tempDiary

            val tempTotal: HashMap<String, ExpenseIncome> = totalModels[i]
            totalModels[i] = totalModels[m]
            totalModels[m] = tempTotal
        }
    }
}