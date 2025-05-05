package com.imaba.imabajogja.ui.admin.member

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.databinding.ItemMembersBinding

class AdmMemberAdapter(
    private val onItemClick: (DataItemMember) -> Unit,
) : PagingDataAdapter<DataItemMember, AdmMemberAdapter.MemberViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val binding = ItemMembersBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MemberViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = getItem(position)
        if (member != null) {
            Log.d("members", "MembersAdapter: Menampilkan item: ${member.fullname}")
            holder.bind(member, onItemClick)
        }
        Log.d("members", "MembersAdapter: Item di posisi $position kosong")
    }

    inner class MemberViewHolder(private val binding: ItemMembersBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(member: DataItemMember, onItemClick: (DataItemMember) -> Unit) {
            val context = binding.root.context
            binding.rvFullname.text = member.fullname ?: context.getString(R.string.empty)
            binding.rvGeneration.text = member.angkatan ?: context.getString(R.string.empty)
            binding.rvNoMember.text = member.noMember ?: context.getString(R.string.empty)
            binding.rvPhone.text = member.phoneNumber ?: context.getString(R.string.empty)
            binding.rvType.text = member.memberType ?: context.getString(R.string.empty)

            val studyMember = member.studyMembers?.firstOrNull()
            binding.rvPrody.text = studyMember?.programStudy ?: context.getString(R.string.empty)
            binding.rvUniversity.text = studyMember?.university ?: context.getString(R.string.empty)

            Glide.with(binding.root.context)
                .load(member.profileImgUrl)
                .placeholder(R.drawable.ic_image_broken)
                .error(R.drawable.ic_user)
                .into(binding.rvImage)

            binding.root.setOnClickListener {
                onItemClick(member)
            }

        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemMember>() {
            override fun areItemsTheSame(
                oldItem: DataItemMember,
                newItem: DataItemMember
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: DataItemMember,
                newItem: DataItemMember
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}