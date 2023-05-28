package com.app.appsinrek.viewmodels.helpers

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
import com.app.appsinrek.R

import com.app.appsinrek.main.post.adapters.MainImageAdapter
import com.app.appsinrek.utils.utility.IMAGE_VIDEO_URI
import java.io.File



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


internal fun RecyclerView.setupMainRecyclerView(
    mainImageAdapter: MainImageAdapter,
    onFastScrollListener: RecyclerView.OnScrollListener
) {


    (layoutManager as GridLayoutManager).apply {
        spanCount = mainImageAdapter.spanCount
        spanSizeLookup =
            object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int) =
                    if (mainImageAdapter.getItemViewType(position) == MainImageAdapter.HEADER) mainImageAdapter.spanCount else 1
            }
    }
    adapter = mainImageAdapter
    addOnScrollListener(onFastScrollListener)
//    addItemDecoration(HeaderItemDecoration(context, mainImageAdapter))
}

fun Context.toast(size: Int) {
    Toast.makeText(
        this, String.format(
            resources.getString(R.string.pix_selection_limiter),
            size
        ), Toast.LENGTH_SHORT
    ).show()
}

fun Context.color(@ColorRes color: Int) = ResourcesCompat.getColor(resources, color, null)