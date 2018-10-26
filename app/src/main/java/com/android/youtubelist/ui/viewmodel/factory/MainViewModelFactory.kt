package com.android.youtubelist.ui.viewmodel.factory

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.android.youtubelist.network.ApiServiceImpl
import com.android.youtubelist.ui.viewmodel.MainViewModel

class MainViewModelFactory : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(repository = ApiServiceImpl) as T
    }
}