package com.android.youtubelist.ui.activity

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AbsListView
import com.android.youtubelist.R
import com.android.youtubelist.adapter.PlaylistAdapter
import com.android.youtubelist.espressoidling.EspressoIdlingResource
import com.android.youtubelist.model.Playlist
import com.android.youtubelist.model.Video
import com.android.youtubelist.ui.viewmodel.MainViewModel
import com.android.youtubelist.ui.viewmodel.factory.MainViewModelFactory
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var disposables: CompositeDisposable
    private lateinit var viewModel: MainViewModel
    private lateinit var playlistAdapter: PlaylistAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        initViewModel()

        viewModel.inputs.fetchPlaylist(false)
    }

    private fun initViews() {
        playlistAdapter = PlaylistAdapter(this@MainActivity)
        mainExpandableList.apply {
            setChildIndicator(null)
            setChildDivider(ContextCompat.getDrawable(this@MainActivity, android.R.color.white))
            setAdapter(playlistAdapter)
            setOnChildClickListener { _, _, groupPosition, childPosition, _ ->
                viewModel.inputs.onChildItemClick(groupPosition, childPosition)
                false
            }
        }

        mainSwipeReFresh.setOnRefreshListener(this)
        solveConflictScrollViews()
    }

    private fun initViewModel() {
        disposables = CompositeDisposable()
        viewModel = ViewModelProviders.of(this, MainViewModelFactory())
            .get(MainViewModel::class.java)
        bindCall(viewModel.outputs.showErrorMessage().subscribe(::showErrorMessage))
        bindCall(viewModel.outputs.showProgressDialog().subscribe(::showProgressDialog))
        bindCall(viewModel.outputs.hideProgressDialog().subscribe(::hideLoading))
        bindCall(viewModel.outputs.openVideoDetailScreen().subscribe(::openVideoDetailScreen))
        bindCall(viewModel.outputs.setPlaylist().subscribe(::setPlaylist))
    }

    override fun onRefresh() {
        viewModel.inputs.fetchPlaylist(true)
        EspressoIdlingResource.increment()
    }

    private fun setPlaylist(list: Playlist) {
        playlistAdapter.clearAndAddAll(list.playlists)
        playlistAdapter.notifyDataSetChanged()
    }

    private fun showErrorMessage(message: String) {
        AlertDialog.Builder(this@MainActivity).create().apply {
            setTitle(getString(R.string.title_error_dialog))
            setMessage(message)
            setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok)) { dialog, _ -> dialog.dismiss() }
            show()
        }
    }

    private fun showProgressDialog(unit: Unit) {
        mainExpandableList.visibility = View.GONE
        mainProgressBar.visibility = View.VISIBLE
        EspressoIdlingResource.increment()
    }

    /**
     * Hide ProgressBar or stop refreshing SwipeToRefresh when request finish
     * @param isSwipeRefresh true if request by swipe
     * false otherwise
     */
    private fun hideLoading(isSwipeRefresh: Boolean) {
        EspressoIdlingResource.decrement()
        mainProgressBar.visibility = View.GONE
        mainExpandableList.visibility = View.VISIBLE
        if (isSwipeRefresh) {
            mainSwipeReFresh.isRefreshing = false
        }
    }

    private fun openVideoDetailScreen(video: Video) =
        startActivity(Intent(this@MainActivity, VideoDetailActivity::class.java)
            .apply { putExtra(VideoDetailActivity.VIDEO, video) })

    override fun onDestroy() {
        viewModel.clear()
        disposables.clear()
        super.onDestroy()
    }

    private fun bindCall(disposable: Disposable): Boolean = this.disposables.add(disposable)

    /**
     * ExpandableListView scroll has a conflict with SwipeRefreshView when scroll it up
     * This is tricky to solve the issue
     * basically control disable/enable SwipeRefreshView regarding to ExpandableListView scroll position
     */
    private fun solveConflictScrollViews() {
        mainSwipeReFresh.isEnabled = false
        mainExpandableList.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}

            override fun onScroll(view: AbsListView, firstVisibleItem: Int,
                                  visibleItemCount: Int, totalItemCount: Int) {
                val topRowVerticalPosition =
                    if (mainExpandableList == null || mainExpandableList.childCount == 0) 0
                    else mainExpandableList.getChildAt(0).top
                mainSwipeReFresh.isEnabled = firstVisibleItem == 0 && topRowVerticalPosition >= 0
            }
        })
    }
}
