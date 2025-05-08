package com.imaba.imabajogja.ui.admin.campuse

import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.RecyclerView
import com.imaba.imabajogja.data.response.StudyPlansItem
import com.imaba.imabajogja.databinding.ItemStudyPlansBinding

class AdmStudyPlansAdapter(
    private val onDeleteClick: (planId: Int) -> Unit,
    private val onStatusChanged: (memberId: Int, planId: Int, status: String) -> Unit
) : RecyclerView.Adapter<AdmStudyPlansAdapter.ViewHolder>() {

    private var editingPositions = mutableListOf<Int>()
    private val items = mutableListOf<StudyPlansItem>()
    private val statusOptions = listOf("pending", "accepted", "rejected", "active")

    fun submitList(newItems: List<StudyPlansItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    fun setEditingPositions(editAll: Boolean) {
        if (editAll) {
            editingPositions.addAll(items.indices) // Enable edit mode for all items
        } else {
            editingPositions.clear() // Disable edit mode for all items
        }
        notifyDataSetChanged()
    }

    inner class ViewHolder(private val binding: ItemStudyPlansBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(data: StudyPlansItem, position: Int) {
            val context = binding.root.context
            val statusAdapter = ArrayAdapter(
                context, android.R.layout.simple_dropdown_item_1line,
                statusOptions.map { it.capitalize() } // Tampilkan dengan huruf kapital
            )

            binding.apply {
                // Set dropdown properties
                dropdownStatus.apply {
                    setAdapter(statusAdapter)
                    setText(data.status?.capitalize() ?: "Pending", false)
                    threshold = 1 // Mulai mencari setelah 1 karakter
                    inputType = InputType.TYPE_NULL // Nonaktifkan keyboard

                    // Handle item selection
                    setOnItemClickListener { _, _, pos, _ ->
                        val selectedStatus = statusOptions[pos]
                        data.memberId?.let { memberId ->
                            data.studyPlanId?.let { planId ->
                                onStatusChanged(memberId, planId, selectedStatus)
                                // Update tampilan langsung
                                tvStatus.text = selectedStatus.capitalize()
                                data.status = selectedStatus
                            }
                        }
                    }
                }

                tvPerguruanTinggi.text = data.university ?: "-"
                tvProgramStudy.text = data.programStudy ?: "-"
                tvStatus.text = data.status?.capitalize() ?: "-"

                // Toggle edit mode
                if (editingPositions.contains(position)) {
                    tvStatus.visibility = View.GONE
                    dropdownStatus.visibility = View.VISIBLE
                    btnDelete.visibility = View.VISIBLE
                } else {
                    tvStatus.visibility = View.VISIBLE
                    dropdownStatus.visibility = View.GONE
                    btnDelete.visibility = View.GONE
                }

                btnDelete.setOnClickListener {
                    data.studyPlanId?.let { id -> onDeleteClick(id) }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemStudyPlansBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.bind(data, position)
    }

}