package com.example.practiceset2.util

import android.content.Context
import androidx.annotation.VisibleForTesting
import androidx.room.Room
import com.example.practiceset2.list.ListRepository
import com.example.practiceset2.local.VideoDatabase
import com.example.practiceset2.local.VideoLocalDataSource
import com.example.practiceset2.network.VideoNetworkDataSource

object ServiceLocator {

    private val lock = Any()
    private var database: VideoDatabase? = null
    @Volatile
    var videoRepository: ListRepository? = null
        @VisibleForTesting set

    fun provideListRepository(context: Context): ListRepository {
        synchronized(this) {
            return videoRepository ?: createVideoRepository(context)
        }
    }

    private fun createVideoRepository(context: Context): ListRepository {
        val database = database ?: createDataBase(context)
        val newRepo = ListRepository(database, VideoNetworkDataSource)
        videoRepository = newRepo
        return newRepo
    }

    private fun createVideoLocalDataSource(context: Context): VideoLocalDataSource {
        val database = database ?: createDataBase(context)
        return VideoLocalDataSource(database.videoDao(),
            database.remoteKeysDao(),
            database.movieCacheDao())
    }

    private fun createDataBase(context: Context): VideoDatabase {
        val result = Room.databaseBuilder(
            context.applicationContext,
            VideoDatabase::class.java, "Videos.db"
        ).fallbackToDestructiveMigration().build()
        database = result
        return result
    }
}