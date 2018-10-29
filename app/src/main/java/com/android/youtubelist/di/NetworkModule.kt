package com.android.youtubelist.di

import com.android.youtubelist.ui.activity.MainActivity
import dagger.Component

@Component
interface NetworkModule {

    fun poke(app: MainActivity)
}