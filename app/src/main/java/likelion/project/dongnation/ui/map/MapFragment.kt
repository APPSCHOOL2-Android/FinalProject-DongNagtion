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
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
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

        addMarkersFromDB(naverMap, markerList)

        fragmentMapBinding.buttonMapSetLocation.setOnClickListener {
            naverMap.cameraPosition = cameraPosition
        }
    }

    fun getMarkerDataFromDB(onSuccess: (List<MarkerData>) -> Unit) {
        val markerDataList = mutableListOf<MarkerData>()
        val db = Firebase.firestore
        val donationCollection = db.collection("Donations")

        donationCollection.get().addOnSuccessListener { result1 ->
            for (document1 in result1) {
                val type = document1["donationType"] as String
                val title = document1["donationTitle"] as String
                val content = document1["donationContent"] as String
                val userId = document1["donationUser"] as String

                db.collection("users").whereEqualTo("userId", userId).get().addOnSuccessListener { result2 ->
                    for (document2 in result2) {
                        val userName = document2["userName"] as String
                        var userAddress = ""
                        if(userAddress == "") {
                            userAddress = "서울특별시 은평구 서오릉로 46"
                        } else {
                            userAddress = document2["userAddress"] as String
                        }
                        markerDataList.add(MarkerData(type, userAddress, userName, title, content))
                    }

                    onSuccess(markerDataList)
                }
            }
        }
    }
    fun addMarkersFromDB(naverMap: NaverMap, markerList: MutableList<Marker>) {
        getMarkerDataFromDB { markerDataList ->
            for (markerData in markerDataList) {
                val marker = Marker()
                val latitude = getLatLng(markerData.address).latitude
                val longitude = getLatLng(markerData.address).longitude
                marker.run {
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

                            imageViewDonateThumbnail1.setImageResource(R.mipmap.ic_launcher_logo)
                            imageViewDonateThumbnail2.setImageResource(R.mipmap.ic_launcher_logo)
                            imageViewDonateThumbnail3.setImageResource(R.mipmap.ic_launcher_logo)

                            buttonMapGotoDetail.setOnClickListener {
                                mainActivity.replaceFragment(MainActivity.DONATE_INFO_FRAGMENT, true, null)
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