package com.android.youtubelist.ui.activity

import android.os.Bundle
import com.android.youtubelist.R
import com.android.youtubelist.model.Video
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import kotlinx.android.synthetic.main.layout_video_detail_activity.*

class VideoDetailActivity : YouTubeBaseActivity(), YouTubePlayer.OnInitializedListener {
    companion object {
        @JvmStatic
        val VIDEO = "video"
    }

    private lateinit var video: Video

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_video_detail_activity)
        youtube_view.initialize(getString(R.string.youtube_developer_key), this)
        video = intent.getParcelableExtra(VIDEO) as Video
    }

    override fun onInitializationSuccess(provider: YouTubePlayer.Provider,
                                         youTubePlayer: YouTubePlayer, wasRestored: Boolean) {
        if (!wasRestored) {
            video.id?.let {
                youTubePlayer.apply {
                    loadVideo(video.id)
                    setFullscreen(true)
                    setShowFullscreenButton(false)
                }
            }
        }
    }

    override fun onInitializationFailure(provider: YouTubePlayer.Provider,
                                         youTubeInitializationResult: YouTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError) {
            youTubeInitializationResult.getErrorDialog(this, 1).show()
        } else {
            //todo another error here
        }
    }
}