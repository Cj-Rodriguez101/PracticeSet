package com.example.practiceset2.list

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.example.practiceset2.domain.MovieDevItem
import com.example.practiceset2.domain.VideoDevItem
import com.example.practiceset2.local.VideoDatabase
import com.example.practiceset2.local.devbytes.toVideoDevItemList
import com.example.practiceset2.local.discover.DiscoverDevCache
import com.example.practiceset2.local.discover.toMovieDevItem
import com.example.practiceset2.local.goRest.UserDtoCache
import com.example.practiceset2.local.top_rated.MovieDevCache
import com.example.practiceset2.local.top_rated.toMovieDevItem
import com.example.practiceset2.network.*
import com.example.practiceset2.paging.DiscoverMovieMediator
import com.example.practiceset2.paging.MovieMediator
import com.example.practiceset2.paging.UserDtoMediator
import com.example.practiceset2.util.DataState
import com.example.practiceset2.util.GenericMessageInfo
import com.example.practiceset2.util.UIComponentType
import com.example.practiceset2.util.VideoRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit

class ListRepository(private val videoDatabase: VideoDatabase,
                     private val videoNetworkDataSource: VideoNetworkDataSource): VideoRepository {
    override fun observeVideos(): LiveData<List<VideoDevItem>> {
        return Transformations.map(videoDatabase.videoDao().getAllDevBytes()) {
            it?.toVideoDevItemList() ?: listOf()
        }
    }

    override suspend fun refreshVideos(){
        val cacheList = videoDatabase.videoDao().getAllDevBytesNoLiveData()
        if(cacheList.isNullOrEmpty()){
            val networkResult = videoNetworkDataSource.observeVideos2().value

            if(!networkResult.isNullOrEmpty()){
                videoDatabase.videoDao().deleteAllVideos()
                videoDatabase.videoDao().insertVideoCaches(networkResult.toVideoDevCacheList())
            }
        }
    }

    @ExperimentalPagingApi
    fun getTopRatedPagerForMovie(): LiveData<PagingData<MovieDevItem>>{
        return Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = false, maxSize = 20*3),
        remoteMediator = MovieMediator(videoNetworkDataSource.movieApiService, videoDatabase), pagingSourceFactory = {
            videoDatabase.movieCacheDao().getMovieDataPaging()
        }).liveData.map {
            it.toPagingTopRatedMovieDevItem()
        }
    }

    @ExperimentalPagingApi
    fun getDiscoverForMovie(): LiveData<PagingData<MovieDevItem>>{
        return Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = false, maxSize = 20*3),
            remoteMediator = DiscoverMovieMediator(videoNetworkDataSource.movieApiService, videoDatabase), pagingSourceFactory = {
                videoDatabase.discoverCacheDao().getMovieDataPaging()
            }).liveData.map {
            it.toPagingDiscoverMovieDevItem()
        }
    }

    @ExperimentalPagingApi
    fun getUsersPaged(): LiveData<PagingData<UserDto>>{
        return Pager(config = PagingConfig(pageSize = 20, enablePlaceholders = false, maxSize = 20*3),
            remoteMediator = UserDtoMediator(videoNetworkDataSource.goRestApiService, videoDatabase), pagingSourceFactory = {
                videoDatabase.userCacheDao().getUserList()
            }).liveData.map {
            it.toPagingUserDto()
        }
    }

    suspend fun insertUpdateUser(userDto: UserDto): DataState<UserDto>{
        return try {
            if (userDto.id == 0){
                //insert user
                    val netResult: UserDto = videoNetworkDataSource.goRestApiService
                        .insertUser(sendUserDto = userDto.toSendUserDto()).await()
                videoDatabase.userCacheDao().insertUser(listOf(netResult.toUserDtoCache()))
                DataState.data(data = netResult,
                    message = GenericMessageInfo.Builder()
                        .id("InsertUpdateUser.Success")
                        .title("Success")
                        .uiComponentType(UIComponentType.Dialog)
                        .description("Successfully added user").build())

            } else {
                //update user
                    val netResult = videoNetworkDataSource.goRestApiService
                        .updateUser(userId = userDto.id.toString(), sendUserDto = userDto).await()
                videoDatabase.userCacheDao().updateUser(name = userDto.name, email = userDto.email,
                    gender = userDto.gender, status = userDto.status, id = userDto.cacheId.toLong())
                DataState.data(data = netResult,
                    message = GenericMessageInfo.Builder()
                        .id("InsertUpdateUser.Success")
                        .title("Success")
                        .uiComponentType(UIComponentType.Dialog)
                        .description("Successfully updated user").build())

            }
        } catch (ex: Exception){
            val errorMessage = ex.message?:"Unknown error"
            DataState.error(message = GenericMessageInfo.Builder().id("InsertUpdateUser.Error").title("Error")
                .description(errorMessage).uiComponentType(UIComponentType.Dialog).build())
        }
    }

    suspend fun deleteListOfUsers(userList: List<UserDto>): DataState<String>{
        return try {
            val requestSemaphore = Semaphore(10)
            val job = CoroutineScope(IO).launch {
                val futures = userList.map {
                        userDto ->
                    async {
                        requestSemaphore.withPermit {
                            videoNetworkDataSource.goRestApiService.deleteUser(userId = userDto.id.toString())
                            videoDatabase.userCacheDao().deleteUserById(userDto.cacheId)
                        }
                    }
            }
                futures.awaitAll()
        }
            job.join()
            DataState.data(data = "Finished", message = GenericMessageInfo.Builder()
                .id("DeleteUser.Success")
                .title("Success")
                .uiComponentType(UIComponentType.Dialog)
                .description("Successful delete user").build())
        } catch (ex: Exception){
            val errorMessage = ex.message?:"Unknown error"
            DataState.error<String>(message = GenericMessageInfo.Builder()
                .id("DeleteUser.Error")
                .title("Error")
                .uiComponentType(UIComponentType.Dialog)
                .description(errorMessage).build())
        }
    }
}

fun PagingData<MovieDevCache>.toPagingTopRatedMovieDevItem(): PagingData<MovieDevItem>{
    return this.map {
        it.toMovieDevItem()
    }
}

fun PagingData<DiscoverDevCache>.toPagingDiscoverMovieDevItem(): PagingData<MovieDevItem>{
    return this.map {
        it.toMovieDevItem()
    }
}

fun PagingData<UserDtoCache>.toPagingUserDto(): PagingData<UserDto>{
    return this.map {
        it.toUserDto()
    }
}