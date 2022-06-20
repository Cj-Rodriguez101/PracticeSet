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
class MovieViewModel(listRepository: ListRepository): ViewModel() {

    val movies : LiveData<PagingData<MovieDevItem>> = listRepository.getTopRatedPagerForMovie().cachedIn(viewModelScope)
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