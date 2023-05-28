package com.app.appsinrek.main.post.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.app.appsinrek.databinding.HeaderRowBinding
import com.app.appsinrek.databinding.MainImageBinding
import com.app.appsinrek.utils.*
import com.app.appsinrek.viewmodels.Img
import com.app.appsinrek.viewmodels.Options
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.app.appsinrek.viewmodels.interfaces.OnSelectionListener
import com.app.appsinrek.viewmodels.interfaces.SectionIndexer
import com.app.appsinrek.utils.utility.TAG
import com.app.appsinrek.utils.utility.WIDTH


internal class MainImageAdapter(val context: Context, internal val spanCount: Int, var options: Options) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        SectionIndexer, ListPreloader.PreloadModelProvider<Img> {

    val itemList: ArrayList<Img> = ArrayList()
    val selected: ArrayList<Int> = ArrayList()

    private var onSelectionListener: OnSelectionListener? = null
    private val layoutParams: FrameLayout.LayoutParams
    private val glide: RequestManager
    private val reqOptions: RequestOptions
    internal var sizeProvider: ListPreloader.PreloadSizeProvider<Img>

    val listSize: Int
        get() {
            return itemList.size
        }

    init {
        val size: Int = WIDTH / spanCount - MARGIN / 2
        layoutParams = FrameLayout.LayoutParams(size, size)
        layoutParams.setMargins(MARGIN, MARGIN - MARGIN / 2, MARGIN, MARGIN - MARGIN / 2)
        reqOptions = RequestOptions().override(size - 50)
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
        glide = Glide.with(context)
        sizeProvider = FixedPreloadSizeProvider(size, size)
    }

    fun addOnSelectionListener(onSelectionListener: OnSelectionListener?) {
        this.onSelectionListener = onSelectionListener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addImageList(images: ArrayList<Img>) {
        itemList.addAll(images)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return 0
        if (itemList.size <= position) {
            return 0
        }
        val i = itemList[position]
        return if (i.contentUrl == Uri.EMPTY) HEADER else ITEM
    }

    fun clearList() {
        itemList.clear()
    }

    fun select(isSelected: Boolean, pos: Int) {
        itemList[pos].selected = isSelected
        notifyItemChanged(pos, itemList[pos])
    }

    override fun getItemId(position: Int): Long {
        return itemList[position].contentUrl.hashCode().toLong()
    }

    // TODO: 18/06/21 first header blinks on image selection need to be resolved
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutManager = LayoutInflater.from(parent.context)
        return if (viewType == HEADER)
            HeaderHolder(HeaderRowBinding.inflate(layoutManager, parent, false))
        else
            Holder(MainImageBinding.inflate(layoutManager, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val image = itemList[position]
        if (holder is Holder) holder.bind(image)
        else if (holder is HeaderHolder) {
            holder.bind(image.headerDate!!)
        }
    }

    override fun getItemCount() = itemList.size

    override fun getSectionText(position: Int) = itemList[position].headerDate

    fun getSectionMonthYearText(position: Int) =
            if (itemList.size <= position) "" else itemList[position].headerDate

    inner class Holder(private val mainImageBinding: MainImageBinding) :
            RecyclerView.ViewHolder(mainImageBinding.root),
            View.OnClickListener, View.OnLongClickListener {
        override fun onClick(view: View) {
            val id = this.layoutPosition
            if (!selected.contains(id)) {
                for (selected_id in selected) {
                    selected.remove(selected_id)
                    itemList[selected_id].selected = false
                    notifyItemChanged(selected_id)
                    onSelectionListener!!.onClick(itemList[selected_id], view, selected_id)
                }
                selected.add(id)
                itemList[id].selected = true
            }
            notifyItemChanged(id)
            onSelectionListener!!.onClick(itemList[id], view, id)
        }

        override fun onLongClick(view: View): Boolean {
//            val id = this.layoutPosition
//            if(selected.contains(id)){
//                selected.remove(id)
//                itemList[id].selected = false
//            }
//            else{
//                for (selected_id in selected) {
//                    selected.remove(selected_id)
//                    itemList[selected_id].selected = false
//                }
////                if(selected.size < options.count){
//                    selected.add(id)
//                    itemList[id].selected = true
////                }else{
////                    context.toast(options.count)
////                }
//            }
//            notifyItemChanged(id)
//            onSelectionListener!!.onLongClick(itemList[id], view, id)
            return true
        }

        fun bind(image: Img) {
            mainImageBinding.root.setOnClickListener(this)
            mainImageBinding.root.setOnLongClickListener(this)
            mainImageBinding.preview.layoutParams = layoutParams
            glide.asBitmap()
                    .load(image.contentUrl)
                    .apply(reqOptions)
                    .into(mainImageBinding.preview)
            if (image.mediaType == 1) {
                mainImageBinding.isVideo.hide()
            } else if (image.mediaType == 3) {
                mainImageBinding.isVideo.show()
            }
            mainImageBinding.selection.apply {
                if (!image.selected) {
                    hide()
                } else {
                    show()
                }
            }
        }
    }

    inner class HeaderHolder(private val headerRowBinding: HeaderRowBinding) : RecyclerView.ViewHolder(headerRowBinding.root) {
        fun bind(headerDate: String) {
            headerRowBinding.header.text = headerDate
        }
    }

    companion object {
        const val HEADER = 1
        const val ITEM = 2
        private const val MARGIN = 4
    }


    override fun getPreloadItems(position: Int): MutableList<Img> {
        return try {
            itemList.subList(position, position + 1)
        } catch (e: Exception) {
            Log.e(TAG, "getPreloadItems ", e)
            ArrayList()
        }
    }

    override fun getPreloadRequestBuilder(image: Img): RequestBuilder<*> {
        return when (image.mediaType) {
            1 -> glide.load(image.contentUrl).apply(reqOptions)
            3 -> glide.asBitmap()
                    .load(image.contentUrl)
                    .apply(reqOptions)
            else -> glide.load(image.contentUrl).apply(reqOptions)
        }
    }
}