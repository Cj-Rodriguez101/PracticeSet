package com.example.practiceset2.network

import android.os.Parcelable
import com.example.practiceset2.domain.MovieDevItem
import com.example.practiceset2.local.discover.DiscoverDevCache
import com.example.practiceset2.local.top_rated.MovieDevCache
import com.example.practiceset2.util.DateTimeUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class MovieNetworkDtoList(
    val results: List<MovieNetworkDto>
): Parcelable{
    override fun toString(): String {
        return "MovieNetworkDtoList(videos=${results.map { it.title }})"
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class MovieNetworkDto(
    val id: Int,
    @Json(name = "original_title") val title: String,
    @Json(name = "original_language") val language: String,
    val overview: String,
    val popularity: Double,
    val release_date: String,
    val poster_path: String
): Parcelable

fun MovieNetworkDto.toMovieDevItem(): MovieDevItem{
    return MovieDevItem(
        id, title, language, overview, popularity, release_date, poster_path
    )
}

fun List<MovieNetworkDto>.toMovieDevItemList(): List<MovieDevItem>{
    return this.map {
        it.toMovieDevItem()
    }
}

fun MovieNetworkDtoList.toMovieDevItemList(): List<MovieDevItem>{
    return this.results.map { it.toMovieDevItem() }
}

fun MovieNetworkDtoList.toMovieDevCacheList(): List<MovieDevCache> {
    return this.results.map { it.toMovieDevCache() }
}

fun MovieNetworkDtoList.toDiscoverMovieDevCacheList(): List<DiscoverDevCache> {
    return this.results.map { it.toDiscoverMovieDevCache() }
}

fun MovieNetworkDto.toMovieDevCache(): MovieDevCache {
    return MovieDevCache(
        remoteId = id.toLong(), title = title, language = language, overview = overview,
        popularity = popularity, release_date = DateTimeUtil.toEpochMilliMovieDate(release_date),
        poster_path = poster_path
    )
}

fun MovieNetworkDto.toDiscoverMovieDevCache(): DiscoverDevCache {
    return DiscoverDevCache(
        remoteId = id.toLong(), title = title, language = language, overview = overview,
        popularity = popularity, release_date = 1.0,
        poster_path = poster_path
    )
}

fun List<MovieNetworkDto>.toMovieDevCacheList(): List<MovieDevCache>{
    return this.map {
        it.toMovieDevCache()
    }
}
