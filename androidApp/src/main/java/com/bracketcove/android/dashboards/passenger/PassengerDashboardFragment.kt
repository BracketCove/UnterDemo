package com.bracketcove.android.dashboards.passenger

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bracketcove.android.R
import com.bracketcove.android.databinding.FragmentPassengerDashboardBinding
import com.zhuinden.simplestackextensions.fragments.KeyedFragment
import com.zhuinden.simplestackextensions.fragmentsktx.backstack
import com.zhuinden.simplestackextensions.fragmentsktx.lookup

class PassengerDashboardFragment : Fragment(R.layout.fragment_passenger_dashboard) {

    private val viewModel by lazy { lookup<PassengerDashboardViewModel>() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentPassengerDashboardBinding.bind(view)


    }
}