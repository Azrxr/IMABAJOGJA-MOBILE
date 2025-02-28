package com.imaba.imabajogja.ui.member

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.response.DataItemMember
import com.imaba.imabajogja.databinding.ItemMembersBinding

class MembersAdapter(
    private val onItemClick: (DataItemMember) -> Unit
) : PagingDataAdapter<DataItemMember, MembersAdapter.MemberViewHolder>(DIFF_CALLBACK) {

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

        fun bind(member: DataItemMember, onItemClick : (DataItemMember) -> Unit) {
            binding.rvFullname.text = member.fullname
            binding.rvGeneration.text = member.angkatan.toString()
            binding.rvNoMember.text = member.noMember
            binding.rvPhone.text = member.phoneNumber.toString()
            binding.rvUniversity.text = member.schollOrigin
            binding.rvPrody.text = member.memberType
            binding.rvType.text = member.memberType

            Glide.with(binding.root.context)
            .load(member.profileImgUrl)
                .placeholder(R.drawable.ic_user)
                .error(R.drawable.bg_circle)
                .into(binding.rvImage)

            binding.root.setOnClickListener {
                onItemClick(member)
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<DataItemMember>() {
            override fun areItemsTheSame(oldItem: DataItemMember, newItem: DataItemMember): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: DataItemMember, newItem: DataItemMember): Boolean {
                return oldItem == newItem
            }
        }
    }
}