package com.android.youtubelist.network

import com.android.youtubelist.model.Playlist
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiService {
    @Headers("Cache-Control: no-cache")
    @GET("playlists")
    fun getPlayList(): Single<Playlist>
}