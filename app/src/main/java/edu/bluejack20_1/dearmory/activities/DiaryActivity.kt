package edu.bluejack20_1.dearmory.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.ThemeManager
import edu.bluejack20_1.dearmory.adapters.ExpenseIncomeAdapter
import edu.bluejack20_1.dearmory.adapters.ImageAdapter
import edu.bluejack20_1.dearmory.factories.DiaryViewModelFactory
import edu.bluejack20_1.dearmory.factories.ExpenseIncomeViewModelFactory
import edu.bluejack20_1.dearmory.factories.ImageViewModelFactory
import edu.bluejack20_1.dearmory.fragments.ImageFragment
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.models.Image
import edu.bluejack20_1.dearmory.repositories.DiaryRepository
import edu.bluejack20_1.dearmory.repositories.ExpenseIncomeRepository
import edu.bluejack20_1.dearmory.repositories.ImageRepository
import edu.bluejack20_1.dearmory.viewmodels.DiaryViewModel
import edu.bluejack20_1.dearmory.viewmodels.ExpenseIncomeViewModel
import edu.bluejack20_1.dearmory.viewmodels.ImageViewModel
import kotlinx.android.synthetic.main.activity_app.iv_main_background
import kotlinx.android.synthetic.main.activity_diary.*
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@RequiresApi(Build.VERSION_CODES.O)
class DiaryActivity : AppCompatActivity(), ExpenseIncomeAdapter.ExpenseIncomeListener, ImageAdapter.ImageClickListener {
    private lateinit var dialog: Dialog
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diary: Diary
    private lateinit var userId: String
    private lateinit var expenseIncomeAdapter: ExpenseIncomeAdapter
    private lateinit var expenseIncomeViewModel: ExpenseIncomeViewModel
    private lateinit var imageAdapter: ImageAdapter
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var imageFragment: ImageFragment

    private val requestCode = 438
    private var imageUri: Uri? = null
    private lateinit var storageRef: StorageReference

    @SuppressLint("SimpleDateFormat")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(ThemeManager.setUpTheme())
        setContentView(R.layout.activity_diary)
        iv_main_background.setImageResource(ThemeManager.setUpBackground())
        userId = GoogleSignIn.getLastSignedInAccount(applicationContext)?.id.toString()
        initializeDiary()
        initializeStorage()
        initializeToolbar()
        initializePopUpEditMood()
        initializeFloatingActionButton()
    }

    private fun initializeDiary() {
        var success = false
        val factory = DiaryViewModelFactory(DiaryRepository.getInstance())
        diaryViewModel = ViewModelProviders.of(this, factory).get(DiaryViewModel::class.java)
        diaryViewModel.getDiary(userId).observe(this, Observer { d ->
            if (!d.getId().equals("false") && !success) {
                diary = d
                setBackgroundBasedOnMood()
                setEditDiaryText()
                val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
                val date = LocalDate.parse(diary.getDate()) as LocalDate
                toolbar_diary.title = date.format(formatter).toString()
                initializeImageRecyclerView()
                initializeExpenseIncomeRecyclerView()
                success = true
            }
        })
    }

    private fun initializeImageRecyclerView() {
        imageFragment = ImageFragment()
        rv_diary_images_container.setHasFixedSize(true)
        rv_diary_images_container.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val factory = ImageViewModelFactory(ImageRepository.getInstance())
        imageViewModel = ViewModelProviders.of(this, factory).get(ImageViewModel::class.java)
        imageViewModel.init(diary.getId())
        imageAdapter = ImageAdapter(imageViewModel.getImages().value!!, this)
        imageViewModel.getImages().observe(this, Observer<ArrayList<Image>>{imagesMap ->
            if(imagesMap.size <= 0){
                cv_diary_images_container.visibility = View.GONE
            }else{
                cv_diary_images_container.visibility = View.VISIBLE
                imageAdapter.notifyDataSetChanged()
                rv_diary_images_container.adapter = imageAdapter
            }
        })
    }

    private fun initializeStorage() {
        storageRef = FirebaseStorage.getInstance().reference
    }

    private fun initializeExpenseIncomeRecyclerView() {
        recycler_expense_income.setHasFixedSize(true)
        recycler_expense_income.layoutManager = LinearLayoutManager(this)
        val factory = ExpenseIncomeViewModelFactory(ExpenseIncomeRepository.getInstance())
        expenseIncomeViewModel =
            ViewModelProviders.of(this, factory).get(ExpenseIncomeViewModel::class.java)
        expenseIncomeViewModel.init(diary.getId())
        expenseIncomeAdapter = ExpenseIncomeAdapter(
            expenseIncomeViewModel.getExpenseIncomes()?.value!!,
            diary.getMood(),
            this
        )
        expenseIncomeViewModel.getExpenseIncomes().observe(this,
            Observer<ArrayList<ExpenseIncome>> {
                expenseIncomeAdapter.setDiaryMood(diary.getMood())
                expenseIncomeAdapter.notifyDataSetChanged()
                recycler_expense_income.adapter = expenseIncomeAdapter
            })
    }

    private fun initializePopUpEditMood() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.edit_mood_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val angryMood = dialog.findViewById(R.id.angry_mood) as ImageView
        val happyMood = dialog.findViewById(R.id.happy_mood) as ImageView
        val sadMood = dialog.findViewById(R.id.sad_mood) as ImageView
        fab_edit_mood.setOnClickListener { dialog.show() }
        angryMood.setOnClickListener {
            diary.setMood(Diary.ANGRY_MOOD)
            setBackgroundBasedOnMood()
            diaryViewModel.saveDiary(userId, diary)
            Toast.makeText(applicationContext, "Angry Mood", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        happyMood.setOnClickListener {
            diary.setMood(Diary.HAPPY_MOOD)
            setBackgroundBasedOnMood()
            diaryViewModel.saveDiary(userId, diary)
            Toast.makeText(applicationContext, "Happy Mood", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        sadMood.setOnClickListener {
            diary.setMood(Diary.SAD_MOOD)
            setBackgroundBasedOnMood()
            diaryViewModel.saveDiary(userId, diary)
            Toast.makeText(applicationContext, "Sad Mood", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
    }

    private fun initializeFloatingActionButton() {
        fab_add_expense_income.setOnClickListener {
            val intent = Intent(applicationContext, ExpenseIncomeActivity::class.java)
            intent.putExtra(Diary.DIARY_ID, diary.getId())
            intent.putExtra(ExpenseIncome.GO_TO_EXPENSE_INCOME, ExpenseIncome.ADD_EXPENSE_INCOME)
            startActivity(intent)
        }
        fab_add_images.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(intent, requestCode)
        }
    }

    private fun initializeToolbar() {
        setSupportActionBar(toolbar_diary)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    private fun setBackgroundBasedOnMood() {
        when (diary.getMood()) {
            Diary.HAPPY_MOOD -> rl_background_shading_mood.setBackgroundColor(getColor(R.color.happyMoodDiary))
            Diary.ANGRY_MOOD -> rl_background_shading_mood.setBackgroundColor(getColor(R.color.angryMoodDiary))
            Diary.SAD_MOOD -> rl_background_shading_mood.setBackgroundColor(getColor(R.color.sadMoodDiary))
        }
    }

    private fun setEditDiaryText() {
        et_edit_diary_text.setText(diary.getText())
        et_edit_diary_text.setSelection(et_edit_diary_text.length())
        et_edit_diary_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                diary.setText(et_edit_diary_text.text.toString().trim())
                diaryViewModel.saveDiary(userId, diary)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onExpenseIncomeClick(position: Int) {
        var temp = expenseIncomeViewModel.getExpenseIncomes().value?.get(position) as ExpenseIncome
        var intent: Intent = Intent(this, ExpenseIncomeActivity::class.java)
        intent.putExtra(ExpenseIncome.GO_TO_EXPENSE_INCOME, ExpenseIncome.UPDATE_EXPENSE_INCOME)
        intent.putExtra(Diary.DIARY_ID, diary.getId())
        intent.putExtra(ExpenseIncome.EXPENSE_INCOME, temp)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == this.requestCode && resultCode == Activity.RESULT_OK && data!!.data != null) {
            imageUri = data.data
//            try {
//                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
//                iv_main_background.setImageBitmap(bitmap)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
            Toast.makeText(applicationContext, "Uploading...", Toast.LENGTH_SHORT).show()
            uploadImage()
        }
    }

    private fun uploadImage() {
        if (imageUri != null) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle(getString(R.string.uploading))
            progressDialog.show()

            val imageRef = storageRef!!.child("images/" + UUID.randomUUID().toString())
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener { task ->
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.upload_success),
                        Toast.LENGTH_SHORT
                    ).show()
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        var image: Image = Image()
                            .setImageUrl(uri.toString())
                        imageViewModel.createImage(diary.getId(), image)
                        Log.d("checkviewupload", uri.toString())
                    }
                }
                .addOnFailureListener {
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        getString(R.string.upload_failed),
                        Toast.LENGTH_SHORT
                    ).show()
                }
                .addOnProgressListener { taskSnap ->
                    val progress = 100 * taskSnap.bytesTransferred / taskSnap.totalByteCount
                    progressDialog.setMessage("${getString(R.string.uploaded)} ${progress.toInt()}%...")
                }
        }
    }

    override fun onImageClick(position: Int) {
        imageFragment.setImages(imageViewModel.getImages().value!!, position, imageViewModel, diary.getId())
        imageFragment.show(supportFragmentManager, "customDialog")
    }

    override fun onBackPressed() {
        var entry: Int = supportFragmentManager.backStackEntryCount
        if (entry > 0){
            for(i in 0 until entry){
                supportFragmentManager.popBackStackImmediate()
            }
        }
        finish()
        super.onBackPressed()
    }
}
