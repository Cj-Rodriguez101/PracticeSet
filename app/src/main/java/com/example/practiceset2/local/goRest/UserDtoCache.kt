package com.example.practiceset2.local.goRest

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "userDtoTable")
@Parcelize
data class UserDtoCache(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val remoteId: Long = 0,
    val name: String = "",
    val email: String = "",
    val gender: String = "",
    val status: String = "inactive"
) : Parcelable {

}
