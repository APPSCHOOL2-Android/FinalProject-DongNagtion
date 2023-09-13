package likelion.project.dongnation.ui.transfer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import likelion.project.dongnation.databinding.FragmentDialogTransferBinding
import likelion.project.dongnation.model.User
import likelion.project.dongnation.ui.main.MainActivity

class TransferDialogFragment(private val transferCode: String) : DialogFragment() {

    private lateinit var viewModel: TransferViewModel
    private lateinit var mainActivity: MainActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = false
    }

    private lateinit var binding: FragmentDialogTransferBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDialogTransferBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[TransferViewModel::class.java]
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        dialog?.window?.setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT)
        binding.editTextDialogTransferTransferCode.setText(transferCode)
        binding.buttonDialogTransferRegistration.setOnClickListener {
            viewModel.updateTransferCode(
                User(
                    userAddress = "서울특별시 종로구 수송동 ",
                    userEmail = "dltkd13956@naver.com",
                    userExperience = 0,
                    userFollowList = listOf(),
                    userFollowingNum = 0,
                    userId = "2eqn9AfBVl9oXROMY2Wx",
                    userName = "이상준",
                    userTransCode = transferCode,
                    userType = 5
                )
            )
        }
        binding.buttonDialogTransferCancel.setOnClickListener {
            dismiss()
        }
        observe()
    }

    private fun observe() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.showMessage.isNotEmpty()) {
                        when (it.showMessage) {
                            "송금 코드 등록 성공" -> {
                                Snackbar.make(mainActivity.activityMainBinding.fragmentContainerViewMain, it.showMessage, Snackbar.LENGTH_SHORT)
                                    .show()
                                mainActivity.replaceFragment(MainActivity.USER_INFO_FRAGMENT, false, null)

                            }

                            "송금 코드 등록 실패" -> {
                                Snackbar.make(requireView(), it.showMessage, Snackbar.LENGTH_SHORT)
                                    .show()
                            }
                        }
                    }
                }
            }
        }
    }
}