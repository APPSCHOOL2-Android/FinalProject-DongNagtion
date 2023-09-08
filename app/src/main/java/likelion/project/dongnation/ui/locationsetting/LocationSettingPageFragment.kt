package likelion.project.dongnation.ui.locationsetting

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.UiThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.MarkerIcons
import com.qure.core_design.custom.recyclerview.RecyclerViewItemDecoration
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentLocationSettingPageBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val ARG_PARAM3 = "param3"
private const val ARG_PARAM4 = "param4"

class LocationSettingPageFragment(listener: RegionPositionCallback?, arealistener: AreaNameCallback?) :
    Fragment(), OnMapReadyCallback {

    private lateinit var viewModel: LocationSettingViewModel
    private lateinit var binding: FragmentLocationSettingPageBinding

    private lateinit var naverMap: NaverMap

    private var title: String? = null
    private var subTitle: String? = null
    private var regionName: String? = null
    private var regionArray: Array<String> = emptyArray()

    private var listener: RegionPositionCallback? = null
    private var arealistener: AreaNameCallback? = null

    private lateinit var adapter: LocationRegionAdapter

    constructor() : this(
        listener = null,
        arealistener = null
    )

    init {
        this.listener = listener
        this.arealistener = arealistener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentLocationSettingPageBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[LocationSettingViewModel::class.java]
        arguments?.let {
            title = it.getString(ARG_PARAM1)
            subTitle = it.getString(ARG_PARAM2)
            regionName = it.getString(ARG_PARAM3)
            regionArray = it.getStringArray(ARG_PARAM4) as Array<String>
        }
        regionName = if (isNotExistCityName()) regionName?.split(" ")?.get(0)
            ?: "" else regionName
        Log.d("test1", "${regionName}")
        openMapFragment()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        initView()
    }

    private fun isNotExistCityName() = regionName?.contains(NOT_EXIST_CITY_NAME) ?: false

    private fun initAdapter() {
        adapter = LocationRegionAdapter(regionArray)
        adapter.setItemClickListener(object : LocationRegionAdapter.ItemClickListener {
            override fun onClick(position: Int) {
                listener?.setRegionPosition(position)
            }
        })
    }

    private fun initView() {
        binding.apply {
            textViewLocationSettingPageTitle.text = title
            textViewLocationSettingPageSubtitle.text = subTitle
            textViewFragmentLocationSettingSelectedName.text = regionName
        }

        if (isMapPage()) {
            binding.recyclerViewLocationSettingPage.visibility = View.GONE
            binding.fragmentFragmentLocationSettingMap.visibility = View.VISIBLE
        } else {
            initRecyclerView()
            binding.recyclerViewLocationSettingPage.visibility = View.VISIBLE
            binding.fragmentFragmentLocationSettingMap.visibility = View.GONE
        }
    }

    private fun isMapPage() = subTitle == getString(R.string.location_setting_map)

    private fun openMapFragment() {
        val fm = childFragmentManager
        val mapFragment =
            fm.findFragmentById(R.id.fragment_fragmentLocationSetting_map) as MapFragment?
                ?: MapFragment.newInstance().also {
                    fm.beginTransaction()
                        .add(R.id.fragment_fragmentLocationSetting_map, it)
                        .commit()
                }
        mapFragment.getMapAsync(this)
    }

    private fun initRecyclerView() {
        binding.apply {
            recyclerViewLocationSettingPage.adapter = adapter
            recyclerViewLocationSettingPage.addItemDecoration(RecyclerViewItemDecoration(10))
        }
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        observe()
        initMapEvent()
    }

    private fun initMapEvent() {
        val marker = Marker()
        naverMap.setOnMapClickListener { point, coord ->
            marker.apply {
                position = LatLng(coord.latitude, coord.longitude)
                map = naverMap
                icon = MarkerIcons.BLACK
                iconTintColor =
                    requireContext().resources.getColor(R.color.green200)
                viewModel.getReverseGeocoding("${coord.longitude},${coord.latitude}")
            }
        }
    }

    private fun observe() {
        viewModel.getGeocoding(regionName ?: "")
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect {
                        if (it.isGeocodingInitialized && !it.isReverseGeocodingInittialized) {
                            moveMapCamera(it)
                        } else if (it.isReverseGeocodingInittialized) {
                            if (it.reverseGeocodingUI?.code == 0 && it.geocodingUI?.coords != "") {
                                arealistener?.setAreaName(
                                    it.reverseGeocodingUI.areaName,
                                    it.geocodingUI!!.coords,
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun moveMapCamera(uiState: UiState) {
        val latitude = uiState.geocodingUI?.y ?: "37.5666102"
        val longitude = uiState.geocodingUI?.x ?: "126.9783881"
        val coords = "${longitude},${latitude}"
        viewModel.getReverseGeocoding(coords)
        arealistener?.setAreaName(regionName ?: "", "${longitude},${latitude}")
        val cameraUpdate = CameraUpdate.scrollTo(
            LatLng(
                latitude.toDouble(),
                longitude.toDouble()
            )
        )
        naverMap.moveCamera(cameraUpdate)
    }

    companion object {
        private const val NOT_EXIST_CITY_NAME = "없음"
        fun newInstance(
            title: String,
            subTitle: String,
            regionArray: Array<String>,
            regionName: String,
            listener: RegionPositionCallback,
            arealistener: AreaNameCallback,
        ) =
            LocationSettingPageFragment(listener, arealistener).apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, title)
                    putString(ARG_PARAM2, subTitle)
                    putString(ARG_PARAM3, regionName)
                    putStringArray(ARG_PARAM4, regionArray)
                }
            }
    }
}

interface RegionPositionCallback {
    fun setRegionPosition(postion: Int)
}

interface AreaNameCallback {
    fun setAreaName(name: String, coords: String)
}