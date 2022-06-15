package com.example.practiceset2.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

const val TOKEN = "Bearer cafaa6ba29604b919db4d7bd848e993f550386786ed26d1d53616003ef3112de"
interface GoRestApiService {

    //create list of paginated users
    @GET("users")
    fun getListOfUsers(
        @Header("Authorization") authorization: String = TOKEN,
        @Query("page") page: Int
        //@Query("Authorization") authorization: String = TOKEN,
    ): Deferred<List<UserDto>>

    //create new user
    @POST("users")
    fun insertUser(
        @Header("Authorization") authorization: String = TOKEN, @Body sendUserDto: SendUserDto
    ): Deferred<UserDto>

    //update the user
    @PATCH("users/{userId}")
    fun updateUser(
        @Header("Authorization") authorization: String = TOKEN,
        @Path("userId") userId: String, @Body sendUserDto: UserDto
    ): Deferred<UserDto>

    @DELETE("users/{userId}")
    fun deleteUser(
        @Header("Authorization") authorization: String = TOKEN,
        @Path("userId") userId: String
    ): Deferred<String>
}

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

object GoRestNetwork {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://gorest.co.in/public/v2/")
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()

    val theGoRestDb = retrofit.create(GoRestApiService::class.java)
}