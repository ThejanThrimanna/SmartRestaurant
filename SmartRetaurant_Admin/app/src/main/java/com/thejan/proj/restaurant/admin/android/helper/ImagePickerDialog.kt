package com.thejan.proj.restaurant.admin.android.helper

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.Restaurant


class ImagePickerDialog : androidx.fragment.app.DialogFragment() {

    private var isCameraAvailable = false
    var openCamera: (() -> Unit)? = null
    var openGallery: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            isCameraAvailable = args.getBoolean(CAMERA_AVAILABLE)
        } ?: run {
            throw IllegalArgumentException("Camera availability unknown")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val options = mutableListOf<CharSequence>()
        if (isCameraAvailable)
            options.add(getText(R.string.use_camera))

        options.add(getText(R.string.pick_from_gallery))

        val items = options.toTypedArray()

        val builder = AlertDialog.Builder(context!!)
        builder.setTitle(getText(R.string.add_image))
                .setItems(items, { _, which ->
                    if (which == 1) {
                        openGallery?.invoke()
                    } else if (isCameraAvailable) {
                        openCamera?.invoke()
                    } else {
                        openGallery?.invoke()
                    }

                })
        return builder.create()
    }

    override fun onDestroy() {
        super.onDestroy()
        openCamera = null
        openGallery = null
    }

    companion object {
        private const val CAMERA_AVAILABLE = "camera_available"

        fun newInstance(isCameraAvailable: Boolean): ImagePickerDialog {
            val fragment = ImagePickerDialog()
            val args = Bundle().apply {
                putBoolean(CAMERA_AVAILABLE, isCameraAvailable)
            }
            fragment.arguments = args
            return fragment
        }
    }
}