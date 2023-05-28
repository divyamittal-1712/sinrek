package com.app.appsinrek.viewmodels.interfaces

import android.view.View
import com.app.appsinrek.viewmodels.Img
import com.app.appsinrek.utils.utility.HeaderItemDecoration




interface PixLifecycle {
    //Image selection
    fun onImageSelected(element: Img?, position: Int, callback: (Boolean) -> Boolean)
    fun onImageLongSelected(element: Img?, position: Int, callback: (Boolean) -> Boolean)
}

interface OnSelectionListener {
    fun onClick(element: Img?, view: View?, position: Int)
    fun onLongClick(element: Img?, view: View?, position: Int)
}

interface SectionIndexer {
    fun getSectionText(position: Int): String?
}

interface StickyHeaderInterface {
    /**
     * This method gets called by [HeaderItemDecoration] to fetch the position of the header item in the adapter
     * that is used for (represents) item at specified position.
     *
     * @param itemPosition int. Adapter's position of the item for which to do the search of the position of the header item.
     * @return int. Position of the header item in the adapter.
     */
    fun getHeaderPositionForItem(itemPosition: Int): Int

    /**
     * This method gets called by [HeaderItemDecoration] to get layout resource id for the header item at specified adapter's position.
     *
     * @param headerPosition int. Position of the header item in the adapter.
     * @return int. Layout resource id.
     */
    fun getHeaderLayout(headerPosition: Int): Int

    /**
     * This method gets called by [HeaderItemDecoration] to setup the header View.
     *
     * @param header         View. Header to set the data on.
     * @param headerPosition int. Position of the header item in the adapter.
     */
    fun bindHeaderData(header: View, headerPosition: Int)

    /**
     * This method gets called by [HeaderItemDecoration] to verify whether the item represents a header.
     *
     * @param itemPosition int.
     * @return true, if item at the specified adapter's position represents a header.
     */
    fun isHeader(itemPosition: Int): Boolean
}