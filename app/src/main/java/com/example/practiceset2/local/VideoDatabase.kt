package com.example.practiceset2.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.practiceset2.local.devbytes.VideoCacheDao
import com.example.practiceset2.local.devbytes.VideoDevCache
import com.example.practiceset2.local.discover.DiscoverCacheDao
import com.example.practiceset2.local.discover.DiscoverDevCache
import com.example.practiceset2.local.discover.DiscoverMoviesKeyDao
import com.example.practiceset2.local.discover.DiscoverRemoteKey
import com.example.practiceset2.local.goRest.GoRestRemoteKey
import com.example.practiceset2.local.goRest.GoRestRemoteKeyDao
import com.example.practiceset2.local.goRest.UserDtoCache
import com.example.practiceset2.local.goRest.UserDtoCacheDao
import com.example.practiceset2.local.top_rated.MovieCacheDao
import com.example.practiceset2.local.top_rated.MovieDevCache
import com.example.practiceset2.local.top_rated.MovieRemoteKey
import com.example.practiceset2.local.top_rated.RemoteKeysDao

@Database(entities = [VideoDevCache::class, MovieRemoteKey::class, DiscoverRemoteKey::class,
    MovieDevCache::class, DiscoverDevCache::class,
    UserDtoCache::class, GoRestRemoteKey::class], version = 5, exportSchema = false)
abstract class VideoDatabase : RoomDatabase() {

    abstract fun videoDao(): VideoCacheDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    abstract fun discoverMoviesKeysDao(): DiscoverMoviesKeyDao

    abstract fun movieCacheDao(): MovieCacheDao

    abstract fun discoverCacheDao(): DiscoverCacheDao

    abstract fun userCacheDao(): UserDtoCacheDao

    abstract fun goRestRemoteKeysCacheDao(): GoRestRemoteKeyDao
}
