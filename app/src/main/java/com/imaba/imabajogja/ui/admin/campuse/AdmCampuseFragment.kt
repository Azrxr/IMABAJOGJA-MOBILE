package com.imaba.imabajogja.ui.admin.campuse

import android.annotation.SuppressLint
import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.imaba.imabajogja.R
import com.imaba.imabajogja.data.utils.Result
import com.imaba.imabajogja.data.utils.showToast
import com.imaba.imabajogja.databinding.FragmentAdmCampuseBinding
import com.imaba.imabajogja.ui.admin.member.AdmMemberDetailActivity
import com.imaba.imabajogja.ui.admin.member.AdmMemberViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class AdmCampuseFragment : Fragment() {

    private lateinit var binding: FragmentAdmCampuseBinding
    private val viewModel: AdmCampuseViewModel by viewModels()
    private val memberViewModel: AdmMemberViewModel by viewModels()
    private lateinit var adapter: AdmStudyAdapter

    companion object {
        fun newInstance() = AdmCampuseFragment()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAdmCampuseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setup()
    }

    private fun setup() {
        setupSearch()
        setupFilterButton()
        setupListStudyMember()
        getMemberSummary()
    }


    @SuppressLint("SetTextI18n")
    private fun getMemberSummary() {
        memberViewModel.getMemberSummary().observe(viewLifecycleOwner) { result ->
            when (result) {
                is com.imaba.imabajogja.data.utils.Result.Success -> {
                    val data = result.data
                    binding.tvTotalMember.text = "Total rencana studi : ${data.totalStudyPlan}"
                    binding.tvStatusPending.text = "Pending : ${data.totalPlanPending}"
                    binding.tvStatusAccepted.text = "Diterima : ${data.totalPlanAccepted}"
                    binding.tvStatusRejected.text = "Ditolak : ${data.totalPlanRejected}"
                    binding.tvStatusActive.text = "Aktif : ${data.totalPlanActive}"
                    binding.tvStatusTotalUnivSelect.text =
                        "Total perguruan tinggi dipilih : ${data.totalUnivPlanSelect}"


                    binding.tvTotalMember.setOnClickListener {
                        clearMemberTypeAndRefresh()
                    }
                    /* TODO: filter study plan belum berfungsi

                                        binding.tvStatusPending.setOnClickListener {
                                            viewModel.clearPlanStatusFilter()
                                            viewModel.setPlanStatusFilter("Pending") // pastikan sesuai nama dari API
                                            adapter.refresh()
                                        }

                                        binding.tvStatusAccepted.setOnClickListener {
                                            viewModel.clearPlanStatusFilter()
                                            viewModel.setPlanStatusFilter("Accepted") // pastikan sesuai nama dari API
                                            adapter.refresh()
                                        }
                                        binding.tvStatusRejected.setOnClickListener {
                                            viewModel.clearPlanStatusFilter()
                                            viewModel.setPlanStatusFilter("Rejected") // pastikan sesuai nama dari API
                                            adapter.refresh()
                                        }
                                        binding.tvStatusActive.setOnClickListener {
                                            viewModel.clearPlanStatusFilter()
                                            viewModel.setPlanStatusFilter("Active")
                                            adapter.refresh()
                                        }
                                        binding.tvStatusTotalUnivSelect.setOnClickListener {
                                            memberViewModel.clearMemberTypeFilters()
                                            memberViewModel.addMemberTypeFilter("Istimewa")
                                            adapter.refresh()
                                        }

                     */

                }

                is com.imaba.imabajogja.data.utils.Result.Error -> requireContext().showToast("Gagal memuat summary: ${result.message}")
                is Result.Loading -> Unit
            }
        }

    }

    private fun clearMemberTypeAndRefresh() {
        memberViewModel.clearMemberTypeFilters()
        adapter.refresh()
        requireContext().showToast("Menampilkan semua tipe member")
    }

    private fun setupListStudyMember() {

        adapter = AdmStudyAdapter(
            onItemClick = { member ->
                val intent = Intent(requireContext(), AdmStudyDetailActivity::class.java)
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
                memberViewModel.members.collectLatest { pagingData ->
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

    private fun setupSearch() {
        binding.searchBar.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Submit tidak diperlukan, kita listen perubahan teks saja
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                memberViewModel.setSearchQuery(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupFilterButton() {
        binding.btnFilter.setOnClickListener { view ->
            val popupMenu = PopupMenu(requireContext(), view)
            popupMenu.menuInflater.inflate(R.menu.member_filter, popupMenu.menu)

            val isFilterActive = memberViewModel.isFilterActive()
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
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val startYear = 2010
        val endYear = currentYear + 2
        val generations = (startYear..endYear).map { it.toString() }.reversed()
        val currentSelections = memberViewModel.getCurrentGenerationFilters()
        val selectedItems = generations.map { currentSelections.contains(it) }.toBooleanArray()


        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Angkatan")
            .setMultiChoiceItems(generations.toTypedArray(), selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    memberViewModel.addGenerationFilter(generations[which])
                } else {
                    memberViewModel.removeGenerationFilter(generations[which])
                }
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Batal", null)
            .setNeutralButton("Reset") { _, _ ->
                memberViewModel.clearGenerationFilters()
            }
            .create()

        dialog.show()
    }

    private fun showMemberTypeFilterDialog() {
        val memberTypes =
            listOf("Semua", "Camaba", "Pengurus", "Anggota", "Demissioner", "Istimewa")
        val currentSelections = memberViewModel.getCurrentMemberTypeFilters()
        val selectedItems = memberTypes.map { currentSelections.contains(it) }.toBooleanArray()

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Pilih Tipe Member")
            .setMultiChoiceItems(memberTypes.toTypedArray(), selectedItems) { _, which, isChecked ->
                if (isChecked) {
                    memberViewModel.addMemberTypeFilter(memberTypes[which])
                } else {
                    memberViewModel.removeMemberTypeFilter(memberTypes[which])
                }
            }
            .setPositiveButton("OK", null)
            .setNegativeButton("Batal", null)
            .setNeutralButton("Reset") { _, _ ->
                memberViewModel.clearMemberTypeFilters()
            }
            .create()

        dialog.show()
    }

    private fun clearFilters() {
        memberViewModel.clearFilters()
        requireContext().showToast("Semua filter telah dihapus")
    }
//    private fun clearMemberTypeAndRefresh() {
//        memberViewModel.clearMemberTypeFilters()
//        adapter.refresh()
//        requireContext().showToast("Menampilkan semua tipe member")
//    }
}