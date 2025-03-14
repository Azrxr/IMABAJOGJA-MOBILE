package com.imaba.imabajogja.ui.campus

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imaba.imabajogja.databinding.ItemDocumentMinimalBinding

class DocumentAdapter(
    private var documentList: List<Pair<String, String?>>, // Pair (Nama Dokumen, URL Dokumen)
    private val onAddClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit,
    private val context: Context
) : RecyclerView.Adapter<DocumentAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemDocumentMinimalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doc: Pair<String, String?>) {
            binding.tvTitle.text = doc.first

            if (!doc.second.isNullOrEmpty()) {
                binding.btnDelete.visibility = View.VISIBLE
                binding.btnAddDoc.visibility = View.GONE
                binding.tvDescription.text = "✅ Sudah diunggah"
                Log.d("DocumentAdapter", "Document: ${doc.first}, URL: ${doc.second}")
                binding.root.setOnClickListener{
                    openPdf(doc.second!!)
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDocumentMinimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = documentList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(documentList[position])
    }
    fun updateData(newList: List<Pair<String, String?>>) {
        documentList = newList
        notifyDataSetChanged()
    }
}
