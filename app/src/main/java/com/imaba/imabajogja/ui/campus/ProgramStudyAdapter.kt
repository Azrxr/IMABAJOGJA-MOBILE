package com.imaba.imabajogja.ui.campus

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.imaba.imabajogja.data.response.DataItemProgramStudy
import com.imaba.imabajogja.databinding.ItemProgramStudyBinding

class ProgramStudyAdapter(
    private val onAddClick: (DataItemProgramStudy) -> Unit
) : ListAdapter<DataItemProgramStudy, ProgramStudyAdapter.ViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemProgramStudy>() {
            override fun areItemsTheSame(old: DataItemProgramStudy, new: DataItemProgramStudy) =
                old.programStudyId == new.programStudyId

            override fun areContentsTheSame(old: DataItemProgramStudy, new: DataItemProgramStudy) =
                old == new
        }
    }

    inner class ViewHolder(private val binding: ItemProgramStudyBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItemProgramStudy) {
            binding.tvPerguruanTinggi.text = item.university
            binding.tvFaculty.text = item.faculty
            binding.tvProgramStudy.text = item.programStudy
            binding.tvJenjang.text = item.jenjang

            binding.btnAdd.setOnClickListener {
                onAddClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemProgramStudyBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) =
        holder.bind(getItem(position))
}
