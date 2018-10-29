package com.android.youtubelist.ui.viewmodel

import android.arch.lifecycle.ViewModel
import com.android.youtubelist.model.CategoryVideo
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

class MainViewModel(val apiService: ApiService) : ViewModel(), MainOutputs, MainInputs {
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
                BiFunction<Pair<Int, Int>, Playlist, Video> { itemClick, playlist ->
                    playlist.get(itemClick)
                })
            .subscribe(openVideoDetailScreenSubject::onNext))
    }

    private fun requestPlaylist(isSwipeRefresh: Boolean) {
        bindCall(apiService
            .getPlayList()
            .subscribeWith(object : DisposableSingleObserver<Playlist>() {
                override fun onSuccess(data: Playlist) {
                    playlistDataSubject.onNext(data)
                    hideProgressSubject.onNext(isSwipeRefresh)
                }

                override fun onError(e: Throwable) {
                    // The server host are unavailable,
                    // we need to mock the response to keep the app running
                    val mockPlaylist = createMockPlaylist()
                    playlistDataSubject.onNext(mockPlaylist)
                    hideProgressSubject.onNext(isSwipeRefresh)

                    // showErrorMessageSubject.onNext(e.message)
                    // hideProgressSubject.onNext(isSwipeRefresh)
                }
            }))
    }

    private fun createMockPlaylist(): Playlist {
        val video = Video(title = "We Ask Kids How Trump is Doing",
            link = "https://youtu.be/XYviM5xevC8",
            thumb = "https://images.pexels.com/photos/67636/rose-blue-flower-rose-blooms-67636.jpeg?auto=compress&cs=tinysrgb&h=350")
        val category = CategoryVideo("kids and Trump", listOf(video, video, video))
        return Playlist(listOf(category, category, category))
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
