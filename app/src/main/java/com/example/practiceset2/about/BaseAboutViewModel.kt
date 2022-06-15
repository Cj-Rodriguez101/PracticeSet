package com.example.practiceset2.about

import android.app.Application
import android.content.Context
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
class BaseAboutViewModel(val application: Application, val listRepository: ListRepository): ViewModel() {

    private val workManager = WorkManager.getInstance(application)

    private val _scheduleText = MutableLiveData<String>("")
    val scheduleText : LiveData<String>
    get() = _scheduleText


    var aboutList : LiveData<PagingData<UserDto>> = listRepository
        .getUsersPaged().cachedIn(viewModelScope)

    init {
        val preference = application.applicationContext.getSharedPreferences("ShouldSchedule", Context.MODE_PRIVATE)
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
                //_selectedUsers.value.remove(userDto)
                _selectedUsers.value = _selectedUsers.value?.filter { it!=userDto }
            } else {
               // _selectedUsers.value!!.add(userDto)
                _selectedUsers.value = _selectedUsers.value!!.plus(userDto)
            }
        }
    }

    /*
    Used to clear list
     */
    fun clearList(){
        _selectedUsers.value = listOf()
    }

    fun refreshList(){
        aboutList = listRepository
            .getUsersPaged().cachedIn(viewModelScope)
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

    private val _hours= MutableLiveData<Int>()
    val hours: LiveData<Int>
        get() = _hours

    val list = arrayOf("0.5","2", "4", "6", "8", "10" ,"12", "14", "16", "18", "20", "22", "24")

    private val _selectedHours= MutableLiveData<String>()
    val selectedHours: LiveData<String>
        get() = _selectedHours

    fun setUserDto(userDto: UserDto){
        _userDto.value = userDto
    }

    fun setHours(selectedIndex: Int){
        _hours.value = selectedIndex
        Log.e("realhours", "${list[selectedIndex]}")
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

        //behave we are gonna chain work but for now we have one worker
//        var continuation = workManager
//            .beginUniqueWork(
//                "DISPLAY_NOTIFICATION_TAG",
//                ExistingWorkPolicy.REPLACE,
//                OneTimeWorkRequest.from(CreateLatestNotificationWorker::class.java)
//            )

        // Create charging constraint
        val constraints = Constraints.Builder()
            //.setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val work = OneTimeWorkRequestBuilder<CreateLatestNotificationWorker>()
            .setConstraints(constraints)
            .addTag("DISPLAY_NOTIFICATION_TAG")
            .build()

        workManager.enqueue(work)
    }

    fun createPeriodicNotification(){

        //behave we are gonna chain work but for now we have one worker
//        var continuation = workManager
//            .beginUniqueWork(
//                "DISPLAY_NOTIFICATION_TAG",
//                ExistingWorkPolicy.REPLACE,
//                OneTimeWorkRequest.from(CreateLatestNotificationWorker::class.java)
//            )

        // Create charging constraint
        val constraints = Constraints.Builder()
            //.setRequiresCharging(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val preference = application.applicationContext.getSharedPreferences("ShouldSchedule", Context.MODE_PRIVATE)
        //val isScheduled = preference?.getBoolean("ShouldSchedule", false)?:false
        val duration = preference?.getString("TimeSchedule", "10")?:"10"
        val durationTime = Duration.ofHours(duration.toLong())

        val work = PeriodicWorkRequestBuilder<CreateLatestNotificationWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            //.setInitialDelay(15, TimeUnit.MINUTES)
            .addTag("DISPLAY_NOTIFICATION_TAG")
            .build()

        workManager.enqueue(work)
        _scheduleText.value = "Cancel"
    }

    fun cancelWork(){
        workManager.cancelUniqueWork("DISPLAY_NOTIFICATION_TAG")
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
)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>) =
        (BaseAboutViewModel(context as Application, ServiceLocator.provideListRepository(context)) as T)
}