package com.android.youtubelist.model

import android.os.Parcel
import android.os.Parcelable

data class Video(val title: String?,
                 val link: String?,
                 val thumb: String?) : Parcelable {
    private val youtubeBaseUrl = "https://www.youtube.com/watch?v="
    val id
        get() =
            if (link != null && link.contains(youtubeBaseUrl))
                link.replace(youtubeBaseUrl, "")
            else ""

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(title)
        parcel.writeString(link)
        parcel.writeString(thumb)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Video> {
        override fun createFromParcel(parcel: Parcel): Video {
            return Video(parcel)
        }

        override fun newArray(size: Int): Array<Video?> {
            return arrayOfNulls(size)
        }
    }
}
