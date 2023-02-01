package com.bracketcove.android.dashboards.driver

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.bracketcove.android.databinding.FragmentDriverDashboardBinding

class DriverDashboardFragment : Fragment() {
    private lateinit var viewModel: DriverDashboardViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentDriverDashboardBinding.bind(view)
    }
}