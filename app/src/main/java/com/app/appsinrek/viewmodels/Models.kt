package com.app.appsinrek.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import java.util.*
@SuppressLint("ParcelCreator")

data class Img(
    var headerDate: String? = "",
    var contentUrl: Uri? = Uri.EMPTY,
    var scrollerDate: String? = "",
    var mediaType: Int = 1
) : Parcelable {
    var selected = false

    var position = 0

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readParcelable(Uri::class.java.classLoader),
        parcel.readString(),
        parcel.readInt()
    ) {
        selected = parcel.readByte() != 0.toByte()
        position = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(headerDate)
        parcel.writeParcelable(contentUrl, flags)
        parcel.writeString(scrollerDate)
        parcel.writeInt(mediaType)
        parcel.writeByte(if (selected) 1 else 0)
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Img> {
        override fun createFromParcel(parcel: Parcel): Img {
            return Img(parcel)
        }

        override fun newArray(size: Int): Array<Img?> {
            return arrayOfNulls(size)
        }
    }

}

@SuppressLint("ParcelCreator")
class Options() : Parcelable {
    var ratio = Ratio.RATIO_AUTO
    var count = 1
    var spanCount = 4
    var path = "Pix/Camera"
    var isFrontFacing = false
    var mode = Mode.Picture
    var flash = Flash.Auto
    var preSelectedUrls = ArrayList<Uri>()
    var videoOptions : VideoOptions = VideoOptions()

    constructor(parcel: Parcel) : this() {
        ratio = parcel.readParcelable(Ratio::class.java.classLoader)!!
        count = parcel.readInt()
        spanCount = parcel.readInt()
        path = parcel.readString()!!
        isFrontFacing = parcel.readByte() != 0.toByte()
        mode = parcel.readParcelable(Mode::class.java.classLoader)!!
        flash = parcel.readParcelable(Flash::class.java.classLoader)!!
        videoOptions = parcel.readParcelable(VideoOptions::class.java.classLoader)!!
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(ratio, flags)
        parcel.writeInt(count)
        parcel.writeInt(spanCount)
        parcel.writeString(path)
        parcel.writeByte(if (isFrontFacing) 1 else 0)
        parcel.writeParcelable(mode, flags)
        parcel.writeParcelable(flash, flags)
        parcel.writeParcelable(videoOptions, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Options> {
        override fun createFromParcel(parcel: Parcel): Options {
            return Options(parcel)
        }

        override fun newArray(size: Int): Array<Options?> {
            return arrayOfNulls(size)
        }
    }

}

enum class Mode() : Parcelable {

    All, Picture, Video;
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Mode> {
        override fun createFromParcel(p0: Parcel?): Mode {
            TODO("Not yet implemented")
        }

        override fun newArray(size: Int): Array<Mode?> {
            return arrayOfNulls(size)
        }
    }

}

enum class Flash() : Parcelable {


    Disabled, On, Off, Auto;
    constructor(parcel: Parcel) : this() {
    }
    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Flash> {


        override fun newArray(size: Int): Array<Flash?> {
            return arrayOfNulls(size)
        }

        override fun createFromParcel(p0: Parcel?): Flash {
            TODO("Not yet implemented")
        }
    }
}

enum class Ratio() : Parcelable {

    RATIO_4_3, RATIO_16_9, RATIO_AUTO;
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ratio> {


        override fun newArray(size: Int): Array<Ratio?> {
            return arrayOfNulls(size)
        }

        override fun createFromParcel(p0: Parcel?): Ratio {
            TODO("Not yet implemented")
        }
    }

}
@SuppressLint("ParcelCreator")
class VideoOptions() : Parcelable {
    var videoBitrate : Int? = null
    var audioBitrate : Int? = null
    var videoFrameRate : Int? = null
    var videoDurationLimitInSeconds = 10

    constructor(parcel: Parcel) : this() {
        videoBitrate = parcel.readValue(Int::class.java.classLoader) as? Int
        audioBitrate = parcel.readValue(Int::class.java.classLoader) as? Int
        videoFrameRate = parcel.readValue(Int::class.java.classLoader) as? Int
        videoDurationLimitInSeconds = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(videoBitrate)
        parcel.writeValue(audioBitrate)
        parcel.writeValue(videoFrameRate)
        parcel.writeInt(videoDurationLimitInSeconds)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VideoOptions> {
        override fun createFromParcel(parcel: Parcel): VideoOptions {
            return VideoOptions(parcel)
        }

        override fun newArray(size: Int): Array<VideoOptions?> {
            return arrayOfNulls(size)
        }
    }

}

class ModelList(
    var list: ArrayList<Img> = ArrayList(),
    var selection: ArrayList<Img> = ArrayList()
)