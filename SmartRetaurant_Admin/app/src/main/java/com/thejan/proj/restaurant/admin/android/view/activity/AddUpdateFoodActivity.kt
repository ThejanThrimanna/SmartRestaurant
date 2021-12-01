package com.thejan.proj.restaurant.admin.android.view.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.rotateImage
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.thejan.proj.restaurant.admin.android.BuildConfig
import com.thejan.proj.restaurant.admin.android.R
import com.thejan.proj.restaurant.admin.android.helper.*
import com.thejan.proj.restaurant.admin.android.model.Category
import com.thejan.proj.restaurant.admin.android.model.Food
import com.thejan.proj.restaurant.admin.android.model.Type
import com.thejan.proj.restaurant.admin.android.viewmodel.AddUpdateFoodViewModel
import com.thejan.proj.restaurant.admin.android.viewmodel.Status
import com.thejan.proj.restaurant.admin.android.viewmodel.ViewModelState
import kotlinx.android.synthetic.main.activity_add_update_food.*
import kotlinx.android.synthetic.main.activity_add_update_food.btnSave
import kotlinx.android.synthetic.main.activity_add_update_food.etName
import kotlinx.android.synthetic.main.activity_add_update_food.ivImage
import kotlinx.android.synthetic.main.fragment_food.*
import java.io.*
import java.util.*


class AddUpdateFoodActivity : BaseActivity() {

    private lateinit var mViewModel: AddUpdateFoodViewModel
    private lateinit var messageFromResponse: String
    private lateinit var categoriesFromResponse: ArrayList<Category>
    private lateinit var typesFromResponse: ArrayList<Type>
    var photoURI: Uri? = null
    private var isAdd: Boolean = true
    private var currentFood: Food? = null


    var message = Observer<String> { value ->
        messageFromResponse = value!!
    }

    var categories = Observer<ArrayList<Category>> { value ->
        categoriesFromResponse = value!!
        setSpinnerCategory()
    }

    var types = Observer<ArrayList<Type>> { value ->
        typesFromResponse = value!!
        setSpinnerToType()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update_food)
        init()
    }

    private fun init() {
        initViewModel()
        initSubscription()
        initData()
        initAction()
    }

    private fun initAction() {
        ivImage.setOnClickListener {
            showImagePickDialog()
        }

        btnSave.setOnClickListener {
            if (isAdd) {
                if (photoURI != null && etName.text.toString()
                        .isNotEmpty() && etCost.text.toString()
                        .isNotEmpty() && etPrice.text.toString().isNotEmpty()
                ) {
                    if (etPrice.text.toString().toDouble() < etCost.text.toString().toDouble()){
                        showToast(getString(R.string.price_should_be_higher_than_the_cost))
                    }else{
                        mViewModel.saveData(
                            isAdd,
                            photoURI!!,
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etCost.text.toString().toDouble(),
                            etPrice.text.toString().toDouble(),
                            categoriesFromResponse[categorySpinner.selectedItemPosition].catID!!,
                            typesFromResponse[spinnerType.selectedItemPosition].typeId!!,
                            ""
                        )
                    }

                } else {
                    showToast(getString(R.string.please_provide_all_details))
                }
            } else {
                if (photoURI != null) {
                    if (etPrice.text.toString().toDouble() < etCost.text.toString().toDouble()){
                        showToast(getString(R.string.price_should_be_higher_than_the_cost))
                    }else {
                        mViewModel.saveData(
                            isAdd, photoURI!!,
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etCost.text.toString().toDouble(),
                            etPrice.text.toString().toDouble(),
                            categoriesFromResponse[categorySpinner.selectedItemPosition].catID!!,
                            typesFromResponse[spinnerType.selectedItemPosition].typeId!!,
                            currentFood!!.foodId!!
                        )
                    }
                } else if (etName.text.toString() != currentFood!!.name ||
                    etDesc.text.toString() != currentFood!!.desc ||
                    etCost.text.toString().toDouble() != currentFood!!.cost ||
                    etPrice.text.toString().toDouble() != currentFood!!.price ||
                    categoriesFromResponse[categorySpinner.selectedItemPosition].catID!! != currentFood!!.cat ||
                    typesFromResponse[spinnerType.selectedItemPosition].typeId!! != currentFood!!.type
                ) {
                    if (etPrice.text.toString().toDouble() < etCost.text.toString().toDouble()){
                        showToast(getString(R.string.price_should_be_higher_than_the_cost))
                    }else {
                        mViewModel.updateFood(
                            false,
                            etName.text.toString(),
                            etDesc.text.toString(),
                            etCost.text.toString().toDouble(),
                            etPrice.text.toString().toDouble(),
                            categoriesFromResponse[categorySpinner.selectedItemPosition].catID!!,
                            typesFromResponse[spinnerType.selectedItemPosition].typeId!!,
                            currentFood!!.foodId!!
                        )
                    }
                } else {
                    showToast(getString(R.string.please_make_a_change_to_update))
                }
            }
        }
    }

    private fun initData() {
        isAdd = intent.getBooleanExtra(IS_ADD, true)
        currentFood = intent.getParcelableExtra(OBJECT)
        if (!isAdd) {
            tvTitle.text = getString(R.string.update_food)
            btnSave.text = getString(R.string.update)
            AppUtils.loadImageGlide(this, currentFood!!.image!!, ivImage)
            etName.setText(currentFood!!.name)
            etDesc.setText(currentFood!!.desc)
            etCost.setText(String.format("%.2f", currentFood!!.cost))
            etPrice.setText(String.format("%.2f", currentFood!!.price))
        }
        mViewModel.getCategories()
    }

    private fun initViewModel() {
        mViewModel = ViewModelProviders.of(this).get(AddUpdateFoodViewModel::class.java)
    }

    private fun initSubscription() {
        mViewModel.message.observe(this, message)
        mViewModel.categories.observe(this, categories)
        mViewModel.types.observe(this, types)
        mViewModel.state!!.observe(this, Observer<ViewModelState> {
            it?.let {
                update(it)
            }
        })
    }

    private fun setSpinnerCategory() {
        var names = categoriesFromResponse.map { it -> it.name }
        var ids = categoriesFromResponse.map { it -> it.catID }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, names
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = adapter

        categorySpinner.setSelection(0)

        if (isAdd)
            categorySpinner.setSelection(0)
        else
            categorySpinner.setSelection(ids.indexOf(currentFood!!.cat))
    }

    private fun setSpinnerToType() {
        var names = typesFromResponse.map { it -> it.name }
        var ids = typesFromResponse.map { it -> it.typeId }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this, android.R.layout.simple_spinner_item, names
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapter

        spinnerType.setSelection(1)

        if (isAdd)
            spinnerType.setSelection(0)
        else
            spinnerType.setSelection(ids.indexOf(currentFood!!.type))
    }

    private fun showImagePickDialog() {
        val isCameraAvailable = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)
        val imagePickDialog = ImagePickerDialog.newInstance(isCameraAvailable)
        imagePickDialog.openGallery = this::openGallery
        imagePickDialog.openCamera = this::openCamera
        supportFragmentManager?.let { imagePickDialog.show(it, "") }
    }

    private fun openGallery() {
        val mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
        val pickPhoto = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        }
        startActivityForResult(
            Intent.createChooser(pickPhoto, getString(R.string.choose_images)),
            IMAGE_PICKER_REQUEST
        )
    }

    private fun openCamera() {
        val permission = ContextCompat.checkSelfPermission(this!!, Manifest.permission.CAMERA)
        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST)
        } else {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (takePictureIntent.resolveActivity(packageManager) != null) {
                try {
                    deleteTempCameraImage()
                    val parentDir = File(
                        getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                        "${File.separator}Temp"
                    )
                    if (!parentDir.exists()) {
                        parentDir.mkdir()
                    }

                    val photoFile = File.createTempFile("temp", ".jpg", parentDir)
                    photoURI = FileProvider.getUriForFile(
                        this,
                        "${BuildConfig.APPLICATION_ID}.fileprovider",
                        photoFile
                    )

                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST)
                } catch (ex: IOException) {
                    FirebaseCrashlytics.getInstance().log(ex.printStackTrace().toString())
                    Toast.makeText(this, R.string.unable_to_open_camera, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun deleteTempCameraImage() {
        val tempFile = File(
            getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "${File.separator}Temp"
        )
        if (tempFile.exists()) {
            tempFile.deleteRecursively() // delete previous temp files
        }
    }

    private fun update(state: ViewModelState) {
        when (state.status) {
            Status.LOADING -> {
                showProgress()
            }
            Status.SUB_SUCCESS1 -> {
                hideProgress()
            }
            Status.SUCCESS -> {
                hideProgress()
                showToast(getString(R.string.successfully_food_item_added))
                finish()
            }
            Status.SUB_SUCCESS2 -> {
                hideProgress()
                showToast(getString(R.string.successfully_updated))
                onBackPressed()
            }
            Status.ERROR -> {
                hideProgress()
            }
            Status.TIMEOUT -> {
                hideProgress()
            }
            Status.LIST_EMPTY -> {
                hideProgress()
            }
            Status.ITEM_EXISTS -> {
                hideProgress()
                showToast(getString(R.string.item_name_exists))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_PICKER_REQUEST -> {
                    val selectedImage: Uri = data!!.data!!
                    photoURI = selectedImage
                    try {
                        var bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(),
                            selectedImage
                        )
                        bitmap = AppUtils.resizePhoto(bitmap)
                        AppUtils.loadImageGlide(
                            this@AddUpdateFoodActivity,
                            bitmap!!,
                            ivImage
                        )
                    } catch (e: IOException) {
                        Log.i("TAG", "Some exception $e")
                    }
                }
                CAMERA_REQUEST -> {
                    val selectedImageUri = photoURI
                    val imageName = UUID.randomUUID().toString()
                    val localImagePath = copyImageToAppDir(selectedImageUri!!, imageName)

                    AppUtils.loadImageGlide(
                        this@AddUpdateFoodActivity,
                        localImagePath!!,
                        ivImage
                    )
                }
            }
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun imageOrientationValidator(tempBitmap: Bitmap?, uri: Uri): Bitmap? {
        var bitmap = tempBitmap
        val ei: ExifInterface
        try {
            val input = contentResolver.openInputStream(uri)
            ei = ExifInterface(input!!)
            val orientation = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            bitmap?.let {
                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> bitmap = rotateImage(it, 90)
                    ExifInterface.ORIENTATION_ROTATE_180 -> bitmap = rotateImage(it, 180)
                    ExifInterface.ORIENTATION_ROTATE_270 -> bitmap = rotateImage(it, 270)
                }
            }
            input!!.close()
        } catch (e: IOException) {
            e.printStackTrace()
            FirebaseCrashlytics.getInstance().log(e.printStackTrace().toString())
        }

        return bitmap
    }

    private fun decodeBitmapFromUri(
        uri: Uri,
        reqWidth: Int, reqHeight: Int
    ): Bitmap? {

        var input: BufferedInputStream? = null
        try {
            input = BufferedInputStream(contentResolver.openInputStream(uri))
            input.mark(input.available())
            // First decode with inJustDecodeBounds=true to check dimensions
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeStream(input, null, options)

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false
            options.inScaled = false
            options.inPreferredConfig = Bitmap.Config.RGB_565
            options.inDither = true
            input.reset()
            val bitmap = BitmapFactory.decodeStream(input, null, options)
            return imageOrientationValidator(bitmap, uri)
        } catch (ex: Exception) {
            FirebaseCrashlytics.getInstance().log(ex.printStackTrace().toString())

        } finally {
            try {
                input?.close()
            } catch (ex: Exception) {
                FirebaseCrashlytics.getInstance().log(ex.printStackTrace().toString())
            }
        }

        return null
    }

    private fun copyImageToAppDir(uri: Uri, fileName: String): String? {
        val cr = contentResolver
        val mime = MimeTypeMap.getSingleton()
        val t = cr.getType(uri)
        val type = mime.getExtensionFromMimeType(t)

        if ("jpg" == type || "jpeg" == type || "png" == type) {
            // downscale and save image to local app folder
            val bitmap = decodeBitmapFromUri(uri, reqWidth = 1024, reqHeight = 768)
            bitmap?.let {
                val folder = File(
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "${File.separator}STC${File.separator}pg"
                )
                if (!folder.exists()) {
                    folder.mkdirs()
                }

                val s = "$fileName.$type"
                val f = File(folder.absolutePath, s)
                var fos: FileOutputStream? = null

                try {
                    fos = FileOutputStream(f)
                    if ("png" == type) {
                        bitmap.compress(Bitmap.CompressFormat.PNG, 75, fos)
                    } else {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fos)
                    }
                    return f.absolutePath
                } catch (e: FileNotFoundException) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().log(e.printStackTrace().toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().log(e.printStackTrace().toString())
                } finally {
                    try {
                        fos?.flush()
                        fos?.close()
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                        FirebaseCrashlytics.getInstance().log(ex.printStackTrace().toString())
                    }
                }

                bitmap.recycle()
            }
        } else {
            return INVALID_IMAGE
        }

        return null
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AppUtils.closeActivityLeftToRight(this)
    }
}