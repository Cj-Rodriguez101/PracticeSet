package com.example.practiceset2.local.goRest

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.practiceset2.local.discover.DiscoverRemoteKey

@Dao
interface GoRestRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<GoRestRemoteKey>)

    @Query("SELECT * FROM goRestRemoteKeyTable WHERE repoId = :repoId")
    suspend fun remoteKeysId(repoId: Long): GoRestRemoteKey?

    @Query("SELECT * FROM goRestRemoteKeyTable ORDER BY repoId LIMIT 1")
    suspend fun getFirstMovieKey(): GoRestRemoteKey?

    @Query("SELECT * FROM goRestRemoteKeyTable")
    suspend fun getAllRemoteKeys(): List<GoRestRemoteKey>

    @Query("DELETE FROM goRestRemoteKeyTable")
    suspend fun clearRemoteKeys()
}