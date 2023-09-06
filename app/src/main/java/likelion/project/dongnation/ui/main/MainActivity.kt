package likelion.project.dongnation.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ActivityMainBinding
import likelion.project.dongnation.ui.home.HomeFragment
import likelion.project.dongnation.ui.login.LoginFragment
import likelion.project.dongnation.ui.userInfo.UserInfoFragment

class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        replaceFragment(USER_INFO_FRAGMENT, false, null)
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
    companion object {
        val LOGIN_FRAGMENT = "LoginFragment"
        val USER_INFO_FRAGMENT = "UserInfoFragment"
        val HOME_FRAGMENT = "HomeFragment"
    }
}