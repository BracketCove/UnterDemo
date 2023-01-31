package com.bracketcove.android.profile.driver

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bracketcove.android.R

class DriverSettingsFragment : Fragment() {

    companion object {
        fun newInstance() = DriverSettingsFragment()
    }

    private lateinit var viewModel: DriverSettingsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_driver, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(DriverSettingsViewModel::class.java)
        // TODO: Use the ViewModel
    }

}