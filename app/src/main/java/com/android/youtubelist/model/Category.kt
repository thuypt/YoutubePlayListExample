package com.android.youtubelist.model

import com.google.gson.annotations.SerializedName

data class Category(
    @SerializedName("list_title") val listTitle: String?,
    @SerializedName("list_items") val listItems: ArrayList<Video>?)