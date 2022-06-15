package com.example.practiceset2.domain

import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieDevItem(
    val id: Int,
    val title: String,
    val language: String,
    val overview: String,
    val popularity: Double,
    val release_date: String,
    val poster_path: String
): Parcelable{

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MovieDevItem

        if (id != other.id) return false
        if (title != other.title) return false
        if (language != other.language) return false
        if (overview != other.overview) return false
        if (popularity != other.popularity) return false
        if (release_date != other.release_date) return false
        if (poster_path != other.poster_path) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + title.hashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + overview.hashCode()
        result = 31 * result + popularity.hashCode()
        result = 31 * result + release_date.hashCode()
        result = 31 * result + poster_path.hashCode()
        return result
    }

    override fun toString(): String {
        return "MovieDevItem(id=$id, title='$title', language='$language', overview='$overview', popularity=$popularity, release_date='$release_date', poster_path='$poster_path')"
    }


}
