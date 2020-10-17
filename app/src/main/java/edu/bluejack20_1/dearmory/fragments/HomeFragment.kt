package edu.bluejack20_1.dearmory.fragments

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.DiaryActivity
import edu.bluejack20_1.dearmory.adapters.DiaryAdapter
import edu.bluejack20_1.dearmory.factories.DiaryViewModelFactory
import edu.bluejack20_1.dearmory.models.Diary
import edu.bluejack20_1.dearmory.models.ExpenseIncome
import edu.bluejack20_1.dearmory.models.Image
import edu.bluejack20_1.dearmory.repositories.DiaryRepository
import edu.bluejack20_1.dearmory.viewmodels.DiaryViewModel
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.channels.consumesAll
import java.util.function.Predicate

@RequiresApi(Build.VERSION_CODES.O)
class HomeFragment : Fragment(), DiaryAdapter.DiaryClickListener {
    private lateinit var userId: String
    private lateinit var diaryViewModel: DiaryViewModel
    private lateinit var diariesAdapter: DiaryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        userId = GoogleSignIn.getLastSignedInAccount(context)?.id.toString()
        initializeButton()
        initializeDiaryRecyclerView()
    }

    private fun initializeDiaryRecyclerView() {
        rv_diaries_container.setHasFixedSize(true)
        rv_diaries_container.layoutManager = LinearLayoutManager(context)
        val factory = DiaryViewModelFactory(DiaryRepository.getInstance())
        diaryViewModel = ViewModelProviders.of(this, factory).get(DiaryViewModel::class.java)
        diaryViewModel.init(userId)
        diariesAdapter = DiaryAdapter(diaryViewModel.getDiaries().value!!, diaryViewModel.getTotals().value!!, this)
        diaryViewModel.getDiaries().observe(viewLifecycleOwner, Observer<ArrayList<Diary>> {
            diariesAdapter.notifyDataSetChanged()
            rv_diaries_container.adapter = diariesAdapter
        })
        diaryViewModel.getTotals().observe(viewLifecycleOwner, Observer<ArrayList<HashMap<String, ExpenseIncome>>> {
            diariesAdapter.notifyDataSetChanged()
            rv_diaries_container.adapter = diariesAdapter
        })
    }

    private fun initializeButton() {
        fab_write_diary.setOnClickListener {
            var intent = Intent(context, DiaryActivity::class.java)
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            intent.putExtra(Diary.SEND_DIARY_TYPE, Diary.WRITE_DIARY)
            startActivity(intent)
        }
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }

    override fun onDiaryClicked(position: Int) {
        var selectedDiary: Diary = diaryViewModel.getDiaries().value!![position]
        var intent = Intent(context, DiaryActivity::class.java)
        intent.putExtra(Diary.SEND_DIARY_TYPE, Diary.SELECT_DIARY)
        intent.putExtra(Diary.DIARY, selectedDiary)
        startActivity(intent)
    }
}