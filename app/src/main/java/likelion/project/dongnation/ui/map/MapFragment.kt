package likelion.project.dongnation.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.PathOverlay
import com.naver.maps.map.util.FusedLocationSource
import likelion.project.dongnation.BuildConfig
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentMapBinding
import likelion.project.dongnation.model.AddressLatLng
import likelion.project.dongnation.model.MarkerData
import likelion.project.dongnation.ui.main.MainActivity
import kotlin.concurrent.thread

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource : FusedLocationSource
    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )
    var markerList: MutableList<Marker> = mutableListOf()
    var markerDataList: MutableList<MarkerData> = mutableListOf()
    val imgList = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMapBinding = FragmentMapBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        NaverMapSdk.getInstance(mainActivity).client = NaverMapSdk.NaverCloudPlatformClient("${BuildConfig.NAVER_MAP_CLIENT_ID}")

        if(!hasPermission()) {
            ActivityCompat.requestPermissions(mainActivity, permissionList, 5000)
        } else {
            initMapView()
        }

        fragmentMapBinding.run {
            searchViewMap.run {
                queryHint = "키워드를 입력해주세요."
                isSubmitButtonEnabled = true
                setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                    override fun onQueryTextSubmit(query: String?): Boolean {
                        if (!query.isNullOrBlank()) {
                            searchMarker(query.toString())
                            return true
                        }
                        return false
                    }

                    override fun onQueryTextChange(newText: String?): Boolean {
                        if (newText.isNullOrBlank()) {
                            thread {
                                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                                imm.hideSoftInputFromWindow(view?.windowToken, 0)
                            }
                        }
                        return false
                    }
                })
            }

            chipGroupMap.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId) {
                    R.id.chip_map_all -> showAllMarkers()
                    R.id.chip_map_up -> showMarkers(Color.MAGENTA)
                    R.id.chip_map_down -> showMarkers(Color.BLUE)
                }
            }

            imageViewMapBack.setOnClickListener {
                mainActivity.removeFragment("MapFragment")
            }

        }

        return fragmentMapBinding.root
    }

    private fun searchMarker(query: String) {

        for (markerData in markerDataList) {
            if (markerData.title.contains(query, ignoreCase = true)) {
                val marker = Marker()
                val latitude = getLatLng(markerData.address).latitude
                val longitude = getLatLng(markerData.address).longitude
                marker.run {
                    isHideCollidedSymbols = true
                    iconTintColor = Color.GREEN
                    position = LatLng(latitude, longitude)
                    onClickListener = Overlay.OnClickListener {
                        val sheetBehavior = BottomSheetBehavior.from(fragmentMapBinding.include1.bottomSheetMap)
                        sheetBehavior.isHideable = false
                        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                        naverMap.setOnMapClickListener { _, _ ->
                            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        }

                        fragmentMapBinding.include1.run {
                            textViewDonateTitle.text = markerData.title
                            textViewDonateName.text = markerData.name
                            textViewDonateContext.text = markerData.content

                            if (markerData.img != null) {
                                for (image in markerData.img) {
                                    imgList.add(image)
                                }

                                if (imgList.size >= 1) {
                                    Glide.with(requireContext())
                                        .load(imgList[0]) // 이미지 URL
                                        .placeholder(R.mipmap.ic_launcher_logo)
                                        .error(R.mipmap.ic_launcher_logo)
                                        .into(imageViewDonateThumbnail1)
                                }
                            }

                            buttonMapGotoDetail.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putString("donationIdx", markerData.idx)
                                mainActivity.replaceFragment(MainActivity.DONATE_INFO_FRAGMENT, true, bundle)
                            }
                        }
                        false
                    }
                    map = naverMap
                }
                markerList.add(marker)
            }
        }
    }

    private fun initMapView() {
        val fm = childFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
        mapFragment.getMapAsync {
            val locationButtonView = fragmentMapBinding.mapLocationButton
            locationButtonView.map = naverMap
        }
        locationSource = FusedLocationSource(this,5000)
    }

    private fun hasPermission(): Boolean {
        for (permission in permissionList) {
            if (ContextCompat.checkSelfPermission(mainActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        locationSource = FusedLocationSource(this,5000)
        naverMap.locationSource = locationSource
        val currentLocation = getLatLng("서울시 은평구 녹번동")
        val cameraPosition = CameraPosition(LatLng(currentLocation.latitude, currentLocation.longitude), 14.0)
        naverMap.cameraPosition = cameraPosition
        naverMap.run {
            uiSettings.run {
                setLogoMargin(75, 20, 20, 50)
                isLocationButtonEnabled = false
                isCompassEnabled = false
                isIndoorLevelPickerEnabled = false
                isZoomControlEnabled = true
            }
            locationTrackingMode = LocationTrackingMode.Follow

            setOnMapDoubleTapListener { pointF, latLng ->  true }
            setOnMapTwoFingerTapListener { pointF, latLng -> true }
        }

        fragmentMapBinding.buttonMapSetLocation.setOnClickListener {
            val cameraUpdate = CameraUpdate.zoomTo(13.0)
            naverMap.cameraPosition = cameraPosition
            naverMap.moveCamera(cameraUpdate)
        }

        addMarkersFromDB(naverMap, markerList)
    }

    fun getMarkerDataFromDB(onSuccess: (List<MarkerData>) -> Unit) {
        val db = Firebase.firestore
        val donationCollection = db.collection("Donations")

        donationCollection.get().addOnSuccessListener { result1 ->
            markerDataList.clear()

            for (document1 in result1) {
                val idx = document1["donationIdx"] as String
                val type = document1["donationType"] as String
                val title = document1["donationTitle"] as String
                val content = document1["donationContent"] as String
                val userId = document1["donationUser"] as String
                val img = document1["donationImg"] as MutableList<String>

                db.collection("users").whereEqualTo("userId", userId).get().addOnSuccessListener { result2 ->
                    for (document2 in result2) {
                        val userName = document2["userName"] as String
                        var userAddress = document2["userAddress"] as String
                        if(userAddress == "") {
                            userAddress = "서울특별시 은평구 녹번동 278-1"
                        } else {
                            userAddress = document2["userAddress"] as String
                            Log.d("테스트e", userAddress)
                        }
                        markerDataList.add(MarkerData(idx, type, userAddress, userName, title, content, img))

                        Log.d("테스트d", markerDataList.size.toString())
                        onSuccess(markerDataList)
                    }
                }
            }
        }
    }
    fun addMarkersFromDB(naverMap: NaverMap, markerList: MutableList<Marker>) {
        markerList.clear()

        getMarkerDataFromDB { markerDataList ->
            for (markerData in markerDataList) {
                val marker = Marker()
                val latitude = getLatLng(markerData.address).latitude
                val longitude = getLatLng(markerData.address).longitude
                marker.run {
                    isHideCollidedSymbols = true
                    if(markerData.type == "도와주세요") {
                        iconTintColor = Color.MAGENTA
                    } else if(markerData.type == "도와드릴게요") {
                        iconTintColor = Color.BLUE
                    }
                    position = LatLng(latitude, longitude)
                    onClickListener = Overlay.OnClickListener {
                        val sheetBehavior = BottomSheetBehavior.from(fragmentMapBinding.include1.bottomSheetMap)
                        sheetBehavior.isHideable = false
                        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

                        naverMap.setOnMapClickListener { _, _ ->
                            if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                                sheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                            }
                        }

                        fragmentMapBinding.include1.run {
                            textViewDonateTitle.text = markerData.title
                            textViewDonateName.text = markerData.name
                            textViewDonateContext.text = markerData.content

                            if (markerData.img != null) {
                                for (image in markerData.img) {
                                    imgList.add(image)
                                }

                                if (imgList.size >= 1) {
                                    Glide.with(requireContext())
                                        .load(imgList[0]) // 이미지 URL
                                        .placeholder(R.mipmap.ic_launcher_logo)
                                        .error(R.mipmap.ic_launcher_logo)
                                        .into(imageViewDonateThumbnail1)
                                }
                            }

                            buttonMapGotoDetail.setOnClickListener {
                                val bundle = Bundle()
                                bundle.putString("donationIdx", markerData.idx)
                                mainActivity.replaceFragment(MainActivity.DONATE_INFO_FRAGMENT, true, bundle)
                            }
                        }
                        false
                    }
                    map = naverMap
                }
                markerList.add(marker)
                Log.d("테스트", markerList.size.toString())
            }
        }
    }

    fun getLatLng(address: String): AddressLatLng{
        val geocoder = Geocoder(mainActivity)
        val addresses: MutableList<Address>? = geocoder.getFromLocationName(address, 1)
        if (addresses != null) {
            val address = addresses[0]
            val latitude = address.latitude
            val longitude = address.longitude
            return AddressLatLng(latitude, longitude)
        }
        return AddressLatLng(0.0, 0.0)
    }

    fun showMarkers(color: Int) {
        for(marker in markerList) {
            marker.isVisible = marker.iconTintColor == color
        }
    }

    fun showAllMarkers() {
        for(marker in markerList) {
            marker.isVisible = true
        }
    }

}