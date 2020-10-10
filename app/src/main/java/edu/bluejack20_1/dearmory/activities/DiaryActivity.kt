package edu.bluejack20_1.dearmory.activities

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.database.*
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.User
import kotlinx.android.synthetic.main.activity_app.iv_main_background
import kotlinx.android.synthetic.main.activity_diary.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


@RequiresApi(Build.VERSION_CODES.O)
class DiaryActivity : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private lateinit var reffDB: DatabaseReference
    private lateinit var diary: Diary
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_diary)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
        userId = GoogleSignIn.getLastSignedInAccount(applicationContext)?.id.toString()
        diaryToDatabase()
        initializeToolbar()
        initializePopUpEditMood()
    }

    private fun diaryToDatabase() {
        reffDB = FirebaseDatabase.getInstance().getReference("Diary").child(userId)
        checkDiaryToday()
    }

    private fun checkDiaryToday() {
        val dateNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        var checkToday: Boolean = false
        reffDB.addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data: DataSnapshot in snapshot.getChildren()) {
                    if (data.child("date").getValue().toString().equals(dateNow)) {
                        checkToday = true
                        getDiary(data)
                        break
                    }
                }
                if(!checkToday){ createDiary() }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "ERROR", Toast.LENGTH_SHORT).show()
                et_edit_diary_text.setText("")
            }
        })
    }

    private fun getDiary(data: DataSnapshot) {
        diary = Diary().setId(data.child("id").getValue().toString())
            .setText(data.child("text").getValue().toString())
            .setMood(data.child("mood").getValue().toString())
            .setDate(data.child("date").getValue().toString())
        val parseDate = LocalDate.parse(diary.getDate())
        val stringDate = parseDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"))
        toolbar_diary.title = stringDate
        setEditDiaryText()
    }

    private fun createDiary() {
        val diaryDate: String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val diaryText: String = et_edit_diary_text.text.toString().trim()
        val diaryMood: String = "Happy"
        val diaryId: String = reffDB.push().key.toString()
        diary = Diary().setId(diaryId)
            .setText(diaryText)
            .setMood(diaryMood)
            .setDate(diaryDate)
        setEditDiaryText()
        reffDB.child(diaryId).setValue(diary)
    }

    private fun setEditDiaryText() {
        et_edit_diary_text.setText(diary.getText())
        et_edit_diary_text.setSelection(et_edit_diary_text.length())
        et_edit_diary_text.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveDiary()
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun saveDiary() {
        diary.setText(et_edit_diary_text.text.toString().trim())
        reffDB.child(diary.getId()).setValue(diary)
    }

    private fun initializePopUpEditMood() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_mood_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var angryMood = dialog.findViewById(R.id.angry_mood) as ImageView
        var happyMood = dialog.findViewById(R.id.happy_mood) as ImageView
        var sadMood = dialog.findViewById(R.id.sad_mood) as ImageView
        fab_edit_mood.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                dialog.show()
            }
        })
        angryMood.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                diary.setMood(Diary.ANGRY_MOOD)
                saveDiary()
                Toast.makeText(applicationContext, "Angry Mood", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        happyMood.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                diary.setMood(Diary.HAPPY_MOOD)
                saveDiary()
                Toast.makeText(applicationContext, "Happy Mood", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        sadMood.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                diary.setMood(Diary.SAD_MOOD)
                saveDiary()
                Toast.makeText(applicationContext, "Sad Mood", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        fab_add_expense_income.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                var intent = Intent(applicationContext, ExpenseIncomeActivity::class.java)
                intent.putExtra(Diary.DIARY_ID, diary.getId())
                startActivity(intent)
            }
        })
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_diary)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
}
