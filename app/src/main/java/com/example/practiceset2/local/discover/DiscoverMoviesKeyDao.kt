package com.example.practiceset2.local.discover

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface DiscoverMoviesKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<DiscoverRemoteKey>)

    @Query("SELECT * FROM discoverMovieRemoteKeyTable WHERE repoId = :repoId")
    suspend fun remoteKeysId(repoId: Long): DiscoverRemoteKey?

    @Query("SELECT * FROM discoverMovieRemoteKeyTable ORDER BY repoId LIMIT 1")
    suspend fun getFirstMovieKey(): DiscoverRemoteKey?

    @Query("SELECT * FROM discoverMovieRemoteKeyTable")
    suspend fun getAllRemoteKeys(): List<DiscoverRemoteKey>

    @Query("DELETE FROM discoverMovieRemoteKeyTable")
    suspend fun clearRemoteKeys()
}