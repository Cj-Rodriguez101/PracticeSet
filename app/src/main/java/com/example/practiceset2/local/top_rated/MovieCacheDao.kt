package com.example.practiceset2.local.top_rated

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieCacheDao {

    @Query("SELECT * FROM movieCacheTable")
    fun getMovieData(): LiveData<List<MovieDevCache>>

    @Query("SELECT * FROM movieCacheTable")
    fun getMovieDataPaging(): PagingSource<Int, MovieDevCache>

    @Query("DELETE FROM movieCacheTable")
    fun clearAllMovieData()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMovie(movieList: List<MovieDevCache>)
}