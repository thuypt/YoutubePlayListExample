package com.android.youtubelist.ui.viewmodel.input

interface MainInputs {
    fun onChildItemClick(groupPosition: Int, childPosition: Int)

    fun fetchPlaylist(isSwipeRefresh: Boolean)
}
