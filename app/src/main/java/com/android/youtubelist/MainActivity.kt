package com.android.youtubelist

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.android.youtubelist.model.Playlist
import com.android.youtubelist.network.ApiClient
import com.android.youtubelist.network.ApiService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadPlaylist()
    }

    private fun loadPlaylist() {
        val apiService = ApiClient.getClient()!!.create(ApiService::class.java)
        //todo add to disposable
        apiService.getPlayList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<Playlist>() {

                override fun onSuccess(data: Playlist) {
                }

                override fun onError(e: Throwable) {
                    Log.e("test", "error " + e)
                }
            })
    }
}
