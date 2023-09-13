package likelion.project.dongnation.ui.gallery

import android.app.Activity.RESULT_OK
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.database.getStringOrNull
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.qure.core_design.custom.recyclerview.RecyclerViewItemDecoration
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.FragmentGalleryBinding
import likelion.project.dongnation.ui.main.MainActivity
import java.io.ByteArrayOutputStream
import java.io.IOException


class GalleryFragment : Fragment(), GalleryAdapter.OnItemClickListener {

    private lateinit var adapter: GalleryAdapter
    private lateinit var binding: FragmentGalleryBinding
    private lateinit var mainActivity: MainActivity
    private lateinit var viewModel: GalleryViewModel
    private lateinit var selectedImages: List<GalleryImage>

    private lateinit var itemListener: GalleryAdapter.OnItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGalleryBinding.inflate(inflater)
        mainActivity = activity as MainActivity
        viewModel = ViewModelProvider(this)[GalleryViewModel::class.java]
        itemListener = this@GalleryFragment
        initView()
        initRecyclerView()
        observe()
        return binding.root
    }

    private fun initView() {
        with(binding.toolbarGalery) {
            inflateMenu(R.menu.menu_gallery)
            setNavigationOnClickListener {

            }
            menu.findItem(R.id.item_image_selection).isEnabled = false
            setOnMenuItemClickListener {
                if (it.itemId == R.id.item_image_selection) {

                    mainActivity.supportFragmentManager.setFragmentResult("images", bundleOf(
                        "imageList" to selectedImages.toTypedArray()
                    ))

                }
                mainActivity.supportFragmentManager.popBackStack()
                true
            }
        }
    }

    private fun observe() {
        lifecycleScope.launch {
            loadGalleryImages()
            lifecycleScope.launchWhenStarted {
                viewModel.images.collect {
                    adapter.submitList(it)
                }
            }
        }
    }

    private fun initRecyclerView() {
        adapter = GalleryAdapter(this, itemListener)
        binding.recyclerViewGalleryImages.adapter = adapter
        binding.recyclerViewGalleryImages.addItemDecoration(RecyclerViewItemDecoration(10))
    }

    private suspend fun loadGalleryImages() {
        return withContext(Dispatchers.IO) {
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )

            val imageCollectionUri =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) MediaStore.Images.Media.getContentUri(
                    MediaStore.VOLUME_EXTERNAL_PRIMARY
                )
                else MediaStore.Images.Media.EXTERNAL_CONTENT_URI

            requireContext().contentResolver.query(
                imageCollectionUri,
                projection,
                null,
                null,
                null,
            )?.use { cursor ->
                val images = mutableListOf(GalleryImage())
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val nameColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
                val bucketIdColumn =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)
                val bucketNameColumn =
                    cursor.getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val bucketId = cursor.getLong(bucketIdColumn)
                    val bucketName = cursor.getStringOrNull(bucketNameColumn) ?: ""
                    val uri = ContentUris.withAppendedId(imageCollectionUri, id)
                    val image = GalleryImage(uri, name, bucketId, bucketName)
                    images.add(image)
                }
                cursor.close()
                viewModel.setImages(images)
            } ?: throw IOException()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            DEFAULT_GALLERY_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startCamera()
                } else {
                    Snackbar.make(this.requireView(), "권한이 거부되었습니다.", Snackbar.LENGTH_SHORT)
                        .show()
                }
                return
            }
        }
    }

    private fun getImageUri(bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            mainActivity.contentResolver,
            bitmap,
            System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path)
    }

    private fun startCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePicture.launch(cameraIntent)
    }

    private val takePicture =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val imageBitmap: Bitmap = it.data?.extras?.get("data") as Bitmap
                getImageUri(imageBitmap)
                lifecycleScope.launch {
                    loadGalleryImages()
                }
            }
        }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    mainActivity,
                    android.Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                return true
            }

        } else if (ActivityCompat.checkSelfPermission(
                mainActivity,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                ),
                DEFAULT_GALLERY_REQUEST_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                mainActivity,
                arrayOf(
                    android.Manifest.permission.CAMERA,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                ),
                DEFAULT_GALLERY_REQUEST_CODE
            )
        }
    }

    override fun setOnItemClickListener() {
        if (checkPermission()) {
            startCamera()
        } else {
            requestPermission()
        }
    }

    override fun setOnImagesClickListener(images: List<GalleryImage>) {
        val count = images.size
        with(binding) {
            textViewGalleryImageCount.text = "총 ${count}장"
            val changedColor = if (count == 3) R.color.red else R.color.black
            textViewGalleryImageCount.setTextColor(resources.getColor(changedColor))
            selectedImages = images
            toolbarGalery.menu.findItem(R.id.item_image_selection).isEnabled =
                images.isNotEmpty()
        }
    }

    companion object {
        const val DEFAULT_GALLERY_REQUEST_CODE = 2
    }
}