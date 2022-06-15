package com.example.practiceset2.local.top_rated

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movieRemoteKeyTable")
data class MovieRemoteKey(@PrimaryKey val repoId: Long, val prevKey: Int?, val nextKey: Int?){

    override fun toString(): String {
        return "MovieRemoteKey(repoId=$repoId, prevKey=$prevKey, nextKey=$nextKey)"
    }
}