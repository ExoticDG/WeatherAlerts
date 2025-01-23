package com.exoticdg.weatheralerts.ui.ct

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.exoticdg.weatheralerts.databinding.FragmentCtBinding

import com.exoticdg.weatheralerts.ui.ct.CTViewModel
import com.exoticdg.weatheralerts.ui.home.HomeViewModel

class CTFragment : Fragment() {

    private var _binding: FragmentCtBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentCtBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.teststest
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        val textView2: TextView = binding.alertstest
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView2.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

