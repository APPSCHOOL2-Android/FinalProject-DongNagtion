package likelion.project.dongnation.ui.permission

import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentPermissionBinding
import likelion.project.dongnation.ui.main.MainActivity

class PermissionFragment : Fragment() {

    lateinit var fragmentPermissionBinding: FragmentPermissionBinding
    lateinit var mainActivity: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentPermissionBinding = FragmentPermissionBinding.inflate(inflater)
        mainActivity = activity as MainActivity

        fragmentPermissionBinding.run {
            onPermissionConfirmClick()
        }

        return fragmentPermissionBinding.root
    }

    private fun FragmentPermissionBinding.onPermissionConfirmClick() {
        buttonPermissionConfirm.run {
            setOnClickListener {
                requestPermission()
            }
        }
    }

    private fun showPermissionRationDialog() {
        val builder = AlertDialog.Builder(mainActivity)
        builder.apply {
            setCancelable(false)
            setMessage(PERMISSION_DIALOG_MESSAGE)
            setNegativeButton("취소") { dialog, _ ->
                mainActivity.replaceFragment(MainActivity.ONBOARDING_FRAGMENT, false, null)
            }
            setPositiveButton("설정하러 가기") { dialog, _ ->
                openApplicationSettings()
            }
            show()
        }
    }

    private fun openApplicationSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            val uri = Uri.fromParts("package", mainActivity.packageName, null)
            data = uri
        }
        startActivity(intent)
    }

    private fun requestPermission() {
        requestPermissions(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) mainActivity.permissionListVersion33 else mainActivity.permissionList,
            MainActivity.PERMISSION_REQUEST_ACCESS
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == MainActivity.PERMISSION_REQUEST_ACCESS) {
            when {
                grantResults.all { it == PackageManager.PERMISSION_GRANTED } -> {
                    mainActivity.replaceFragment(MainActivity.ONBOARDING_FRAGMENT, false, null)
                }

                mainActivity.shouldShowPermissionRationale() -> {
                    showPermissionRationDialog()
                }

                else -> {
                    requestPermission()
                }
            }
        }
    }

    companion object {
        private val PERMISSION_DIALOG_MESSAGE =
            """선택 접근 권한을 거부하였습니다.
            |해당 기능을 사용하기 위해서는
            |권한 허용이 필요합니다.
            |기능 사용을 원하신다면 설정 > 애플리케이션
            |> 동네이션 에서 권한 설정을 해주세요""".trimMargin()
    }
}