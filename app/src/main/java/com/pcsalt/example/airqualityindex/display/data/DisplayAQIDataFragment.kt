package com.pcsalt.example.airqualityindex.display.data

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.pcsalt.example.airqualityindex.R
import com.pcsalt.example.airqualityindex.databinding.FragmentDisplayAqiDataBinding
import com.pcsalt.example.airqualityindex.db.entity.AQIData
import com.pcsalt.example.airqualityindex.display.AQIDataViewModel
import com.pcsalt.example.airqualityindex.display.details.AQIDetailsFragment
import com.pcsalt.example.airqualityindex.ext.isMoreThan30Sec

class DisplayAQIDataFragment : Fragment() {
    private lateinit var binding: FragmentDisplayAqiDataBinding
    private lateinit var viewModel: AQIDataViewModel
    private var aqiAdapter: AQIDataAdapter? = null
    private var lastReceived: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDisplayAqiDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this).get(AQIDataViewModel::class.java)
        aqiAdapter = AQIDataAdapter()
        aqiAdapter?.listener = object : AQIDataAdapter.OnItemClickListener {
            override fun onClick(item: AQIData) {
                activity?.let {
                    val aqiDetailsFragment = AQIDetailsFragment()
                    val bundle = Bundle()
                    bundle.putString(AQIDetailsFragment.EXTRA_CITY_NAME, item.cityName)
                    aqiDetailsFragment.arguments = bundle

                    it.supportFragmentManager
                        .beginTransaction()
                        .add(R.id.container, aqiDetailsFragment)
                        .addToBackStack(AQIDetailsFragment::class.java.simpleName)
                        .commit()
                }
            }
        }
        binding.rvData.apply {
            layoutManager = LinearLayoutManager(context)

            adapter = aqiAdapter
        }

        viewModel.getLatestData().observe(viewLifecycleOwner, {
            val now = System.currentTimeMillis()
            if (now.isMoreThan30Sec(lastReceived)) {
                lastReceived = now
                aqiAdapter?.setData(it)
            }
        })
    }

    override fun onDestroyView() {
        aqiAdapter?.clear()
        aqiAdapter = null
        lastReceived = 0
        super.onDestroyView()
    }
}