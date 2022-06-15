package com.example.practiceset2.local.devbytes

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VideoCacheDao {

    @Query("SELECT * FROM videoCacheTable")
    fun getAllDevBytes(): LiveData<List<VideoDevCache>>

    @Query("SELECT * FROM videoCacheTable")
    fun getAllDevBytesNoLiveData(): List<VideoDevCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideoCaches(videoDevCaches: List<VideoDevCache>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVideoCache(videoDevCache: VideoDevCache): Long

    @Query("DELETE FROM videoCacheTable")
    fun deleteAllVideos()
}