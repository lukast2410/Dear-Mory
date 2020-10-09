package edu.bluejack20_1.dearmory.activities

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import kotlinx.android.synthetic.main.activity_app.iv_main_background
import kotlinx.android.synthetic.main.activity_diary.*
import java.util.concurrent.atomic.AtomicInteger

class DiaryActivity : AppCompatActivity() {
    private lateinit var dialog: Dialog
    private lateinit var reffDB: DatabaseReference
    private val count = AtomicInteger()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_diary)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
        initializeToolbar()
        initializePopUpEditMood()
        diaryToDatabase()
    }

    private fun diaryToDatabase() {
//        reffDB = FirebaseDatabase.getInstance().getReference().child("User")
        fab_add_images.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                count.incrementAndGet()
                Toast.makeText(applicationContext, count.get(), Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun initializePopUpEditMood() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_mood_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        var angryMood = dialog.findViewById(R.id.angry_mood) as ImageView
        var happyMood = dialog.findViewById(R.id.happy_mood) as ImageView
        var sadMood = dialog.findViewById(R.id.sad_mood) as ImageView
        fab_edit_mood.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                dialog.show()
            }
        })
        angryMood.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(applicationContext, "Angry Mood", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        happyMood.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(applicationContext, "Happy Mood", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
        sadMood.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                Toast.makeText(applicationContext, "Sad Mood", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
}
