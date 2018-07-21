package com.android.youtubelist.network

import com.android.youtubelist.model.Playlist
import io.reactivex.Single
import retrofit2.http.GET

interface ApiService {
    @GET("playlists")
    fun getPlayList(): Single<Playlist>
}