package com.imaba.imabajogja.ui.admin.member

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.FragmentAdmMemberBinding
import com.imaba.imabajogja.data.utils.Result
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AdmMemberFragment : Fragment() {

    companion object {
        fun newInstance() = AdmMemberFragment()
    }

    private val viewModel: AdmMemberViewModel by viewModels()
    private lateinit var adapter: AdmMemberAdapter
    private lateinit var binding: FragmentAdmMemberBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdmMemberBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getMemberSummary()
        setupRecyclerView()
        setupSearch()
        setupFilterButton()
    }

    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Submit tidak diperlukan, kita listen perubahan teks saja
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.member_filter, popupMenu.menu)

            val isFilterActive = viewModel.isFilterActive()
            binding.btnFilter.setImageResource(if (isFilterActive) R.drawable.ic_filter_on else R.drawable.ic_filter_off)
            popupMenu.menu.findItem(R.id.filter_clear).isVisible = isFilterActive

            popupMenu.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.angkatan -> showGenerationFilterDialog()
                    R.id.memberType -> showMemberTypeFilterDialog()
                    R.id.filter_clear -> clearFilters()
                }
                true
            }
            popupMenu.show()
        }
    }

    private fun showGenerationFilterDialog() {
        val generations = (2015..2025).map { it.toString() }
        val currentSelections = viewModel.getCurrentGenerationFilters()
        val selectedItems = generations.map { currentSelections.contains(it) }.toBooleanArray()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Angkatan")
            .setMultiChoiceItems(generations.toTypedArray(), selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    viewModel.addGenerationFilter(generations[which])
                } else {
                    viewModel.removeGenerationFilter(generations[which])
                }
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Batal", null)
            .setNeutralButton("Reset") { _, _ ->
                viewModel.clearGenerationFilters()
            }
            .create()

        dialog.show()
    }

    private fun showMemberTypeFilterDialog() {
        val memberTypes =
            listOf("Semua", "Camaba", "Pengurus", "Anggota", "Demissioner", "Istimewa")
        val currentSelections = viewModel.getCurrentMemberTypeFilters()
        val selectedItems = memberTypes.map { currentSelections.contains(it) }.toBooleanArray()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Tipe Member")
            .setMultiChoiceItems(memberTypes.toTypedArray(), selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    viewModel.addMemberTypeFilter(memberTypes[which])
                } else {
                    viewModel.removeMemberTypeFilter(memberTypes[which])
                }
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Batal", null)
            .setNeutralButton("Reset") { _, _ ->
                viewModel.clearMemberTypeFilters()
            }
            .create()

        dialog.show()
    }

    private fun clearFilters() {
        viewModel.clearFilters()
        requireContext().showToast("Semua filter telah dihapus")
    }

    private fun setupRecyclerView() {

        adapter = AdmMemberAdapter(
            onItemClick = { member ->
                val intent = Intent(requireContext(), AdmMemberDetailActivity::class.java)
                intent.putExtra(AdmMemberDetailActivity.EXTRA_MEMBER, member)
                startActivity(intent)
            },
        )

        binding.rvMemberList.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMemberList.adapter = adapter

        adapter.addLoadStateListener { loadState ->
            binding.progressBar.visibility =
                if (loadState.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

        }
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.members.collectLatest { pagingData ->
                    Log.d("Members", "fragment: Menerima data: $pagingData")
                    adapter.submitData(pagingData)

                    binding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }
        // Cek apakah Adapter sedang loading, kosong, atau error
        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow.collectLatest { loadStates ->
                binding.progressBar.visibility =
                    if (loadStates.source.refresh is LoadState.Loading) View.VISIBLE else View.GONE

                if (loadStates.source.refresh is LoadState.NotLoading) {
                    binding.swipeRefreshLayout.isRefreshing = false
                }

                binding.tvEmpty.visibility =
                    if (loadStates.source.refresh is LoadState.NotLoading && adapter.itemCount == 0)
                        View.VISIBLE
                    else View.GONE
            }
        }
        binding.swipeRefreshLayout.setOnRefreshListener {
            adapter.refresh()
        }

    }

    private fun clearMemberTypeAndRefresh() {
        viewModel.clearMemberTypeFilters()
        adapter.refresh()
        requireContext().showToast("Menampilkan semua tipe member")
    }

    private fun getMemberSummary() {
        viewModel.getMemberSummary().observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    val data = result.data
                    binding.tvTotalMember.text = "Total anggota : ${data.totalMember}"
                    binding.tvTotalCamaba.text = "Camaba : ${data.totalMemberProspective}"
                    binding.tvTotalActive.text = "Aktif : ${data.totalMemberRegular}"
                    binding.tvTotalDemissioner.text = "Demissioner : ${data.totalMemberDemissioner}"
                    binding.tvTotalPengurus.text = "Pengurus : ${data.totalMemberManagement}"
                    binding.tvTotalIstimewa.text = "Istimewa : ${data.totalMemberSpecial}"

                    // klik untuk filter
                    binding.tvTotalMember.setOnClickListener {
                        clearMemberTypeAndRefresh()
                    }

                    binding.tvTotalActive.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Anggota") // pastikan sesuai nama dari API
                        adapter.refresh()
                    }

                    binding.tvTotalDemissioner.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Demissioner")
                        adapter.refresh()
                    }
                    binding.tvTotalCamaba.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Camaba")
                        adapter.refresh()
                    }
                    binding.tvTotalPengurus.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Pengurus")
                        adapter.refresh()
                    }
                    binding.tvTotalIstimewa.setOnClickListener {
                        viewModel.clearMemberTypeFilters()
                        viewModel.addMemberTypeFilter("Istimewa")
                        adapter.refresh()
                    }

                }

                is Result.Error -> requireContext().showToast("Gagal memuat summary: ${result.message}")
                is Result.Loading -> Unit
            }
        }

    }
}