package com.app.appsinrek.main.dashboard

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper

class SnapOnScrollListener(
        private val snapHelper: SnapHelper,
        var onSnapPositionChangeListener: OnSnapPositionChangeListener? = null
) : RecyclerView.OnScrollListener() {

    enum class Behavior {
        NOTIFY_ON_SCROLL,
        NOTIFY_ON_SCROLL_STATE_IDLE
    }

    private var snapPosition = RecyclerView.NO_POSITION

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {

    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
//        Log.e("State ::"," $newState");
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            maybeNotifySnapPositionChange(recyclerView)
        }else{
            onSnapPositionChangeListener?.onSnapScroll()
        }
    }

    private fun maybeNotifySnapPositionChange(recyclerView: RecyclerView) {
        val snapPosition = snapHelper.getSnapPosition(recyclerView)
        val lastPosition = getLastVisiblePosition(recyclerView)
//        val snapPositionChanged = this.snapPosition != snapPosition
//        if (snapPositionChanged) {
            onSnapPositionChangeListener?.onSnapPositionChange(snapPosition,lastPosition)
            this.snapPosition = snapPosition
//        }
    }
    private fun SnapHelper.getSnapPosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager ?: return RecyclerView.NO_POSITION
        val snapView = findSnapView(layoutManager) ?: return RecyclerView.NO_POSITION
        return layoutManager.getPosition(snapView)
    }
    private fun getLastVisiblePosition(recyclerView: RecyclerView): Int {
        val layoutManager = recyclerView.layoutManager as LinearLayoutManager?
        val firstVisible = layoutManager!!.findFirstVisibleItemPosition()
        val lastVisible = layoutManager.findLastVisibleItemPosition()
        return lastVisible;
    }
}