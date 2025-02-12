package com.imaba.imabajogja.ui.admin.campuse

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.imaba.imabajogja.R

class AdmCampuseFragment : Fragment() {

    companion object {
        fun newInstance() = AdmCampuseFragment()
    }

    private val viewModel: AdmCampuseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_adm_campuse, container, false)
    }
}