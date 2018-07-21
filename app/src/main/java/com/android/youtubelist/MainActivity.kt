package com.android.youtubelist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.android.youtubelist.adapter.CategoryAdapter
import com.android.youtubelist.model.Playlist
import com.android.youtubelist.model.Video
import com.android.youtubelist.ui.activity.VideoDetailActivity
import com.android.youtubelist.ui.viewmodel.MainViewModel
import com.android.youtubelist.ui.viewmodel.factory.MainViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var adapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        compositeDisposable = CompositeDisposable()
        initViewModel()
        initViews()
    }

    private fun initViews() {
        adapter = CategoryAdapter(this)
        categoryExpandList.setAdapter(adapter)
        categoryExpandList.setChildIndicator(null);
        categoryExpandList.setChildDivider(ContextCompat.getDrawable(this, android.R.color.white))
        categoryExpandList
            .setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                viewModel.inputs.onChildItemClick(groupPosition, childPosition)
                false
            }
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
        bindCall(viewModel.outputs.setPlaylist().subscribe(::setPlaylist))
    }

    private fun setPlaylist(list: Playlist) {
        adapter.clearAndAddAll(list.playlists)
        adapter.notifyDataSetChanged()
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
        val intent = Intent(this@MainActivity, VideoDetailActivity::class.java)
        intent.putExtra(VideoDetailActivity.VIDEO, video)
        startActivity(intent)
    }

    private fun bindCall(disposable: Disposable): Boolean = compositeDisposable.add(disposable)
}


