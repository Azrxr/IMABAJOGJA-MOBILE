package com.imaba.imabajogja.ui.campus

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.HomePhotoItem
import com.imaba.imabajogja.databinding.ItemPhotoBinding

class HomePhotoAdapter(
    private var homePhotos: List<HomePhotoItem>,
    private val onDeleteClick: (HomePhotoItem) -> Unit
) : RecyclerView.Adapter<HomePhotoAdapter.HomePhotoViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePhotoViewHolder {
        val binding = ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HomePhotoViewHolder(binding)
    }

    override fun getItemCount(): Int = homePhotos.size

    override fun onBindViewHolder(holder: HomePhotoViewHolder, position: Int) {
        holder.bind(homePhotos[position])
    }

    inner class HomePhotoViewHolder(private val binding: ItemPhotoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(photo: HomePhotoItem) {
            Glide.with(binding.root.context)
                .load(photo.photoImgUrl)
                .placeholder(R.drawable.ic_image_broken)
                .into(binding.ivPhoto)

            binding.tvPhotoTitle.text = photo.photoTitle

            // 🔍 Klik foto untuk melihat lebih besar
            binding.ivPhoto.setOnClickListener {
                showFullImageDialog(binding.root.context, photo.photoImgUrl)
            }

            // 🗑️ Hapus foto
            binding.btnDelete.setOnClickListener {
                onDeleteClick(photo)
                notifyDataSetChanged()
            }
        }
    }

    private fun showFullImageDialog(context: Context, imageUrl: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_full_image, null)
        val photoView = dialogView.findViewById<ImageView>(R.id.photoView)

        Glide.with(context)
            .load(imageUrl)
            .placeholder(R.drawable.ic_image_broken)
            .into(photoView)

        AlertDialog.Builder(context)
            .setView(dialogView)
            .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
            .show()
    }

}
