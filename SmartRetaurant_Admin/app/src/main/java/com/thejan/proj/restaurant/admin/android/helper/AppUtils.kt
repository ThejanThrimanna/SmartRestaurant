package com.thejan.proj.restaurant.admin.android.helper

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ColorDrawable
import android.util.Base64
import android.view.View
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.thejan.proj.restaurant.admin.android.R
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import kotlin.math.roundToInt

/**
 * Created by thejanthrimanna on 2020-10-19.
 */
object AppUtils {

    fun startActivityRightToLeft(activity: Activity, aClass: Class<out Activity>) {
        val intent = Intent(activity, aClass)
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
    }

    fun startActivityRightToLeft(activity: Activity, intent: Intent) {

        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
    }


    fun startActivityRightToLeftForResult(
        activity: Activity,
        aClass: Class<out Activity>,
        requestCode: Int
    ) {
        val intent = Intent(activity, aClass)
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
    }

    fun startActivityRightToLeftForResult(activity: Activity, intent: Intent, requestCode: Int) {
//        val intent = Intent(activity, aClass)
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivityForResult(intent, requestCode)
        activity.overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left)
    }


    fun closeActivityLeftToRight(activity: Activity) {

        activity.overridePendingTransition(R.anim.left_in, R.anim.slide_to_right)
    }

    fun startActivityForLogout(activity: Activity, aClass: Class<out Activity>) {
        activity.finish()
        val intent = Intent(activity, aClass)
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent)
        activity.overridePendingTransition(R.anim.left_in, R.anim.slide_to_right)
    }

    fun loadImageGlide(activity: Context, url: String, iv: ImageView) {
        Glide.with(activity)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .into(iv)
    }

    fun loadImageGlide(activity: Context, url: Bitmap, iv: ImageView) {
        Glide.with(activity)
            .load(url)
            .placeholder(R.drawable.ic_placeholder)
            .into(iv)
    }

    fun loadImageGlide(activity: Context, drawable: Int, iv: ImageView) {
        Glide.with(activity)
            .load(drawable)
            .error(R.drawable.ic_error)
            .into(iv)
    }

    fun loadImagePicaso(url: String, iv: ImageView) {
        Picasso.get().load(url).placeholder(R.drawable.ic_placeholder).into(iv)
    }

    fun loadImagePicaso(drawable: Int, iv: ImageView) {
        Picasso.get().load(drawable).error(R.drawable.ic_placeholder).into(iv)
    }

    fun showProgress(activity: Activity): Dialog {
        val dialog = Dialog(activity)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(0)
        )
        dialog.setContentView(R.layout.fragment_dialog_progress)
        dialog.setCancelable(false)
        //        dialog.setb
        dialog.window!!.decorView.setBackgroundResource(android.R.color.transparent)
        dialog.window!!.setDimAmount(0.0f)
        return dialog
    }

    fun slideUp(view: View) {
        if (view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
            view.alpha = 0f
            if (view.height > 0) {
                slideUpNow(view)
            } else {
                // wait till height is measured
                view.post { slideUpNow(view) }
            }
        }
    }

    fun slideDown(view: View) {
        view.animate()
            .translationY(view.height.toFloat())
            .alpha(0f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    // superfluous restoration
                    view.visibility = View.GONE
                    view.alpha = 1f
                    view.translationY = 0f
                }
            })
    }

    private fun slideUpNow(view: View) {
        if (view.visibility == View.VISIBLE) {
            view.translationY = view.height.toFloat()
            view.animate()
                .translationY(0f)
                .alpha(1f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        view.visibility = View.VISIBLE
                        view.alpha = 1f
                    }
                })
        }
    }


    /*fun getDisplayableTime(delta: Long): String? {
        var difference: Long = 0
        val mDate = System.currentTimeMillis()
        if (mDate > delta) {
            difference = mDate - delta
            val seconds = difference / 1000
            val minutes = seconds / 60
            val hours = minutes / 60
            val days = hours / 24
            val months = days / 31
            val years = days / 365
            return if (seconds < 0) {
                "not yet"
            } else if (seconds < 60) {
                if (seconds == 1L) "one second ago" else "$seconds seconds ago"
            } else if (seconds < 120) {
                "a minute ago"
            } else if (seconds < 2700) // 45 * 60
            {
                "$minutes minutes ago"
            } else if (seconds < 5400) // 90 * 60
            {
                "an hour ago"
            } else if (seconds < 86400) // 24 * 60 * 60
            {
                "$hours hours ago"
            } else if (seconds < 172800) // 48 * 60 * 60
            {
                "yesterday"
            } else if (seconds < 2592000) // 30 * 24 * 60 * 60
            {
                "$days days ago"
            } else if (seconds < 31104000) // 12 * 30 * 24 * 60 * 60
            {
                if (months <= 1) "one month ago" else "$days months ago"
            } else {
                if (years <= 1) "one year ago" else "$years years ago"
            }
        }
        return null
    }*/

    fun convertStringToMiliSeconds(timeX: String, formatX: String): Long {

        val formatter = SimpleDateFormat(formatX)
        try {
            var mDate = formatter.parse(timeX)
            return mDate.time
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return 0
    }

    fun convertTimeInMillisToDateString(dateFormat: String, timeInMillis: Long): String? {
        val d = Date(timeInMillis)
        val sdf = SimpleDateFormat(dateFormat)
        return sdf.format(d)
    }


    fun resizePhoto(bitmap: Bitmap): Bitmap {
        val ratio: Float = bitmap.height.toFloat() / bitmap.width.toFloat()
        val width: Int = (400 / 4.5).roundToInt()
        val W = 300
        val H = 300
        val b = Bitmap.createScaledBitmap(bitmap, W, H, false)
        return b
    }

    fun encrypt(strToEncrypt: String): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(IV, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =
                PBEKeySpec(
                    SECRET_KEY.toCharArray(),
                    Base64.decode(SALT, Base64.DEFAULT),
                    10000,
                    256
                )
            val tmp = factory.generateSecret(spec)
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec)
            return Base64.encodeToString(
                cipher.doFinal(strToEncrypt.toByteArray(Charsets.UTF_8)),
                Base64.DEFAULT
            )
        } catch (e: Exception) {
            println("Error while encrypting: $e")
        }
        return null
    }

    fun decrypt(strToDecrypt: String): String? {
        try {
            val ivParameterSpec = IvParameterSpec(Base64.decode(IV, Base64.DEFAULT))

            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            val spec =
                PBEKeySpec(
                    SECRET_KEY.toCharArray(),
                    Base64.decode(SALT, Base64.DEFAULT),
                    10000,
                    256
                )
            val tmp = factory.generateSecret(spec);
            val secretKey = SecretKeySpec(tmp.encoded, "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            return String(cipher.doFinal(Base64.decode(strToDecrypt, Base64.DEFAULT)))
        } catch (e: Exception) {
            println("Error while decrypting: $e");
        }
        return null
    }
}

