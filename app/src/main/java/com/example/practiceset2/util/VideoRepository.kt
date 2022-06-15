package com.example.practiceset2.util

import androidx.lifecycle.LiveData
import com.example.practiceset2.domain.VideoDevItem

interface VideoRepository {

    fun observeVideos(): LiveData<List<VideoDevItem>>

    suspend fun refreshVideos()
}