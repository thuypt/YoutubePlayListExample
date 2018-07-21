package com.android.youtubelist

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.android.youtubelist.model.Video
import com.android.youtubelist.ui.viewmodel.MainViewModel
import com.android.youtubelist.ui.viewmodel.factory.MainViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var compositeDisposable: CompositeDisposable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()
        initViewModel()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders
            .of(this, MainViewModelFactory())
            .get(MainViewModel::class.java)

        bindCall(viewModel.outputs.showEmptyState().subscribe(::showEmptyState))
        bindCall(viewModel.outputs.showErrorMessage().subscribe(::showErrorMessage))
        bindCall(viewModel.outputs.showProgressDialog().subscribe(::showProgressDialog))
        bindCall(viewModel.outputs.hideProgressDialog().subscribe(::hideProgressDialog))
        bindCall(viewModel.outputs.openVideoDetailScreen().subscribe(::openVideoDetailScreen))

    }

    private fun showEmptyState(unit: Unit) {

    }

    private fun showErrorMessage(message: String) {

    }

    private fun showProgressDialog(unit: Unit) {

    }

    private fun hideProgressDialog(unit: Unit) {

    }

    private fun openVideoDetailScreen(video: Video) {

    }

    private fun bindCall(disposable: Disposable): Boolean = compositeDisposable.add(disposable)
}


