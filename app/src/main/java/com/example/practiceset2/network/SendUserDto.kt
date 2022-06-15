package com.example.practiceset2.network

import android.os.Parcelable
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class SendUserDto(
    val name: String = "",
    val email: String = "",
    val gender: String = "",
    val status: String = "InActive"
): Parcelable {

    override fun toString(): String {
        return "UserDto(name='$name', email='$email', gender='$gender', status='$status')"
    }
}
