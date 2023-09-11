package likelion.project.dongnation.ui.map

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.widget.ZoomControlView
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentMapBinding
import likelion.project.dongnation.ui.main.MainActivity
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.Locale
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
        NaverMapSdk.getInstance(mainActivity).client = NaverMapSdk.NaverCloudPlatformClient("l4mcwgowrf")

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
        }

        return fragmentMapBinding.root
    }

    private fun initMapView() {
        val fm = mainActivity.supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
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
        naverMap.locationSource = locationSource
        naverMap.run {
            uiSettings.run {
                setLogoMargin(75, 20, 20, 50)
                isLocationButtonEnabled = false
                isCompassEnabled = false
                isIndoorLevelPickerEnabled = false
                isZoomControlEnabled = true
            }
            locationTrackingMode = LocationTrackingMode.Follow
        }

        addMarkersFromDB(naverMap, markerList)
        getCurrentLocation()

        fragmentMapBinding.buttonMapSetLocation.setOnClickListener {
            getCurrentLocation()
        }
    }

    fun getMarkerDataFromDB(): List<MarkerData> {
        val markerDataList = mutableListOf<MarkerData>()
        for (i in 1..1) {
            val latitude = getLatLng("서울특별시 은평구 녹번동 278-1").latitude
            val longitude = getLatLng("서울특별시 은평구 녹번동 278-1").longitude
            val captionText = "마커 $i"
            val title = "재능기부 $i"
            val content = "재능내용 $i"
            val name = "$i 길동"

            markerDataList.add(MarkerData(latitude, longitude, captionText, title, content, name))
        }
        return markerDataList
    }

    data class MarkerData(
        val latitude: Double,
        val longitude: Double,
        val captionText: String,
        val title: String,
        val content: String,
        val name: String
    )

    fun addMarkersFromDB(naverMap: NaverMap, markerList: MutableList<Marker>) {
        val markerDataList = getMarkerDataFromDB()

        for (markerData in markerDataList) {
            val marker = Marker()
            marker.run {
                position = LatLng(markerData.latitude, markerData.longitude)
                captionText = markerData.captionText
                onClickListener = Overlay.OnClickListener {
                    val sheetBehavior = BottomSheetBehavior.from(fragmentMapBinding.include1.bottomSheetMap)
                    sheetBehavior.isHideable = false
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

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

    data class AddressLatLng(
        var latitude: Double,
        var longitude: Double
    )

    fun getCurrentLocation() {
        val locationManager = mainActivity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                val cameraUpdate = CameraUpdate.scrollTo(currentLatLng)
                naverMap.moveCamera(cameraUpdate)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        }

        // 위치 업데이트를 요청합니다.
        if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, // 업데이트 주기 (5초)
                10f,  // 업데이트 거리 (10미터)
                locationListener,
                Looper.getMainLooper()
            )
        } else {
            // 위치 권한이 없는 경우 권한을 요청해야 합니다.
            ActivityCompat.requestPermissions(mainActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 5000)
        }
    }
}