package com.example.practiceset2.local.goRest

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goRestRemoteKeyTable")
data class GoRestRemoteKey(@PrimaryKey val repoId: Long, val prevKey: Int?, val nextKey: Int?){

    override fun toString(): String {
        return "GoRestRemoteKey(repoId=$repoId, prevKey=$prevKey, nextKey=$nextKey)"
    }
}