package com.example.practiceset2.paging

import android.util.Log
import androidx.paging.*
import androidx.room.withTransaction
import com.example.practiceset2.local.top_rated.MovieDevCache
import com.example.practiceset2.local.top_rated.MovieRemoteKey
import com.example.practiceset2.local.VideoDatabase
import com.example.practiceset2.network.MovieService
import com.example.practiceset2.network.toMovieDevCacheList
import retrofit2.HttpException
import java.io.IOException

private const val DEFAULT_PAGE_INDEX = 1
@ExperimentalPagingApi
class MovieMediator(val movieService: MovieService, val appDatabase: VideoDatabase) :
    RemoteMediator<Int, MovieDevCache>() {

    override suspend fun initialize(): InitializeAction {
        // Require that remote REFRESH is launched on initial load and succeeds before launching
        // remote PREPEND / APPEND.
        return if(appDatabase.remoteKeysDao().getFirstMovieKey()!=null){
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }

    }

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, MovieDevCache>
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
            val response = movieService.getTopRatedMovies("en", page).await()
            Log.e("page", "${response.results.size} ${page}")
            val isEndOfList = response.results.isEmpty()
            appDatabase.withTransaction {
                // clear all tables in the database
                if (loadType == LoadType.REFRESH) {
                    appDatabase.remoteKeysDao().clearRemoteKeys()
                    appDatabase.movieCacheDao().clearAllMovieData()

                    //Log.e("YUM"," ${pageKeyData} FIRST REFRESH")
                }
                val prevKey = if (page == DEFAULT_PAGE_INDEX) null else page - 1
                val nextKey = if (isEndOfList) null else page + 1
                val keys = response.results.map {
                    MovieRemoteKey(repoId = it.id.toLong(), prevKey = prevKey, nextKey = nextKey)
                }
                appDatabase.remoteKeysDao().insertAll(keys)
                appDatabase.movieCacheDao().insertMovie(response.toMovieDevCacheList())
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
    private suspend fun getFirstRemoteKey(state: PagingState<Int, MovieDevCache>): MovieRemoteKey? {
        return state.pages
            .firstOrNull() { it.data.isNotEmpty() }
            ?.data?.firstOrNull()
            ?.let { movie -> appDatabase.remoteKeysDao().remoteKeysId(movie.remoteId) }
    }

    /**
     * get the last remote key inserted which had the data
     */
    private suspend fun getLastRemoteKey(state: PagingState<Int, MovieDevCache>): MovieRemoteKey? {
//        return state.pages
//            .lastOrNull() { it.data.isNotEmpty() }
//            ?.data?.lastOrNull()
//            ?.let { movie -> appDatabase.remoteKeysDao().remoteKeysId(movie.id) }
        return state.pages.lastOrNull() { it.data.isNotEmpty() }?.data?.lastOrNull()
            ?.let { repo ->
                // Get the remote keys of the last item retrieved
                appDatabase.remoteKeysDao().remoteKeysId(repo.remoteId)
            }
    }

    /**
     * get the closest remote key inserted which had the data
     */
    private suspend fun getClosestRemoteKey(state: PagingState<Int, MovieDevCache>): MovieRemoteKey? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.remoteId?.let { repoId ->
                appDatabase.remoteKeysDao().remoteKeysId(repoId)
            }
        }
    }

    /**
     * this returns the page key or the final end of list success result
     */
    suspend fun getKeyPageData(loadType: LoadType, state: PagingState<Int, MovieDevCache>): Any? {
        return when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getClosestRemoteKey(state)
                Log.e("YUM"," nextkey: ${remoteKeys?.nextKey?.minus(1)} remotekeys: ${remoteKeys} FIRST REFRESH")
                remoteKeys?.nextKey?.minus(1) ?: DEFAULT_PAGE_INDEX
            }
            LoadType.APPEND -> {
//                val remoteKeys = getLastRemoteKey(state)
//                    ?: throw InvalidObjectException("Remote key should not be null for $loadType")
//                remoteKeys.nextKey

                //newest
                val remoteKeys = getLastRemoteKey(state)
                val nextKey = remoteKeys?.nextKey

                Log.e("YUM"," nextkey: ${nextKey} remotekeys: ${remoteKeys} FIRST APPEND")
                if (nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                }
                nextKey

//                val lastItem = state.lastItemOrNull()
//
//                // We must explicitly check if the last item is `null` when appending,
//                // since passing `null` to networkService is only valid for initial load.
//                // If lastItem is `null` it means no items were loaded after the initial
//                // REFRESH and there are no more items to load.
//                if (lastItem == null) {
//                    return MediatorResult.Success(endOfPaginationReached = true)
//                }
//
//                lastItem.id.toInt()



//                val remoteKeys = getLastRemoteKey(state)
//                if (remoteKeys?.nextKey == null) {
//                    return MediatorResult.Success(endOfPaginationReached = true)
//                }
//                remoteKeys.nextKey
                //MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.PREPEND -> {
//                val remoteKeys = getFirstRemoteKey(state)
//                    ?: throw InvalidObjectException("Invalid state, key should not be null")
//                //end of list condition reached
//                remoteKeys.prevKey ?: return MediatorResult.Success(endOfPaginationReached = true)
//                remoteKeys.prevKey
                Log.e("YUM"," FIRST PREPEND")
//                MediatorResult.Success(endOfPaginationReached = false)

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
