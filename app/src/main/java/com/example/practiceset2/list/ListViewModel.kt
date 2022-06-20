package com.example.practiceset2.list

import android.content.Context
import android.util.Log
import androidx.lifecycle.*
import com.example.practiceset2.domain.VideoDevItem
import com.example.practiceset2.util.Result
import com.example.practiceset2.util.ServiceLocator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListViewModel(val repository: ListRepository): ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading : LiveData<Boolean>
    get() = _isLoading

    private val _items = repository.observeVideos()
    val items: LiveData<List<VideoDevItem>>
        get() = _items

    private val _status = MutableLiveData(if(!_items.value.isNullOrEmpty()){
        _items.value!!.joinToString(",")}else {""})
    val status: LiveData<String>
        get() = _status


    init {
        viewModelScope.launch {
            _isLoading.value = true
            val job = CoroutineScope(Dispatchers.IO).launch {
                repository.refreshVideos()
            }
            job.join()
            _isLoading.value = false
        }
    }
}

@Suppress("UNCHECKED_CAST")
class ListDetailViewModelFactory(
    val context: Context
)
 : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (ListViewModel(ServiceLocator.provideListRepository(context)) as T)
}
