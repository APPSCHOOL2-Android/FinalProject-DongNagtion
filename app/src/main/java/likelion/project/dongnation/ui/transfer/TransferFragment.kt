package likelion.project.dongnation.ui.transfer

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import likelion.project.dongnation.databinding.FragmentTransferBinding
import likelion.project.dongnation.ui.main.MainActivity


class TransferFragment : Fragment() {

    private lateinit var binding: FragmentTransferBinding
    private lateinit var viewModel: TransferViewModel
    private var currentScroll = TransferGuide.FIRST
    private lateinit var mainActivity: MainActivity
    private lateinit var clipboard: ClipboardManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransferBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[TransferViewModel::class.java]
        mainActivity = activity as MainActivity
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val guide2 = binding.imageViewTransferGuide2
        val guide3 = binding.imageViewTransferGuide3
        val scrollView = binding.scrollViewTransfer
        clipboard = mainActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        addOnScrollChangedListener(scrollView, guide2, guide3)
        setOnTransferCliclListener(guide2, scrollView, guide3)
    }

    private fun setOnTransferCliclListener(
        guide2: ImageView,
        scrollView: NestedScrollView,
        guide3: ImageView
    ) {
        binding.buttonTransfer.setOnClickListener {
            if (navigateToKakaoTalk()) return@setOnClickListener
            currentScroll = setCurrentScroll(guide2, scrollView, guide3)
        }
    }

    private fun setCurrentScroll(
        guide2: ImageView,
        scrollView: NestedScrollView,
        guide3: ImageView
    ): TransferGuide = when (currentScroll) {
        TransferGuide.FIRST -> {
            guide2.post {
                val x = guide2.left
                val y = guide2.top
                scrollView.smoothScrollTo(x, y)
            }
            TransferGuide.SECOND
        }

        TransferGuide.SECOND -> {
            guide3.post {
                val x = guide3.left
                val y = guide3.top
                scrollView.smoothScrollTo(x, y)
            }
            TransferGuide.THIRD
        }

        TransferGuide.THIRD -> {
            guide3.post {
                val x = guide3.left
                val y = guide3.top
                scrollView.smoothScrollTo(x, y)
            }
            TransferGuide.THIRD
        }
    }

    private fun navigateToKakaoTalk(): Boolean {
        if (binding.buttonTransfer.text == "송금코드 등록 하러 가기") {
            val intent = mainActivity.packageManager.getLaunchIntentForPackage("com.kakao.talk")
            if (intent != null) {
                if (getClipboardData().startsWith("https://qr.kakaopay.com")) {
                    showDialog()
                } else {
                    startActivity(intent)
                }
            } else {
                Toast.makeText(requireContext(), "카카오톡이 존재하지 않습니다", Toast.LENGTH_SHORT).show()
            }
            return true
        }
        return false
    }

    private fun addOnScrollChangedListener(
        scrollView: NestedScrollView,
        guide2: ImageView,
        guide3: ImageView
    ) {
        scrollView.viewTreeObserver.addOnScrollChangedListener {
            val view = scrollView.getChildAt(scrollView.childCount - 1)
            val diff = (view.bottom - (scrollView.height + scrollView.scrollY))
            val guide2Position = guide2.top - scrollView.scrollY
            val guide3Position = guide3.top - scrollView.scrollY

            currentScroll = when {
                diff > 0 && guide2Position > 0 -> TransferGuide.FIRST
                guide2Position <= 0 && guide3Position > 0 -> TransferGuide.SECOND
                guide3Position <= 0 && diff > 0 -> TransferGuide.THIRD
                else -> TransferGuide.FIRST
            }
            CoroutineScope(Dispatchers.Default).launch {
                delay(50)
                binding.buttonTransfer.text = if (diff <= 10) "송금코드 등록 하러 가기" else "아래로 스크롤하기"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        CoroutineScope(Dispatchers.Default).launch {
            delay(100)
            if (getClipboardData().startsWith("https://qr.kakaopay.com")) {
                showDialog()
            }
        }
    }

    private fun showDialog() {
        TransferDialogFragment(getClipboardData()).show(
            this@TransferFragment.childFragmentManager, "TransferDialog"
        )
    }

    private fun getClipboardData(): String {
        var transferCode = ""
        if (clipboard.hasPrimaryClip()) {
            val clipData = clipboard.primaryClip
            if (clipData != null && clipData.itemCount > 0) {
                val clipItem = clipData.getItemAt(0)
                if (clipItem.text != null) {
                    transferCode = clipItem.text.toString()
                }
            }
        }

        return transferCode
    }

    override fun onDestroy() {
        super.onDestroy()
        val emptyClipData = ClipData.newPlainText("", "")
        clipboard.setPrimaryClip(emptyClipData)
    }
}

enum class TransferGuide {
    FIRST, SECOND, THIRD
}