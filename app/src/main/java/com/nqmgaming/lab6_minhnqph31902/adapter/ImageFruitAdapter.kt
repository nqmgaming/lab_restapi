package com.nqmgaming.lab6_minhnqph31902.adapter

import android.app.AlertDialog
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nqmgaming.lab6_minhnqph31902.databinding.ItemPhotoProductBinding

class ImageFruitAdapter(
    private val context: Context,
    private val listImage: MutableList<Uri>
) : RecyclerView.Adapter<ImageFruitAdapter.ImageFruitViewHolder>() {
    inner class ImageFruitViewHolder(private val binding: ItemPhotoProductBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(uri: Uri) {
            Glide.with(binding.root)
                .load(uri)
                .into(binding.view)

            binding.closeBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val builder = AlertDialog.Builder(binding.root.context)
                    builder.setTitle("Delete Image")
                    builder.setMessage("Are you sure you want to delete this image?")
                    builder.setPositiveButton("Yes") { _, _ ->
                        listImage.removeAt(position)
                        notifyItemRemoved(position)
                    }
                    builder.setNegativeButton("No") { _, _ -> }
                    builder.create().show()
                }
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageFruitViewHolder {
        val binding =
            ItemPhotoProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageFruitViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return listImage.size
    }

    override fun onBindViewHolder(holder: ImageFruitViewHolder, position: Int) {
        holder.bind(listImage[position])
    }

}