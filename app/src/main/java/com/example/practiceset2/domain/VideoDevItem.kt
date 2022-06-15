package com.example.practiceset2.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoDevItem(
    val title: String,
    val desc: String,
    val url: String,
    val updated: String,
    val thumbnail: String,
):Parcelable
