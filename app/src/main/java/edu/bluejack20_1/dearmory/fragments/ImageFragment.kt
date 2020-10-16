package edu.bluejack20_1.dearmory.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.models.Image
import edu.bluejack20_1.dearmory.viewmodels.ImageViewModel
import kotlinx.android.synthetic.main.fragment_image.*

@RequiresApi(Build.VERSION_CODES.N)
class ImageFragment : DialogFragment() {
    private val storageRefs: FirebaseStorage = FirebaseStorage.getInstance()
    private lateinit var imageViewModel: ImageViewModel
    private lateinit var diaryId: String
    private lateinit var images: ArrayList<Image>
    private var position: Int = 0
    private var showFAB: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.FullScreenDialogFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeImage()
        initializeButton()
        initializeDeleteConfirmation()
    }

    private fun initializeImage() {
        Picasso.get().load(images[position].getImageUrl()).into(iv_show_image_fragment)
        if (position == 0)
            fab_previous_image.visibility = View.GONE
        if (position == images.size-1)
            fab_next_image.visibility = View.GONE
    }

    private fun initializeButton() {
        btn_close_image.setOnClickListener {

            activity?.supportFragmentManager!!.beginTransaction().hide(this).addToBackStack(null).remove(this).commit()
        }
        rl_image_fragment_container.setOnClickListener {
            if(showFAB){
                fab_next_image.visibility = View.GONE
                fab_previous_image.visibility = View.GONE
            }else{
                if (position != 0)
                    fab_previous_image.visibility = View.VISIBLE
                if (position != images.size-1)
                    fab_next_image.visibility = View.VISIBLE
            }
            showFAB = !showFAB
        }
        fab_next_image.setOnClickListener{
            position++
            fab_previous_image.visibility = View.VISIBLE
            if (position == images.size-1)
                fab_next_image.visibility = View.GONE
            Picasso.get().load(images[position].getImageUrl()).into(iv_show_image_fragment)
        }
        fab_previous_image.setOnClickListener{
            position--
            fab_next_image.visibility = View.VISIBLE
            if (position == 0)
                fab_previous_image.visibility = View.GONE
            Picasso.get().load(images[position].getImageUrl()).into(iv_show_image_fragment)
        }
    }

    private fun initializeDeleteConfirmation(){
        btn_delete_image.setOnClickListener {
            rl_delete_confirmation_container.visibility = View.VISIBLE
        }
        rl_delete_confirmation_container.setOnClickListener{
            rl_delete_confirmation_container.visibility = View.GONE
        }
        btn_confirm_no.setOnClickListener{
            rl_delete_confirmation_container.visibility = View.GONE
        }
        btn_confirm_yes.setOnClickListener{
//            delete image from database and firebase storage
            val url = images[position].getImageUrl()
            val imageRefs = storageRefs.getReferenceFromUrl(url)
            imageRefs.delete().addOnSuccessListener {
                imageViewModel.deleteImage(diaryId, images[position])
                Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show()
                activity?.supportFragmentManager!!.beginTransaction().addToBackStack("done").remove(this).commit()
            }
        }
    }

    fun setImages(images: ArrayList<Image>, position: Int, imageViewModel: ImageViewModel, diaryId: String){
        this.images = images
        this.position = position
        this.imageViewModel = imageViewModel
        this.diaryId = diaryId
        showFAB = true
    }
}