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
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.activities.DiaryActivity
import edu.bluejack20_1.dearmory.models.Image
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.coroutines.channels.consumesAll
import java.util.function.Predicate

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fab_write_diary.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                var intent = Intent(context, DiaryActivity::class.java)
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
        })
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}
