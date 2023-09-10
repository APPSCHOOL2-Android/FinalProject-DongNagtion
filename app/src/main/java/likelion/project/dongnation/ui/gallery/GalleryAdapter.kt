package likelion.project.dongnation.ui.gallery

import android.content.ContentUris
import android.content.ContentValues
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.constraintlayout.widget.Group
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import likelion.project.dongnation.R
import likelion.project.dongnation.databinding.ItemGalleryCameraBinding
import likelion.project.dongnation.databinding.ItemGalleryImageBinding
import java.io.File

class GalleryAdapter(
    private val context: GalleryFragment,
    private val setOnItemClickListener: OnItemClickListener
) : ListAdapter<GalleryImage, RecyclerView.ViewHolder>(DIFF_UTIL) {

    private val VIEW_TYPE_CAMERA = 1
    private val VIEW_TYPE_IMAGE = 2
    private var preGroup: Group? = null
    private var preCheckBox: CheckBox? = null
    private val selectedImages = mutableListOf<GalleryImage>()

    inner class CarmeraViewHoler(private val binding: ItemGalleryCameraBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                setOnItemClickListener.setOnItemClickListener()
            }
        }
    }

    inner class ImageViewHoler(private val binding: ItemGalleryImageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GalleryImage) {
            Glide.with(binding.root)
                .load(item.uri)
                .into(binding.imageViewItemGalleryImage)
            binding.groupItemGalleryImage.setOnClickListener {
                if (selectedImages.size < 3 || selectedImages.contains(item)) {
                    binding.groupItemGalleryImage.background =
                        if (binding.groupItemGalleryImage.background == null)
                            context.resources.getDrawable(R.drawable.bg_rect_green200_outline)
                        else null

                    binding.checkBoxItemGalleryImage.isChecked =
                        !binding.checkBoxItemGalleryImage.isChecked

                    if (binding.checkBoxItemGalleryImage.isChecked) {
                        selectedImages.add(item)
                    } else {
                        selectedImages.remove(item)
                    }
                    setOnItemClickListener.setOnImagesClickListener(selectedImages)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_CAMERA -> {
                CarmeraViewHoler(
                    ItemGalleryCameraBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            VIEW_TYPE_IMAGE -> {
                ImageViewHoler(
                    ItemGalleryImageBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_IMAGE) {
            (holder as ImageViewHoler).bind(getItem(position))
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_CAMERA else VIEW_TYPE_IMAGE
    }

    companion object {
        private val DIFF_UTIL = object : DiffUtil.ItemCallback<GalleryImage>() {
            override fun areItemsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean {
                return oldItem.uri == newItem.uri
            }

            override fun areContentsTheSame(oldItem: GalleryImage, newItem: GalleryImage): Boolean {
                return oldItem == newItem
            }
        }
    }

    interface OnItemClickListener {
        fun setOnItemClickListener()
        fun setOnImagesClickListener(images: List<GalleryImage>)
    }
}