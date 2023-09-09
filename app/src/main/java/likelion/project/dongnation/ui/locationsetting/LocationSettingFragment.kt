package com.qure.create.location

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentLocationSettingBinding
import likelion.project.dongnation.model.User
import likelion.project.dongnation.ui.locationsetting.AreaNameCallback
import likelion.project.dongnation.ui.locationsetting.LocationSettingPageFragment
import likelion.project.dongnation.ui.locationsetting.LocationSettingPagerAdapter
import likelion.project.dongnation.ui.locationsetting.LocationSettingViewModel
import likelion.project.dongnation.ui.locationsetting.Region
import likelion.project.dongnation.ui.locationsetting.RegionPositionCallback
import likelion.project.dongnation.ui.main.MainActivity

class LocationSettingFragment : Fragment(), RegionPositionCallback, AreaNameCallback {

    private lateinit var binding: FragmentLocationSettingBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: LocationSettingViewModel
    lateinit var listener: RegionPositionCallback
    lateinit var arealistener: AreaNameCallback

    private var areaName = ""
    private var coords = ""
    private var currentItemPosition = 0
    private lateinit var adapter: LocationSettingPagerAdapter
    private var selectedRegionName = MutableList(2, { "" })
    private var selectedRegionId = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        listener = this
        arealistener = this
        binding = FragmentLocationSettingBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel =
            ViewModelProvider(this@LocationSettingFragment)[LocationSettingViewModel::class.java]
        adapter = LocationSettingPagerAdapter(this@LocationSettingFragment, getFragments())
        initViewPager()
        initEvent()
        observe()
        return binding.root
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.uiState.collectLatest {
                if (it.showMessage.isNotEmpty()) {
                    Snackbar.make(requireView(), it.showMessage, Snackbar.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun initViewPager() {
        binding.apply {
            viewPagerLocationSetting.adapter = adapter
            viewPagerLocationSetting.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            viewPagerLocationSetting.isUserInputEnabled = false

            TabLayoutMediator(
                tabLayoutLocationSettingDot,
                viewPagerLocationSetting
            ) { tab, position -> }.attach()
        }
    }

    private fun getFragments(): MutableList<Fragment> {
        return mutableListOf(
            LocationSettingPageFragment.newInstance(
                title = getString(R.string.location_setting_selection_do),
                subTitle = getString(R.string.location_setting_do),
                regionArray = Region.getArray(this.requireContext()),
                regionName = "",
                listener = listener,
                arealistener = arealistener,
            ),
            LocationSettingPageFragment.newInstance(
                title = getString(R.string.location_setting_selection_city),
                subTitle = getString(R.string.location_setting_city),
                regionArray = Region.getArray(this.requireContext()),
                regionName = "",
                listener = listener,
                arealistener = arealistener,
            ),
            LocationSettingPageFragment.newInstance(
                title = getString(R.string.location_setting_selection_map),
                subTitle = getString(R.string.location_setting_map),
                regionArray = emptyArray(),
                regionName = "",
                listener = listener,
                arealistener = arealistener,
            )
        )
    }

    private fun initEvent() {
        setViewPagePosition()
        setButtonoPageTransition()
    }

    private fun setViewPagePosition() {
        binding.apply {
            buttonLocationSettingNext.setOnClickListener {
                if (currentItemPosition == 2) {
                    viewModel.updateAddress(User(userId = "2eqn9AfBVl9oXROMY2Wx", userAddress = areaName.filterNot { it.isDigit() }))
                    mainActivity.replaceFragment(MainActivity.HOME_FRAGMENT, false, null)
                }
                viewPagerLocationSetting.run {
                    currentItem += PAGE_INCREMENT_VALUE
                    currentItemPosition = currentItem
                }
            }
            buttonLocationSettingPrevious.setOnClickListener {
                viewPagerLocationSetting.run {
                    currentItem -= PAGE_INCREMENT_VALUE
                    currentItemPosition = currentItem
                }
            }
        }
    }

    private fun setButtonoPageTransition() {
        binding.apply {
            viewPagerLocationSetting.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    when (position) {
                        0 -> setPreViousAndNextButton(position)
                        1 -> setPreViousAndNextButton(position)
                        else -> setPreViousAndNextButton(position)
                    }
                }
            })
        }
    }

    private fun setPreViousAndNextButton(position: Int) {
        with(binding) {
            buttonLocationSettingPrevious.visibility =
                if (position == 0) View.INVISIBLE else View.VISIBLE
            buttonLocationSettingNext.text =
                if (position == 2) getString(R.string.location_setting_setting) else
                    getString(R.string.location_setting_next)
            setEnabledNetxtButton(position)
        }
    }

    private fun setEnabledNetxtButton(position: Int) {
        when {
            selectedRegionName[0] != "" && position == 0 -> {
                binding.buttonLocationSettingNext.isEnabled = true
            }

            selectedRegionName[1] == "" && position == 1 -> {
                binding.buttonLocationSettingNext.isEnabled = false
            }

            position == 2 -> {
                binding.buttonLocationSettingNext.isEnabled = true
            }
        }
    }


    override fun setRegionPosition(postion: Int) {
        binding.buttonLocationSettingNext.isEnabled = true
        setSelectedRegionName(postion)
    }

    private fun setSelectedRegionName(postion: Int) {
        val regionArray = Region.getArray(this.requireContext(), postion)
        when (currentItemPosition) {
            0 -> {
                selectedRegionId = postion
                selectedRegionName[0] = resources.getStringArray(R.array.array_region)[postion]
                selectedRegionName[1] = ""
                refreshAdapter(regionArray)
            }

            1 -> {
                selectedRegionName[1] =
                    Region.getArray(this.requireContext(), selectedRegionId)[postion]
                refreshAdapter(regionArray)
            }
        }
    }

    private fun refreshAdapter(regionArray: Array<String>) {
        when (currentItemPosition) {
            0 -> setRefreshAdapter(
                regionArray,
                R.string.location_setting_selection_city,
                R.string.location_setting_city
            )

            else -> setRefreshAdapter(
                emptyArray(),
                R.string.location_setting_selection_map,
                R.string.location_setting_map
            )
        }
    }

    private fun setRefreshAdapter(regionArray: Array<String>, title: Int, subTitle: Int) {
        adapter.refreshFragment(
            currentItemPosition + 1,
            LocationSettingPageFragment.newInstance(
                title = getString(title),
                subTitle = getString(subTitle),
                regionArray = regionArray,
                regionName = selectedRegionName.joinToString(" "),
                listener = listener,
                arealistener = arealistener,
            )
        )
    }

    override fun setAreaName(name: String, coords: String) {
        this.areaName = name
        this.coords = coords
    }

    companion object {
        private const val PAGE_INCREMENT_VALUE = 1
    }

}