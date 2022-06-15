package com.example.practiceset2.local.discover

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "discoverMovieRemoteKeyTable")
data class DiscoverRemoteKey(@PrimaryKey val repoId: Long, val prevKey: Int?, val nextKey: Int?){

    override fun toString(): String {
        return "DiscoverRemoteKeys(repoId=$repoId, prevKey=$prevKey, nextKey=$nextKey)"
    }
}