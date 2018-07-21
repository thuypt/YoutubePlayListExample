package com.android.youtubelist.ui.viewmodel.output

import com.android.youtubelist.model.Video
import io.reactivex.Observable

interface MainOutputs {
    fun showProgressDialog(): Observable<Unit>

    fun hideProgressDialog(): Observable<Unit>

    fun showEmptyState(): Observable<Unit>

    fun showErrorMessage(): Observable<String>

    fun openVideoDetailScreen(): Observable<Video>
}