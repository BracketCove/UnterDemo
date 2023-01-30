package com.bracketcove.android.dashboards.driver

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bracketcove.android.R

class DriverDashboardFragment : Fragment() {

    companion object {
        fun newInstance() = DriverDashboardFragment()
    }

    private lateinit var viewModel: DriverDashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DriverDashboardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}