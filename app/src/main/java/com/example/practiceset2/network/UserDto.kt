package com.example.practiceset2.network

import android.os.Parcelable
import com.example.practiceset2.local.goRest.UserDtoCache
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
data class UserDtoList(
    val userDto: List<UserDto>
): Parcelable{
    override fun toString(): String {
        return "UserDtoList(userDto=$userDto)"
    }
}

@Parcelize
@JsonClass(generateAdapter = true)
data class UserDto(

    @Transient
    val cacheId: Int = 0,
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val gender: String = "male",
    val status: String = "inactive",
    @Transient
    var isChecked: Boolean = false
): Parcelable{

    override fun toString(): String {
        return "UserDto(cacheId=$cacheId, id=$id, name='$name', email='$email', gender='$gender', status='$status', isChecked=$isChecked)"
    }
}

fun UserDto.toSendUserDto(): SendUserDto{
    return SendUserDto(
        name, email, gender, status
    )
}

fun UserDto.toUserDtoCache(): UserDtoCache{
    return  UserDtoCache(
        remoteId = id.toLong(), name = name, email = email, gender = gender, status = status
    )
}

fun UserDtoCache.toUserDto(): UserDto{
    return  UserDto(
        id = remoteId.toInt(), cacheId = id.toInt(), name =  name, email = email,
        gender = gender, status = status
    )
}

fun List<UserDto>.toUserDtoCacheList(): List<UserDtoCache>{
    return this.map { it.toUserDtoCache() }
}
