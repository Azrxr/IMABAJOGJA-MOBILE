package com.imaba.imabajogja.ui.campus

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.imaba.imabajogja.data.response.StudyPlans
import com.imaba.imabajogja.databinding.ItemStudyPlansBinding

class StudyPlansAdapter(
    private val onDeleteClick: (Int) -> Unit,
    var isEditing: Boolean
) : ListAdapter<StudyPlans, StudyPlansAdapter.ViewHolder>(DIFF_CALLBACK) {

    fun setEditingMode(editing: Boolean) {
        isEditing = editing
        notifyDataSetChanged() // ⬅️ WAJIB agar semua item di-refresh dan bind ulang
    }


    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StudyPlans>() {
            override fun areItemsTheSame(oldItem: StudyPlans, newItem: StudyPlans): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: StudyPlans, newItem: StudyPlans): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ViewHolder(private val binding: ItemStudyPlansBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(studyPlan: StudyPlans) {
            binding.tvPerguruanTinggi.text = studyPlan.university.name
            binding.tvProgramStudy.text = studyPlan.programStudy.name
            binding.tvJenjang.text = studyPlan.programStudy.jenjang
            binding.tvStatus.text = studyPlan.status

            binding.btnDelete.setOnClickListener {
                onDeleteClick(studyPlan.id)
            }

            binding.btnDelete.visibility = if (isEditing) View.VISIBLE else View.GONE
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding =
            ItemStudyPlansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}