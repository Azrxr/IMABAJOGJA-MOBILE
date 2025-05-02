package com.imaba.imabajogja.ui.admin.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imaba.imabajogja.data.response.OrganizationFile
import com.imaba.imabajogja.databinding.ItemDocumentMinimalBinding
import com.imaba.imabajogja.ui.home.FileAdapter

class AdmFileAdapter (
    private val files: List<OrganizationFile>,
    private val onItemClicked: (OrganizationFile) -> Unit,
    private val onDeleteClicked: (OrganizationFile) -> Unit,
    private val isDeleteVisible: Boolean = false // default tidak kelihatan
) : RecyclerView.Adapter<AdmFileAdapter.FileViewHolder>() {

    class FileViewHolder(private val binding: ItemDocumentMinimalBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.tvTitle
        val description: TextView = binding.tvDescription
        val btnDelete = binding.btnDelete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileViewHolder {
        val binding = ItemDocumentMinimalBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FileViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FileViewHolder, position: Int) {
        val file = files[position]
        holder.title.text = file.title
        holder.description.text = file.description ?: "No description"

        holder.itemView.setOnClickListener {
            onItemClicked(file)
        }
        if (isDeleteVisible) {
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnDelete.setOnClickListener {
                onDeleteClicked(file)
            }
        } else {
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount() = files.size
}