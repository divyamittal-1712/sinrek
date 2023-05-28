package com.app.appsinrek.viewmodels.helpers

import com.app.appsinrek.main.post.adapters.MainImageAdapter

// TODO: 18/06/21 if possible include in fragment class
internal lateinit var mainImageAdapter: MainImageAdapter

fun Int.selection(b: Boolean) {
    mainImageAdapter.select(b, this)
}
