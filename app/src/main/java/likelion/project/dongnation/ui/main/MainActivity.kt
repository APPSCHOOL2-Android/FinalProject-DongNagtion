package likelion.project.dongnation.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.transition.MaterialSharedAxis
import com.qure.create.location.LocationSettingFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ActivityMainBinding
import likelion.project.dongnation.ui.board.BoardContentsFragment
import likelion.project.dongnation.ui.donate.DonateInfoFragment
import likelion.project.dongnation.ui.board.BoardMainFragment
import likelion.project.dongnation.ui.board.BoardModifyFragment
import likelion.project.dongnation.ui.board.BoardWriteFragment
import likelion.project.dongnation.ui.donate.DonateWriteFragment
import likelion.project.dongnation.ui.gallery.GalleryFragment
import likelion.project.dongnation.ui.chatting.ChattingFragment
import likelion.project.dongnation.ui.donate.DonateModifyFragment
import likelion.project.dongnation.ui.home.HomeFragment
import likelion.project.dongnation.ui.login.LoginFragment
import likelion.project.dongnation.ui.map.MapFragment
import likelion.project.dongnation.ui.onboarding.OnboardingFragment
import likelion.project.dongnation.ui.permission.PermissionFragment
import likelion.project.dongnation.ui.review.ReviewShowFragment
import likelion.project.dongnation.ui.review.ReviewWriteFragment
import likelion.project.dongnation.ui.transfer.TransferFragment
import likelion.project.dongnation.ui.userInfo.UserInfoBoardFragment
import likelion.project.dongnation.ui.userInfo.UserInfoFollowingFragment
import likelion.project.dongnation.ui.userInfo.UserInfoFragment
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    lateinit var activityMainBinding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel


    val permissionListVersion33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    )

    val permissionList = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
    )

    private var isFirstVisitor = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this, MainViewModelFactory(this))[MainViewModel::class.java]

        setContentView(activityMainBinding.root)
//        replaceFragment(HOME_FRAGMENT, false, null)
        observe()
        navigateToPermissionOrOnboardingOrLogin()
        bottomNavigationBar()
    }

    fun replaceFragment(name:String, addToBackStack:Boolean, bundle:Bundle?){
        // Fragment 교체 상태로 설정
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        var newFragment: Fragment? = null
        var oldFragment: Fragment? = null

        if(newFragment != null){
            oldFragment = newFragment
        }

        // 새로운 Fragment를 담을 변수
        newFragment = when(name){
            LOGIN_FRAGMENT -> LoginFragment()
            USER_INFO_FRAGMENT -> UserInfoFragment()
            HOME_FRAGMENT -> HomeFragment()
            MAP_FRAGMENT -> MapFragment()
            DONATE_INFO_FRAGMENT -> DonateInfoFragment()
            ONBOARDING_FRAGMENT -> OnboardingFragment()
            PERMISSION_FRAGMENT -> PermissionFragment()
            BOARD_MAIN_FRAGMENT -> BoardMainFragment()
            BOARD_WRITE_FRAGMENT -> BoardWriteFragment()
            LOCATION_SETTING_FRAGMENT -> LocationSettingFragment()
            REVIEW_SHOW_FRAGMENT -> ReviewShowFragment()
            REVIEW_WRITE_FRAGMENT -> ReviewWriteFragment()
            BOARD_CONTENTS_FRAGMENT -> BoardContentsFragment()
            GALLERY_FRAGMENT -> GalleryFragment()
            BOARD_MODIFY_FRAGMENT -> BoardModifyFragment()
            DONATE_WRITE_FRAGMENT -> DonateWriteFragment()
            DONATE_MODIFY_FRAGMENT -> DonateModifyFragment()
            CHATTING_FRAGMENT -> ChattingFragment()
            USER_INFO_BOARD_FRAGMENT -> UserInfoBoardFragment()
            USER_INFO_FOLLOWING_FRAGMENT -> UserInfoFollowingFragment()
            TRANSFER_FRAGMENT -> TransferFragment()
            else -> Fragment()
        }

        newFragment?.arguments = bundle

        if(newFragment != null) {
            if(oldFragment != null){
                oldFragment?.exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
                oldFragment?.reenterTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)
                oldFragment?.enterTransition = null
                oldFragment?.returnTransition = null
            }

            newFragment?.exitTransition = null
            newFragment?.reenterTransition = null
            newFragment?.enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true)
            newFragment?.returnTransition = MaterialSharedAxis(MaterialSharedAxis.X, false)

            // Fragment를 교채한다.
            fragmentTransaction.replace(R.id.fragmentContainerView_main, newFragment!!)
            if (addToBackStack == true) {
                // Fragment를 Backstack에 넣어 이전으로 돌아가는 기능이 동작할 수 있도록 한다.
                fragmentTransaction.addToBackStack(name)
            }

            // 교체 명령이 동작하도록 한다.
            fragmentTransaction.commit()
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            viewModel.isFistVisitor.collect {
                isFirstVisitor = it
            }
        }
    }

    fun shouldShowPermissionRationale(): Boolean {
        val permissionList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) permissionListVersion33 else permissionList
        return permissionList.any { permission ->
            shouldShowRequestPermissionRationale(permission)
        }
    }

    private fun navigateToPermissionOrOnboardingOrLogin() {
        CoroutineScope(Dispatchers.Main).launch {
            if ((checkPermission() || shouldShowPermissionRationale()) && isFirstVisitor) {
                activityMainBinding.bottomNavigation.visibility = View.GONE
                replaceFragment(ONBOARDING_FRAGMENT, false, null)
            } else if (!isFirstVisitor) {
                activityMainBinding.bottomNavigation.visibility = View.GONE
                replaceFragment(LOGIN_FRAGMENT, false, null)
            } else {
                activityMainBinding.bottomNavigation.visibility = View.GONE
                replaceFragment(PERMISSION_FRAGMENT, false, null)
            }
            delay(500)
        }
    }


    fun checkPermission(): Boolean {
        return permissionList.all { permission ->
            ActivityCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun removeFragment(name: String){
        supportFragmentManager.popBackStack(name, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun bottomNavigationBar() {
        activityMainBinding.run {
            bottomNavigation.run {
                visibility = View.VISIBLE
                setOnItemSelectedListener(
                    object : NavigationBarView.OnItemSelectedListener {
                        override fun onNavigationItemSelected(item: MenuItem): Boolean {
                            val fragment = when (item.itemId) {
                                R.id.item_bottom_donate -> HOME_FRAGMENT
                                R.id.item_bottom_chat -> CHATTING_FRAGMENT
                                R.id.item_bottom_tip -> BOARD_MAIN_FRAGMENT
                                R.id.item_bottom_info -> USER_INFO_FRAGMENT
                                else -> { HOME_FRAGMENT }
                            }
                            replaceFragment(fragment, false, null)
                            return true
                        }
                    }
                )
            }
        }
    }

    // 시간/날짜를 원하는 형식으로 변경하는 함수
    fun formatTimeDifference(tipDate: Date): String {
        val currentDate = Date()
        val timeDifference = currentDate.time - tipDate.time
        val timeDifferenceInSeconds = TimeUnit.MILLISECONDS.toSeconds(timeDifference)
        val timeDifferenceInMinutes = TimeUnit.MILLISECONDS.toMinutes(timeDifference)
        val timeDifferenceInHours = TimeUnit.MILLISECONDS.toHours(timeDifference)
        val timeDifferenceInDays = TimeUnit.MILLISECONDS.toDays(timeDifference)

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        return when {
            timeDifferenceInSeconds < 60 -> {
                "${timeDifferenceInSeconds}초 전"
            }
            timeDifferenceInMinutes < 60 -> {
                "${timeDifferenceInMinutes}분 전"
            }
            timeDifferenceInHours < 24 -> {
                "${timeDifferenceInHours}시간 전"
            }
            timeDifferenceInDays <= 30 -> {
                "${timeDifferenceInDays}일 전"
            }
            else -> {
                dateFormat.format(tipDate)
            }
        }
    }

    companion object {
        const val PERMISSION_REQUEST_ACCESS = 100
        val LOGIN_FRAGMENT = "LoginFragment"
        val USER_INFO_FRAGMENT = "UserInfoFragment"
        val HOME_FRAGMENT = "HomeFragment"
        val MAP_FRAGMENT = "MapFragment"
        val DONATE_INFO_FRAGMENT = "DonateInfoFragment"
        val ONBOARDING_FRAGMENT = "OnboardingFragment"
        val PERMISSION_FRAGMENT = "PermissionFragment"
        val BOARD_MAIN_FRAGMENT = "BoardMainFragment"
        val BOARD_WRITE_FRAGMENT = "BoardWriteFragment"
        val LOCATION_SETTING_FRAGMENT = "LocationSettingFragment"
        val REVIEW_SHOW_FRAGMENT = "ReviewShowFragment"
        val REVIEW_WRITE_FRAGMENT = "ReviewWriteFragment"
        val BOARD_CONTENTS_FRAGMENT = "BoardContentsFragment"
        val GALLERY_FRAGMENT = "GalleryFragment"
        val BOARD_MODIFY_FRAGMENT = "BoardModifyFragment"
        val DONATE_WRITE_FRAGMENT = "DonateWriteFragment"
        val DONATE_MODIFY_FRAGMENT = "DonateModifyFragment"
        val CHATTING_FRAGMENT = "ChattingFragment"
        val USER_INFO_BOARD_FRAGMENT = "UserInfoBoardFragment"
        val USER_INFO_FOLLOWING_FRAGMENT = "UserInfoFollowingFragment"
        val TRANSFER_FRAGMENT = "TransferFragment"
    }
}