package com.example.practiceset2.paging

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.example.practiceset2.local.top_rated.MovieDevCache
import com.example.practiceset2.local.top_rated.MovieRemoteKey
import com.example.practiceset2.local.VideoDatabase
import com.example.practiceset2.local.discover.DiscoverDevCache
import com.example.practiceset2.local.discover.DiscoverRemoteKey
import com.example.practiceset2.network.MovieService
import com.example.practiceset2.network.toDiscoverMovieDevCacheList
import com.example.practiceset2.network.toMovieDevCacheList
import retrofit2.HttpException
import java.io.IOException

private const val DEFAULT_PAGE_INDEX = 1
@ExperimentalPagingApi
class DiscoverMovieMediator(val movieService: MovieService, val appDatabase: VideoDatabase) :
    RemoteMediator<Int, DiscoverDevCache>() {

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return if(appDatabase.discoverMoviesKeysDao().getFirstMovieKey()!=null){
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, DiscoverDevCache>
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
            val response = movieService.getDiscoverMovies("en", page).await()
            Log.e("page", "${response.results.size} ${page}")
            val isEndOfList = response.results.isEmpty()
            appDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    appDatabase.discoverMoviesKeysDao().clearRemoteKeys()
                    appDatabase.discoverCacheDao().clearAllMovieData()

                    //Log.e("YUM"," ${pageKeyData} FIRST REFRESH")
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.results.map {
                    DiscoverRemoteKey(repoId = it.id.toLong(), prevKey = prevKey, nextKey = nextKey)
                }
                appDatabase.discoverMoviesKeysDao().insertAll(keys)
                appDatabase.discoverCacheDao().insertMovie(response.toDiscoverMovieDevCacheList())
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
    private suspend fun getFirstRemoteKey(state: PagingState<Int, DiscoverDevCache>): MovieRemoteKey? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { movie -> appDatabase.remoteKeysDao().remoteKeysId(movie.remoteId) }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, DiscoverDevCache>): MovieRemoteKey? {
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                appDatabase.remoteKeysDao().remoteKeysId(repo.remoteId)
            }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, DiscoverDevCache>): MovieRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.remoteId?.let { repoId ->
                appDatabase.remoteKeysDao().remoteKeysId(repoId)
            }
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, DiscoverDevCache>): Any? {
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
