package com.app.appsinrek.viewmodels.helpers

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.app.appsinrek.viewmodels.Mode
import com.app.appsinrek.viewmodels.Options

private val REQUIRED_PERMISSIONS_IMAGES =
    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }
private val REQUIRED_PERMISSIONS_VIDEO = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
} else {
    arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.RECORD_AUDIO,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}

// TODO: 20/06/21 call onBackPressed() method if any permission is denied
fun ActivityResultLauncher<Array<String>>.permissionsFilter(
    fragmentActivity: FragmentActivity,
    options: Options,
    callback: () -> Unit
) {
    if (fragmentActivity.allPermissionsGranted(options.mode)) {
        callback()
    } else {
        this.launch(if (options.mode == Mode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO)
    }
}

private fun Activity.allPermissionsGranted(mode: Mode) =
    (if (mode == Mode.Picture) REQUIRED_PERMISSIONS_IMAGES else REQUIRED_PERMISSIONS_VIDEO).all {
        val check = ContextCompat.checkSelfPermission(
            this, it
        ) == PackageManager.PERMISSION_GRANTED
        check
    }