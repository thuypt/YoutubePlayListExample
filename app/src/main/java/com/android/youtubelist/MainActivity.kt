package com.android.youtubelist

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
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
    private lateinit var compositeDisposable: CompositeDisposable
    private lateinit var viewModel: MainViewModel
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initViewModel()

        viewModel.inputs.fetchPlaylist()
    }

    private fun initViews() {
        categoryAdapter = CategoryAdapter(this)
        categoryExpandList.apply {
            setChildIndicator(null)
            setAdapter(categoryAdapter)
            setChildDivider(ContextCompat.getDrawable(this@MainActivity, android.R.color.white))
            setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                viewModel.inputs.onChildItemClick(groupPosition, childPosition)
                false
            }
        }
    }

    private fun initViewModel() {
        compositeDisposable = CompositeDisposable()
        viewModel = ViewModelProviders.of(this, MainViewModelFactory())
            .get(MainViewModel::class.java)
        bindCall(viewModel.outputs.showErrorMessage().subscribe(::showErrorMessage))
        bindCall(viewModel.outputs.showProgressDialog().subscribe(::showProgressDialog))
        bindCall(viewModel.outputs.hideProgressDialog().subscribe(::hideProgressDialog))
        bindCall(viewModel.outputs.openVideoDetailScreen().subscribe(::openVideoDetailScreen))
        bindCall(viewModel.outputs.setPlaylist().subscribe(::setPlaylist))
    }

    private fun setPlaylist(list: Playlist) {
        categoryAdapter.clearAndAddAll(list.playlists)
        categoryAdapter.notifyDataSetChanged()
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this@MainActivity).create().apply {
            setMessage(message)
            setButton(AlertDialog.BUTTON_NEUTRAL, "OK") { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun showProgressDialog(unit: Unit) {
        categoryExpandList.visibility = View.GONE
        progressbar.visibility = View.VISIBLE
    }

    private fun hideProgressDialog(unit: Unit) {
        progressbar.visibility = View.GONE
        categoryExpandList.visibility = View.VISIBLE
    }

    private fun openVideoDetailScreen(video: Video) {
        val intent = Intent(this@MainActivity, VideoDetailActivity::class.java)
            .apply { putExtra(VideoDetailActivity.VIDEO, video) }
        startActivity(intent)
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    private fun bindCall(disposable: Disposable): Boolean = compositeDisposable.add(disposable)
}
