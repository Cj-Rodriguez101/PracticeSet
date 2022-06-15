package com.example.practiceset2

import android.app.Application
import com.example.practiceset2.list.ListRepository
import com.example.practiceset2.util.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class VideoApplication: Application() {

    //private val applicationScope = CoroutineScope(Dispatchers.Default)
//    val listRepository: ListRepository
//        get() = ServiceLocator.provideListRepository(this)

    override fun onCreate() {
        super.onCreate()
    }
}