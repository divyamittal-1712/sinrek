package com.app.appsinrek.utils

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.DisplayMetrics
import android.view.View
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.app.appsinrek.R
import com.app.appsinrek.main.post.adapters.MainImageAdapter
import com.app.appsinrek.utils.utility.IMAGE_VIDEO_URI
import java.io.File
import java.text.SimpleDateFormat


fun Context.toDp(px: Float): Float =
    px / (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)

fun Context.toPx(dp: Float): Float =
    dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)


fun View.hide() {
    visibility = View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}
fun View.visible(isVisible: Boolean) {
    visibility = (if(isVisible) View.VISIBLE else View.GONE)
}

fun View.invisible() {
    visibility = View.INVISIBLE
}

val Int.counterText: String
    get() {
        var min = 0
        var sec = "" + this
        if (this > 59) {
            min = this / 60
            sec = "" + (this - 60 * min)
        }
        if (sec.length == 1) {
            sec = "0$sec"
        }
        return "$min:$sec"
    }
val String?.text: String
    get() {
        if (this != null && this.isNotEmpty()) {
            return this;
        }
        return ""
    }

fun Context.scanPhoto(file: File, callback: ((Uri) -> Unit)? = null) =
    MediaScannerConnection.scanFile(
        this,
        arrayOf(file.toString()),
        arrayOf(file.name),
    ) { _, uri ->
        val mainUri = Uri.withAppendedPath(
            IMAGE_VIDEO_URI,
            uri.lastPathSegment
        )
        callback?.invoke(mainUri)
    }

@SuppressLint("SimpleDateFormat")
fun convertDateTime(input: String, format: String, output: String): String? {
    val inputFormat = SimpleDateFormat(format)
    val outputFormat = SimpleDateFormat(output)
    try {
        val dateTime = inputFormat.parse(input)
        return outputFormat.format(dateTime)    // format output
    } catch (e: Exception) {
        e.printStackTrace()
        return ""
    }

    return null
}
fun Context.toast(size: Int) {
    Toast.makeText(
        this, String.format(
            resources.getString(R.string.pix_selection_limiter),
            size
        ), Toast.LENGTH_SHORT
    ).show()
}
fun Context.toast(text: String) {
    Toast.makeText(
        this, text, Toast.LENGTH_SHORT
    ).show()
}


fun Context.color(@ColorRes color: Int) = ResourcesCompat.getColor(resources, color, null)