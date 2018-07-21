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

    private val fetchPlaylistSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val showProgressDialogSubject: PublishSubject<Unit> = PublishSubject.create()
    private val hideProgressSubject: PublishSubject<Boolean> = PublishSubject.create()
    private val showErrorMessageSubject: PublishSubject<String> = PublishSubject.create()
    private val openVideoDetailScreenSubject: PublishSubject<Video> = PublishSubject.create()
    private val playlistDataSubject: BehaviorSubject<Playlist> = BehaviorSubject.create()
    private val childItemClickSubject: PublishSubject<Pair<Int, Int>> = PublishSubject.create()

    private var disposables = CompositeDisposable()

    init {
        bindCall(fetchPlaylistSubject.subscribe(::requestPlaylist))

        bindCall(fetchPlaylistSubject
            .filter { !it }
            .map { Unit }
            .subscribe(showProgressDialogSubject::onNext))

        bindCall(childItemClickSubject
            .withLatestFrom(playlistDataSubject,
                BiFunction<Pair<Int, Int>, Playlist, Video> { itemClick, playlists ->
                    playlists.get(itemClick)
                })
            .subscribe(openVideoDetailScreenSubject::onNext))
    }

    private fun requestPlaylist(isSwipeRefresh: Boolean) {
        bindCall(repository
            .getPlayList()
            .subscribeWith(object : DisposableSingleObserver<Playlist>() {
                override fun onSuccess(data: Playlist) {
                    playlistDataSubject.onNext(data)
                    hideProgressSubject.onNext(isSwipeRefresh)
                }

                override fun onError(e: Throwable) {
                    showErrorMessageSubject.onNext(e.message)
                    hideProgressSubject.onNext(isSwipeRefresh)
                }
            }))
    }

    fun clear() = disposables.clear()

    override fun showProgressDialog(): Observable<Unit> = showProgressDialogSubject

    override fun hideProgressDialog(): Observable<Boolean> = hideProgressSubject

    override fun showErrorMessage(): Observable<String> = showErrorMessageSubject

    override fun openVideoDetailScreen(): Observable<Video> = openVideoDetailScreenSubject

    override fun setPlaylist(): Observable<Playlist> = playlistDataSubject

    override fun fetchPlaylist(isSwipeRefresh: Boolean) = fetchPlaylistSubject.onNext(isSwipeRefresh)

    override fun onChildItemClick(groupPosition: Int, childPosition: Int) {
        childItemClickSubject.onNext(Pair(groupPosition, childPosition))
    }

    private fun bindCall(disposable: Disposable): Boolean = disposables.add(disposable)
}

fun Playlist.get(itemClick: Pair<Int, Int>): Video? =
    this.playlists[itemClick.first].listItems?.get(itemClick.second)
