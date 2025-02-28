package com.imaba.imabajogja.ui.member

import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.databinding.FragmentMemberBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MemberFragment : Fragment() {

    companion object {
        fun newInstance() = MemberFragment()
    }
    private lateinit var binding: FragmentMemberBinding
    private val viewModel: MemberViewModel by viewModels()
    private lateinit var adapter: MembersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
    }

    private fun setupRecyclerView() {

        adapter = MembersAdapter { member ->
            Toast.makeText(requireContext(), "Klik: ${member.fullname}", Toast.LENGTH_SHORT).show()
        }

        binding.rvMemberList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMemberList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
            if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {
                viewModel.members.collectLatest { pagingData ->
                    Log.d("Members", "fragment: Menerima data: $pagingData")
                    adapter.submitData(pagingData)
                }
            }
        }
        // Cek apakah Adapter sedang loading, kosong, atau error
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.visibility =
                    if (loadStates.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

                binding.swipeRefreshLayout.setOnRefreshListener {
                    adapter.refresh()
                }

                if (loadStates.source.refresh is LoadState.NotLoading && adapter.itemCount == 0) {
                    binding.tvEmpty.visibility = View.VISIBLE
                } else {
                    binding.tvEmpty.visibility = View.GONE
                }
            }
        }
    }
}