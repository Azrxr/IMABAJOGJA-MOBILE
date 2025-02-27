package com.imaba.imabajogja.ui.home

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imaba.imabajogja.data.response.OrganizationFile
import com.imaba.imabajogja.databinding.ItemDocumentBinding
import com.imaba.imabajogja.databinding.ItemDocumentMinimalBinding

class FileAdapter (private val files: List<OrganizationFile>, private val onItemClicked: (OrganizationFile) -> Unit) :
    RecyclerView.Adapter<FileAdapter.FileViewHolder>() {

    class FileViewHolder(private val binding: ItemDocumentMinimalBinding) : RecyclerView.ViewHolder(binding.root) {
        val title: TextView = binding.tvTitle
        val description: TextView = binding.tvDescription
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
    }

    override fun getItemCount() = files.size
}