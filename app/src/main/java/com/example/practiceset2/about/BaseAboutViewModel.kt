package com.example.practiceset2.about

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.*
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.*

import com.example.practiceset2.list.ListRepository
import com.example.practiceset2.network.UserDto
import com.example.practiceset2.util.DataState
import com.example.practiceset2.util.ServiceLocator
import com.example.practiceset2.worker.CreateLatestNotificationWorker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Duration
import java.util.concurrent.TimeUnit

@ExperimentalPagingApi
class BaseAboutViewModel(private val application: Application, private val listRepository: ListRepository): ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    private val _scheduleText = MutableLiveData<String>("")
    val scheduleText : LiveData<String>
    get() = _scheduleText


    var aboutList : LiveData<PagingData<UserDto>> = listRepository
        .getUsersPaged().cachedIn(viewModelScope)

    var preference: SharedPreferences

    init {
          preference = application.applicationContext.getSharedPreferences("ShouldSchedule", Context.MODE_PRIVATE)
        _scheduleText.value = if (preference.getBoolean("ShouldSchedule", false)) "Cancel" else "Schedule"
    }

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading : LiveData<Boolean>
        get() = _isLoading

    private val _selectedUsers = MutableLiveData<List<UserDto>>(arrayListOf())
    val selectedUsers : LiveData<List<UserDto>>
        get() = _selectedUsers

    /*
    Used to add items in the list
     */
    fun addOrRemoveSelectedUsers(userDto: UserDto){
        _selectedUsers.value?.let { userList->
            if (userList.isNotEmpty() && userList.contains(userDto)){
                _selectedUsers.value = _selectedUsers.value?.filter { it!=userDto }
            } else {
                _selectedUsers.value = _selectedUsers.value!!.plus(userDto)
            }
        }
    }

    /* Used to clear list */
    fun clearList(){
        _selectedUsers.value = listOf()
    }

    fun addAllSelectedItems(userDto: List<UserDto>){
        _selectedUsers.value = _selectedUsers.value?.plus(userDto)
    }

    private val _info = MutableLiveData<DataState<*>?>()
    val info : LiveData<DataState<*>?>
        get() = _info

    private val _userDto= MutableLiveData<UserDto?>(UserDto())
    val userDto: LiveData<UserDto?>
        get() = _userDto

    private val _hours= MutableLiveData<Int>(preference.getInt("TimeSchedule", 0))
    val hours: LiveData<Int>
        get() = _hours

    val list = arrayOf("15","30", "45", "60")

    private val _selectedHours= MutableLiveData<String>()
    val selectedHours: LiveData<String>
        get() = _selectedHours

    fun setUserDto(userDto: UserDto){
        _userDto.value = userDto
    }

    fun setHours(selectedIndex: Int){
        preference.edit().putInt("TimeSchedule", selectedIndex).apply()
        _hours.value = selectedIndex
        _selectedHours.value = list[selectedIndex]
    }

    fun resetInfo(){
        _info.value = null
    }

    fun resetUserDto(){
        _userDto.value = UserDto()
    }

    fun setNameDto(name: CharSequence){
        _userDto.value = _userDto.value?.copy(name = name.toString())
    }

    fun setEmailDto(email: CharSequence){
        _userDto.value = _userDto.value?.copy(email = email.toString())
    }

    fun setStatus(status: Boolean){
        if (status){
            _userDto.value = _userDto.value?.copy(status = "active")
        } else {
            _userDto.value = _userDto.value?.copy(status = "inactive")
        }
    }

    fun setGender(gender: String){
        _userDto.value = _userDto.value?.copy(gender = gender)
    }

    fun insertUpdateUserDto(userDto: UserDto){
        viewModelScope.launch {
            _isLoading.value = true
            CoroutineScope(Dispatchers.IO).launch {
                _info.postValue(listRepository.insertUpdateUser(userDto))
                _isLoading.postValue(false)
            }
        }
    }

    fun deleteUsers(userDto: List<UserDto>){
        viewModelScope.launch {
            _isLoading.value = true
            _info.value = listRepository.deleteListOfUsers(userDto)
            _isLoading.value = false
        }
    }

    fun createInstantNotification(){

        // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = OneTimeWorkRequestBuilder<CreateLatestNotificationWorker>()
            .setConstraints(constraints)
            .addTag("DISPLAY_NOTIFICATION_TAG")
            .build()

        workManager.enqueue(work)
    }

    fun createPeriodicNotification(){

        // Create charging constraint
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val preference = application.applicationContext.getSharedPreferences("ShouldSchedule", Context.MODE_PRIVATE)
        val interval = list[preference?.getInt("TimeSchedule", 0)?:0].toLong()

        val work = PeriodicWorkRequestBuilder<CreateLatestNotificationWorker>(interval, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag("DISPLAY_NOTIFICATION_TAG")
            .build()

        workManager.enqueue(work)
        _scheduleText.value = "Cancel"
    }

    fun cancelPeriodicWork(){
        workManager.cancelAllWorkByTag("DISPLAY_NOTIFICATION_TAG")
        _scheduleText.value = "Schedule"
    }

    override fun onCleared() {
        super.onCleared()
        _userDto.value = null
    }
}

@ExperimentalPagingApi
@Suppress("UNCHECKED_CAST")
class BaseAboutViewModelFactory(
    val context: Context
) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (BaseAboutViewModel(context as Application, ServiceLocator.provideListRepository(context)) as T)
}