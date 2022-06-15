package com.example.practiceset2.network

import android.os.Parcelable
import com.example.practiceset2.local.devbytes.VideoDevCache
import com.example.practiceset2.domain.VideoDevItem
import com.example.practiceset2.util.DateTimeUtil
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize



@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkVideosList(
    val videos: List<NetworkVideo>
): Parcelable{
    override fun toString(): String {
        return "NetworkVideosList(videos=${videos.map { it.title }})"
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class NetworkVideo(
    val title: String,
    @Json(name = "description") val desc: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
):Parcelable{

    override fun toString(): String {
        return "VideoNetwork(title='$title', desc='$desc', url='$url', updated='$updated', thumbnail='$thumbnail')"
    }
}

fun NetworkVideo.toVideoDevItem(): VideoDevItem{
    return VideoDevItem(
        title, desc, url, updated, thumbnail
    )
}

fun NetworkVideo.toVideoDevCache(): VideoDevCache {
    return VideoDevCache(
        title = title, desc = desc, url = url, updated = DateTimeUtil.toEpochMilliseconds(updated),
        thumbnail = thumbnail
    )
}
fun NetworkVideosList.toVideoDevItemList(): List<VideoDevItem>{
    return this.videos.map { it.toVideoDevItem() }
}

fun NetworkVideosList.toVideoDevCacheList(): List<VideoDevCache>{
    return this.videos.map { it.toVideoDevCache() }
}

fun VideoDevItem.toVideoCache(): VideoDevCache {
    return VideoDevCache(
        title = title, desc = desc, url = url, updated = DateTimeUtil.toEpochMilliseconds(updated),
        thumbnail = thumbnail
    )
}

fun List<VideoDevItem>.toVideoDevCacheList(): List<VideoDevCache>{
    return this.map { it.toVideoCache() }
}
