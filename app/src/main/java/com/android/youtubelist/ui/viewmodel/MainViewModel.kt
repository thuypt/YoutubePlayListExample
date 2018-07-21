package com.android.youtubelist.ui.viewmodel

import android.arch.lifecycle.ViewModel
import com.android.youtubelist.model.Playlist
import com.android.youtubelist.model.Video
import com.android.youtubelist.network.ApiService
import com.android.youtubelist.ui.viewmodel.input.MainInputs
import com.android.youtubelist.ui.viewmodel.output.MainOutputs
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.PublishSubject

class MainViewModel(val repository: ApiService) : ViewModel(), MainOutputs, MainInputs {

    val outputs = this
    val inputs = this

    private val showProgressSubject: PublishSubject<Unit> = PublishSubject.create()
    private val hideProgressSubject: PublishSubject<Unit> = PublishSubject.create()
    private val showEmptyStateSubject: PublishSubject<Unit> = PublishSubject.create()
    private val showErrorMessageSubject: PublishSubject<String> = PublishSubject.create()
    private val openVideoDetailScreenSubject: PublishSubject<Video> = PublishSubject.create()

    private lateinit var compositeDisposable: CompositeDisposable

    init {
        compositeDisposable = CompositeDisposable()
        bindCall(repository
            .getPlayList()
            .subscribeWith(object : DisposableSingleObserver<Playlist>() {

                override fun onSuccess(data: Playlist) {
                }

                override fun onError(e: Throwable) {
                    showErrorMessageSubject.onNext("Error")
                }
            }))

    }

    override fun showProgressDialog(): Observable<Unit> = showProgressSubject

    override fun hideProgressDialog(): Observable<Unit> = hideProgressSubject

    override fun showEmptyState(): Observable<Unit> = showEmptyStateSubject

    override fun showErrorMessage(): Observable<String> = showErrorMessageSubject

    override fun openVideoDetailScreen(): Observable<Video> = openVideoDetailScreenSubject

    private fun bindCall(disposable: Disposable): Boolean = compositeDisposable.add(disposable)
}