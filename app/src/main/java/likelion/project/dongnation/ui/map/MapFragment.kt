package likelion.project.dongnation.ui.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import likelion.project.dongnation.BuildConfig
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentMapBinding
import likelion.project.dongnation.model.AddressLatLng
import likelion.project.dongnation.model.MarkerData
import likelion.project.dongnation.ui.login.LoginViewModel
import likelion.project.dongnation.ui.main.MainActivity

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var fragmentMapBinding: FragmentMapBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var naverMap: NaverMap
    private lateinit var locationSource : FusedLocationSource

    private var markerList: MutableList<Marker> = mutableListOf()
    private var markerDataList: MutableList<MarkerData> = mutableListOf()


    var userId = LoginViewModel.loginUserInfo.userId
    var userAddress = "서울특별시 은평구 불광동"
    val db = Firebase.firestore
    private val imgList = ArrayList<String>()
    private val vworldAddress = "https://api.vworld.kr/req/data?service=data&request=GetFeature&data=LT_C_ADEMD_INFO&key=B4CFB7B1-1523-3C86-9CE2-4EAAE0ADC98A&attrFilter=emd_kor_nm:=:녹번동&단일검색=Y"
    private val permissionList = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

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
            loadMarkers()
        }

        fragmentMapBinding.run {
            chipGroupMap.setOnCheckedChangeListener { group, checkedId ->
                when(checkedId) {
                    R.id.chip_map_all -> showAllMarkers()
                    R.id.chip_map_up -> showMarkers(Color.MAGENTA)
                    R.id.chip_map_down -> showMarkers(Color.BLUE)
                }
            }

            imageViewMapBack.setOnClickListener {
                mainActivity.removeFragment("MapFragment")
                mainActivity.activityMainBinding.bottomNavigation.visibility = View.VISIBLE
            }

        }

        return fragmentMapBinding.root
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

        db.collection("users").whereEqualTo("userId", userId).get().addOnSuccessListener { result ->
            for(document in result) {
                val dbAddress = document["userAddress"] as String
                userAddress = dbAddress
            }

        locationSource = FusedLocationSource(this,5000)
        naverMap.locationSource = locationSource
        val currentLocation = getLatLng(userAddress)
        val cameraPosition = CameraPosition(LatLng(currentLocation.latitude, currentLocation.longitude), 14.0)
        naverMap.cameraPosition = cameraPosition
        naverMap.run {
            uiSettings.run {
                setLogoMargin(40, 20, 20, 40)
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
            val dongAddress = extractLocation(userAddress)
            Log.d("테스트", dongAddress)
            val cameraUpdate = CameraUpdate.zoomTo(13.0)
            val newLocation = getLatLng(dongAddress.split("동").firstOrNull().plus("동"))
            val newCameraPosition = CameraPosition(LatLng(newLocation.latitude, newLocation.longitude), 14.0)
            naverMap.cameraPosition = newCameraPosition
            naverMap.moveCamera(cameraUpdate)
        }

        loadMarkers()
        }
    }

    private suspend fun getMarkerDataFromDBAsync(): List<MarkerData> {
        return withContext(Dispatchers.IO) {
            val donationCollection = db.collection("Donations")
            val markerDataList = mutableListOf<MarkerData>()

            val result1 = donationCollection.get().await() // 비동기로 데이터 가져오기

            for (document1 in result1) {
                val idx = document1["donationIdx"] as String
                val type = document1["donationType"] as String
                val title = document1["donationTitle"] as String
                val content = document1["donationContent"] as String
                val userId = document1["donationUser"] as String
                val img = document1["donationImg"] as MutableList<String>

                val result2 = db.collection("users").whereEqualTo("userId", userId).get().await()

                for (document2 in result2) {
                    val userName = document2["userName"] as String
                    var userAddress = document2["userAddress"] as String
                    markerDataList.add(MarkerData(idx, type, userAddress, userName, title, content, img))
                }
            }

            markerDataList
        }
    }

    private fun loadMarkers() {
        mainActivity.lifecycleScope.launch {
            val markerDataList = getMarkerDataFromDBAsync()
            addMarkersToMap(markerDataList)
        }
    }

    private fun addMarkersToMap(markerDataList: List<MarkerData>) {
        db.collection("users").whereEqualTo("userId", userId).get().addOnSuccessListener { result ->
            for (document in result) {
                val dbAddress = document["userAddress"] as String
                userAddress = dbAddress
            }
            for (markerData in markerDataList) {
                val marker = Marker()
                val latitude = getLatLng(markerData.address).latitude
                val longitude = getLatLng(markerData.address).longitude
                val address = markerData.address
                val city = address.split(" ")[1]
                val userCity = userAddress.split(" ")[1]
                Log.d("userCity", city)
                Log.d("userCity", userCity)
                if(city == userCity) {
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
                                    imgList.clear()

                                    for (image in markerData.img) {
                                        imgList.add(image)
                                    }

                                    val imageViewMap = mapOf(
                                        imageViewDonateThumbnail1 to imgList.getOrNull(0),
                                        imageViewDonateThumbnail2 to imgList.getOrNull(1),
                                        imageViewDonateThumbnail3 to imgList.getOrNull(2)
                                    )

                                    for ((imageView, imageUrl) in imageViewMap) {
                                        if (!imageUrl.isNullOrBlank()) {
                                            Glide.with(requireContext())
                                                .load(imageUrl)
                                                .placeholder(R.mipmap.ic_launcher_logo)
                                                .error(R.mipmap.ic_launcher_logo)
                                                .into(imageView)
                                        }
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
                }


                markerList.add(marker)
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

    fun extractLocation(address: String): String {
        val addressParts = address.split(" ")
        val dongIndex = addressParts.indexOfFirst { it.endsWith("동") || it.endsWith("읍") || it.endsWith("면") }
        val dongOrEupOrMyeon = if (dongIndex >= 0) addressParts.subList(2, dongIndex + 1).joinToString(" ") else ""
        return "${addressParts[0]} ${addressParts[1]} $dongOrEupOrMyeon".trim()
    }
}