package com.android.youtubelist.ui.viewmodel

import com.android.youtubelist.model.CategoryVideo
import com.android.youtubelist.model.Playlist
import com.android.youtubelist.model.Video
import com.android.youtubelist.network.ApiService
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.mockito.runners.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest {
    @Before
    fun setUp() {
    }

    @Test
    fun `when fetch playlist error, it shows error dialog`() {
        val apiServiceError = mock(ApiService::class.java)
        `when`(apiServiceError.getPlayList()).thenReturn(Single.error(Throwable("Server Error")))
        val viewModel = MainViewModel(apiServiceError)
        val showProgressDialog = viewModel.outputs.showProgressDialog().test()
        val hideProgressDialog = viewModel.outputs.hideProgressDialog().test()
        val setPlaylist = viewModel.outputs.setPlaylist().test()
        val showErrorMessage = viewModel.outputs.showErrorMessage().test()
        val openVideoDetailScreen = viewModel.outputs.openVideoDetailScreen().test()

        viewModel.inputs.fetchPlaylist(false)

        showProgressDialog.assertValueCount(1)
        hideProgressDialog.assertValueCount(1).assertValue { !it }
        showErrorMessage.assertValueCount(1).assertValue { it == "Server Error" }
        setPlaylist.assertValueCount(0)
        openVideoDetailScreen.assertValueCount(0)
    }

    @Test
    fun `when fetch playlist successful, it shows set playlist`() {
        val apiServiceSuccessful = mock(ApiService::class.java)
        val playlist = Playlist(ArrayList())
        `when`(apiServiceSuccessful.getPlayList()).thenReturn(Single.just(playlist))
        val viewModel = MainViewModel(apiServiceSuccessful)
        val showProgressDialog = viewModel.outputs.showProgressDialog().test()
        val hideProgressDialog = viewModel.outputs.hideProgressDialog().test()
        val setPlaylist = viewModel.outputs.setPlaylist().test()
        val showErrorMessage = viewModel.outputs.showErrorMessage().test()
        val openVideoDetailScreen = viewModel.outputs.openVideoDetailScreen().test()

        viewModel.inputs.fetchPlaylist(false)

        showProgressDialog.assertValueCount(1)
        hideProgressDialog.assertValueCount(1).assertValue { !it }
        showErrorMessage.assertValueCount(0)
        setPlaylist.assertValueCount(1).assertValue { it == playlist }
        openVideoDetailScreen.assertValueCount(0)
    }

    @Test
    fun `When item click, it calls open video detail screen`() {
        val apiServiceSuccessful = mock(ApiService::class.java)
        val video = Video("video_title", "video_link", "video_thumb")
        val category = CategoryVideo("category_title", arrayListOf(video, video, video))
        val playlist = Playlist(arrayListOf(category, category))
        `when`(apiServiceSuccessful.getPlayList()).thenReturn(Single.just(playlist))

        val viewModel = MainViewModel(apiServiceSuccessful)
        val showProgressDialog = viewModel.outputs.showProgressDialog().test()
        val hideProgressDialog = viewModel.outputs.hideProgressDialog().test()
        val setPlaylist = viewModel.outputs.setPlaylist().test()
        val showErrorMessage = viewModel.outputs.showErrorMessage().test()
        val openVideoDetailScreen = viewModel.outputs.openVideoDetailScreen().test()

        viewModel.fetchPlaylist(false)

        viewModel.inputs.onChildItemClick(0, 2)
        showProgressDialog.assertValueCount(1)
        hideProgressDialog.assertValueCount(1).assertValue { !it }
        showErrorMessage.assertValueCount(0)
        setPlaylist.assertValueCount(1)
        openVideoDetailScreen
            .assertValueCount(1)
            .assertValue { it == video }
    }

    @Test
    fun `When swipe to refresh, it calls load data`() {
        val apiServiceSuccessful = mock(ApiService::class.java)
        val playlist = Playlist(ArrayList())
        `when`(apiServiceSuccessful.getPlayList()).thenReturn(Single.just(playlist))
        val viewModel = MainViewModel(apiServiceSuccessful)

        val showProgressDialog = viewModel.outputs.showProgressDialog().test()
        val hideProgressDialog = viewModel.outputs.hideProgressDialog().test()
        val setPlaylist = viewModel.outputs.setPlaylist().test()
        val showErrorMessage = viewModel.outputs.showErrorMessage().test()
        val openVideoDetailScreen = viewModel.outputs.openVideoDetailScreen().test()

        viewModel.inputs.fetchPlaylist(true)
        showProgressDialog.assertValueCount(0)
        hideProgressDialog.assertValueCount(1).assertValue { it }
        showErrorMessage.assertValueCount(0)
        setPlaylist.assertValueCount(1)
        openVideoDetailScreen.assertValueCount(0)
    }
}