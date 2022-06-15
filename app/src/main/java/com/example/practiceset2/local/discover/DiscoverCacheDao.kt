package com.example.practiceset2.local.discover

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiscoverCacheDao {

    @Query("SELECT * FROM discoverCacheTable")
    fun getMovieData(): LiveData<List<DiscoverDevCache>>

    @Query("SELECT * FROM discoverCacheTable")
    fun getMovieDataPaging(): PagingSource<Int, DiscoverDevCache>

    @Query("DELETE FROM discoverCacheTable")
    fun clearAllMovieData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movieList: List<DiscoverDevCache>)
}