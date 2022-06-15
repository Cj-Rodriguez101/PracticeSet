package com.example.practiceset2.movie

import android.content.Context
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.practiceset2.domain.MovieDevItem
import com.example.practiceset2.list.ListRepository
import com.example.practiceset2.util.ServiceLocator

@ExperimentalPagingApi
class MovieViewModel(val listRepository: ListRepository): ViewModel() {

    //private val _movies = MutableLiveData<PagingData<MovieDevItem>>()
    val movies : LiveData<PagingData<MovieDevItem>> = listRepository.getTopRatedPagerForMovie().cachedIn(viewModelScope)
    //get() = _movies

    private val _hasSubmittedList = MutableLiveData<Boolean>(false)
    val hasSubmittedList: LiveData<Boolean>
    get() = _hasSubmittedList

    fun setHasSubmittedBack(bool: Boolean){
        _hasSubmittedList.value = bool
    }

//    fun getObserveList(): LiveData<PagingData<MovieDevItem>> =
//        listRepository.getTopRatedPagerForMovie().cachedIn(viewModelScope)
}

@ExperimentalPagingApi
@Suppress("UNCHECKED_CAST")
class MovieViewModelFactory(
    val context: Context
)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (MovieViewModel(ServiceLocator.provideListRepository(context)) as T)
}