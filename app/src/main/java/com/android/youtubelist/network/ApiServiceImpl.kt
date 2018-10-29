package com.android.youtubelist.network

import com.android.youtubelist.model.Playlist
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class ApiServiceImpl @Inject constructor() : ApiService {

    private val apiService: ApiService = ApiClient.getClient()
        .create(ApiService::class.java)

    override fun getPlayList(): Single<Playlist> {
        return apiService
            .getPlayList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
