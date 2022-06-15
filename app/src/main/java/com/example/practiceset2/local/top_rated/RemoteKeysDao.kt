package com.example.practiceset2.local.top_rated

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface RemoteKeysDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MovieRemoteKey>)

    @Query("SELECT * FROM movieRemoteKeyTable WHERE repoId = :repoId")
    suspend fun remoteKeysId(repoId: Long): MovieRemoteKey?

    @Query("SELECT * FROM movieRemoteKeyTable ORDER BY repoId LIMIT 1")
    suspend fun getFirstMovieKey(): MovieRemoteKey?

    @Query("SELECT * FROM movieRemoteKeyTable")
    suspend fun getAllRemoteKeys(): List<MovieRemoteKey>

    @Query("DELETE FROM movieRemoteKeyTable")
    suspend fun clearRemoteKeys()
}