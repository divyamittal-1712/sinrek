package com.app.appsinrek.main.dashboard

interface OnSnapPositionChangeListener {

    fun onSnapScroll()
    fun onSnapPositionChange(position: Int, lastPosition: Int)
}