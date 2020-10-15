package edu.bluejack20_1.dearmory.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import edu.bluejack20_1.dearmory.R
import edu.bluejack20_1.dearmory.models.Image

class ImageAdapter(): RecyclerView.Adapter<ImageAdapter.ViewHolder>(){
    private lateinit var imageClickListener: ImageClickListener
    private lateinit var imageModels: ArrayList<Image>

    constructor(imageModels: ArrayList<Image>, imageClickListener: ImageClickListener): this(){
        this.imageModels = imageModels
        this.imageClickListener = imageClickListener
    }

    class ViewHolder(itemView: View, imageClickListener: ImageClickListener): RecyclerView.ViewHolder(itemView), View.OnClickListener{
        var listener = imageClickListener
        var imageView: ImageView = itemView.findViewById(R.id.iv_image_item)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            listener.onImageClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view: View = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view, imageClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get()
            .load(imageModels[position].getImageUrl())
            .into(holder.imageView)
    }

    override fun getItemCount(): Int {
        return imageModels.size
    }

    interface ImageClickListener{
        fun onImageClick(position: Int)
    }
}