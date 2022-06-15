package com.example.practiceset2.local.discover

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.practiceset2.domain.MovieDevItem
import com.example.practiceset2.util.DateTimeUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "discoverCacheTable")
data class DiscoverDevCache(
    @PrimaryKey(autoGenerate = true)
    val id: Long=0,
    val remoteId: Long=0,
    val title: String,
    val language: String,
    val overview: String,
    val popularity: Double,
    val release_date: Double,
    val poster_path: String
) : Parcelable

fun DiscoverDevCache.toMovieDevItem(): MovieDevItem {
    return MovieDevItem(
        remoteId.toInt(), title, language, overview, popularity, DateTimeUtil.toLocalDateString(release_date), poster_path
    )
}