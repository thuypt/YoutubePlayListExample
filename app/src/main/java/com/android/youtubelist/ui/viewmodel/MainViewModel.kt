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
import io.reactivex.functions.BiFunction
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class MainViewModel(val repository: ApiService) : ViewModel(), MainOutputs, MainInputs {

    val outputs = this
    val inputs = this

    private val fetchPlaylistSubject: PublishSubject<Unit> = PublishSubject.create()
    private val showProgressDialogSubject: PublishSubject<Unit> = PublishSubject.create()
    private val hideProgressSubject: PublishSubject<Unit> = PublishSubject.create()
    private val showErrorMessageSubject: PublishSubject<String> = PublishSubject.create()
    private val openVideoDetailScreenSubject: PublishSubject<Video> = PublishSubject.create()
    private val playlistSubject: BehaviorSubject<Playlist> = BehaviorSubject.create()
    private val childItemClickSubject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()

    private var compositeDisposable = CompositeDisposable()

    init {
        bindCall(fetchPlaylistSubject
            .subscribe {
                showProgressDialogSubject.onNext(Unit)
                requestPlaylist()
            })

        bindCall(childItemClickSubject
            .withLatestFrom(playlistSubject,
                BiFunction<Pair<Int, Int>, Playlist, Video> { itemClick, list ->
                    list.playlists[itemClick.first].listItems?.get(itemClick.second)
                })
            .subscribe(openVideoDetailScreenSubject::onNext))
    }

    private fun requestPlaylist() {
        bindCall(repository
            .getPlayList()
            .subscribeWith(object : DisposableSingleObserver<Playlist>() {
                override fun onSuccess(data: Playlist) {
                    playlistSubject.onNext(data)
                    hideProgressSubject.onNext(Unit)
                }

                override fun onError(e: Throwable) {
                    showErrorMessageSubject.onNext(e.message)
                    hideProgressSubject.onNext(Unit)
                }
            }))
    }

    override fun showProgressDialog(): Observable<Unit> = showProgressDialogSubject

    override fun hideProgressDialog(): Observable<Unit> = hideProgressSubject

    override fun showErrorMessage(): Observable<String> = showErrorMessageSubject

    override fun openVideoDetailScreen(): Observable<Video> = openVideoDetailScreenSubject

    override fun setPlaylist(): Observable<Playlist> = playlistSubject

    override fun fetchPlaylist() = fetchPlaylistSubject.onNext(Unit)

    override fun onChildItemClick(groupPosition: Int, childPosition: Int) {
        childItemClickSubject.onNext(Pair(groupPosition, childPosition))
    }

    private fun bindCall(disposable: Disposable): Boolean = compositeDisposable.add(disposable)
}
