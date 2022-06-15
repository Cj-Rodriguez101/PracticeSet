package com.example.practiceset2.paging

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.practiceset2.local.VideoDatabase
import com.example.practiceset2.local.goRest.GoRestRemoteKey
import com.example.practiceset2.local.goRest.UserDtoCache
import com.example.practiceset2.network.GoRestApiService
import com.example.practiceset2.network.toUserDtoCacheList
import retrofit2.HttpException
import java.io.IOException

private const val DEFAULT_PAGE_INDEX = 1
@ExperimentalPagingApi
class UserDtoMediator(val goRestApiService: GoRestApiService, val appDatabase: VideoDatabase) :
    RemoteMediator<Int, UserDtoCache>() {

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return if(appDatabase.goRestRemoteKeysCacheDao().getFirstMovieKey()!=null){
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, UserDtoCache>
    ): MediatorResult {

        val pageKeyData = getKeyPageData(loadType, state)

        val page = when (pageKeyData) {
            is MediatorResult.Success -> {
                return pageKeyData
            }
            else -> {
                pageKeyData as Int
            }
        }

        try {
            val response = goRestApiService.getListOfUsers(page = page).await()
            //Log.e("page", "${response.results.size} ${page}")
            val isEndOfList = response.isEmpty()
            appDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    appDatabase.discoverMoviesKeysDao().clearRemoteKeys()
                    appDatabase.discoverCacheDao().clearAllMovieData()

                    //Log.e("YUM"," ${pageKeyData} FIRST REFRESH")
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1

//                val prevKey = if(response.isNotEmpty()) page + 1 else null
//                val nextKey = if (isEndOfList) null else page - 1
                Log.e("page", "${page}")
                val keys = response.map {
                    GoRestRemoteKey(repoId = it.id.toLong(), prevKey = prevKey, nextKey = nextKey)
                }
                appDatabase.goRestRemoteKeysCacheDao().insertAll(keys)
                appDatabase.userCacheDao().insertUser(response.toUserDtoCacheList())
            }
            return MediatorResult.Success(endOfPaginationReached = isEndOfList)
        } catch (exception: IOException) {
            return MediatorResult.Error(exception)
        } catch (exception: HttpException) {
            return MediatorResult.Error(exception)
        }
    }

    /**
     * get the first remote key inserted which had the data
     */
    private suspend fun getFirstRemoteKey(state: PagingState<Int, UserDtoCache>): GoRestRemoteKey? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { users -> appDatabase.goRestRemoteKeysCacheDao().remoteKeysId(users.remoteId) }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, UserDtoCache>): GoRestRemoteKey? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                appDatabase.goRestRemoteKeysCacheDao().remoteKeysId(repo.remoteId.toLong())
            }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, UserDtoCache>): GoRestRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.remoteId?.let { repoId ->
                appDatabase.goRestRemoteKeysCacheDao().remoteKeysId(repoId.toLong())
            }
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, UserDtoCache>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                Log.e("YUM"," nextkey: ${remoteKeys?.nextKey?.minus(1)} remotekeys: ${remoteKeys} FIRST REFRESH")
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {

                //newest
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey

                Log.e("YUM"," nextkey: ${nextKey} remotekeys: ${remoteKeys} FIRST APPEND")
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey
            }
            LoadType.PREPEND -> {
                Log.e("YUM"," FIRST PREPEND")

                //newest
                val remoteKeys = getFirstRemoteKey(state)
                val prevKey = remoteKeys?.prevKey
                if (prevKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                prevKey
            }
        }
    }
}