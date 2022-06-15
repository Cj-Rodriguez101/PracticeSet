package com.example.practiceset2.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.example.practiceset2.domain.VideoDevItem
import com.example.practiceset2.local.devbytes.VideoCacheDao
import com.example.practiceset2.local.devbytes.VideoDevCache
import com.example.practiceset2.local.devbytes.toVideoDevItemList
import com.example.practiceset2.local.top_rated.MovieCacheDao
import com.example.practiceset2.local.top_rated.RemoteKeysDao
import com.example.practiceset2.util.Result
import com.example.practiceset2.util.VideoDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VideoLocalDataSource internal constructor(
    private val videoDao: VideoCacheDao,
    val remoteKeysDao: RemoteKeysDao,
    val movieCacheDao: MovieCacheDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : VideoDataSource {
    override fun observeVideos(): LiveData<List<VideoDevItem>> {
        return videoDao.getAllDevBytes().map { it.toVideoDevItemList() }
        //Log.e("unit", "${unit?.joinToString(",")?:""}")
//       return videoDao.getAllDevBytes().map {
//            Result.Success(it.toVideoDevItemList())
//        }
    }

    override suspend fun getVideo(): Result<List<VideoDevItem>> {
        TODO("Not yet implemented")
    }

    override suspend fun refreshVideos() {
        TODO("Not yet implemented")
    }

    override suspend fun observeVideo(taskId: String): LiveData<Result<VideoDevItem>> {
        TODO("Not yet implemented")
    }

    fun insertVideos(videoDevCacheList: List<VideoDevCache>){
        CoroutineScope(ioDispatcher).launch {
            videoDao.insertVideoCaches(videoDevCacheList)
        }
    }

//    fun insertVideosLoop(videoDevCacheList: List<VideoDevCache>){
//        CoroutineScope(ioDispatcher).launch {
//            for (item in videoDevCacheList){
//                val yes = videoDao.insertVideoCache(item)
//                Log.e("count", "${yes}")
//            }
//            //videoDao.insertVideoCaches(*videoDevCacheList.toTypedArray())
//        }
//    }

    fun deleteAllVideos(){
        CoroutineScope(ioDispatcher).launch {
            videoDao.deleteAllVideos()
        }
    }
    }