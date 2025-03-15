package com.imaba.imabajogja.ui.campus

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.databinding.ItemDocumentMinimalBinding

class DocumentAdapter(
    private var documentList: List<Pair<String, String?>>, // Pair (Nama Dokumen, URL Dokumen)
    private val onAddClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    companion object {
        private const val TYPE_DOCUMENT = 1
        private const val TYPE_PHOTO = 2
    }

    override fun getItemViewType(position: Int): Int {
        return if (documentList[position].first.contains("Foto")) TYPE_PHOTO else TYPE_DOCUMENT
    }

    inner class ViewHolder(private val binding: ItemDocumentMinimalBinding, private val viewType: Int) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doc: Pair<String, String?>) {
            binding.tvTitle.text = doc.first

            if (!doc.second.isNullOrEmpty()) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnAddDoc.visibility = View.GONE
                binding.tvDescription.text = "✅ Sudah diunggah"
                Log.d("DocumentAdapter", "Document: ${doc.first}, URL: ${doc.second}")

                binding.root.setOnClickListener{
                    if (viewType == TYPE_DOCUMENT)
                    openPdf(doc.second!!)
                    else (viewType == TYPE_PHOTO)
                    showFullImage(doc.second!!)
                }

            } else {
                binding.btnDelete.visibility = View.GONE
                binding.btnAddDoc.visibility = View.VISIBLE
                binding.tvDescription.text = "❌ Belum diunggah"
                binding.root.setOnClickListener(null)
            }

            binding.btnAddDoc.setOnClickListener {
                onAddClick(doc.first)
            }

            binding.btnDelete.setOnClickListener {
                onDeleteClick(doc.first)
            }
        }
        private fun openPdf(url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(url), "application/pdf")
            intent.flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            context.startActivity(intent)
        }
        private fun showFullImage(url: String) {
            val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_full_image, null)
            val photoView = dialogView.findViewById<ImageView>(R.id.photoView)

            Glide.with(context)
                .load(url)
                .placeholder(R.drawable.ic_image_broken)
                .into(photoView)

            AlertDialog.Builder(context)
                .setView(dialogView)
                .setPositiveButton("Tutup") { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDocumentMinimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding, viewType)

    }

    override fun getItemCount(): Int = documentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documentList[position])
    }
}
