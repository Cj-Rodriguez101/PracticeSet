package com.example.practiceset2.network

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.example.practiceset2.domain.MovieDevItem
import com.example.practiceset2.domain.VideoDevItem
import com.example.practiceset2.util.Result
import com.example.practiceset2.util.VideoDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object VideoNetworkDataSource: VideoDataSource {

    private val videoService = VideoNetwork.devbytes
    val movieApiService = MovieNetwork.theMoveDb
    val goRestApiService = GoRestNetwork.theGoRestDb

    private val mutableLiveData = MutableLiveData<List<VideoDevItem>>()
    val liveData: LiveData<List<VideoDevItem>>
        get() = mutableLiveData

    suspend fun observeVideos2(): LiveData<List<VideoDevItem>> {
        val yes = videoService.getPlaylist().await()
        val success = yes.toVideoDevItemList()
        withContext(Dispatchers.Main){
            mutableLiveData.value = success
        }
        return liveData
    }

}