package com.example.practiceset2.local.devbytes

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.practiceset2.domain.VideoDevItem
import com.example.practiceset2.util.DateTimeUtil
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "videoCacheTable")
data class VideoDevCache(
    @PrimaryKey
    val url: String,
    val title: String,
    val desc: String,
    val updated: Double,
    val thumbnail: String,
): Parcelable

fun VideoDevCache.toVideoDevItem(): VideoDevItem{
    return VideoDevItem(
        title = title, desc = desc, url = url, updated = DateTimeUtil.toLocalDateString(updated),
        thumbnail = thumbnail
    )
}
fun List<VideoDevCache>.toVideoDevItemList(): List<VideoDevItem>{
    return this.map { it.toVideoDevItem() }
}