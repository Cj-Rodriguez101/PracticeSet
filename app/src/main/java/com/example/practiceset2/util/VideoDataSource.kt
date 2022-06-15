package com.example.practiceset2.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.practiceset2.domain.VideoDevItem

interface VideoDataSource {

    fun observeVideos(): LiveData<List<VideoDevItem>> = liveData { listOf<VideoDevItem>() }

    suspend fun getVideo(): Result<List<VideoDevItem>> = Result.Success(data = listOf())

    suspend fun refreshVideos(){}

    suspend fun observeVideo(taskId: String): LiveData<Result<VideoDevItem>>
    = liveData { Result.Success(data = VideoDevItem("", "", "",
        "", "")) }
}