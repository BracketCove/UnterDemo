package com.bracketcove.android.dashboards.passenger

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bracketcove.android.R

class PassengerDashboardFragment : Fragment() {

    companion object {
        fun newInstance() = PassengerDashboardFragment()
    }

    private lateinit var viewModel: PassengerDashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_passenger_dashboard, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PassengerDashboardViewModel::class.java)
        // TODO: Use the ViewModel
    }

}