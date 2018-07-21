package com.android.youtubelist.model

import junit.framework.Assert.assertTrue
import org.junit.Test

class VideoTest {
    @Test
    fun `when video link is invalid format, it returns empty video id`() {
        val video = Video("video_title", "video_link", "video_thumb")
        assertTrue(video.id == "")
    }

    @Test
    fun `when video link is empty, it returns empty video id`() {
        val video = Video("video_title", null, "video_thumb")
        assertTrue(video.id == "")
    }

    @Test
    fun `when video link is valid, it returns proper video id`() {
        val video = Video("video_title",
            "https://www.youtube.com/watch?v=aJOTlE1K90k",
            "video_thumb")
        assertTrue(video.id == "aJOTlE1K90k")
    }
}
