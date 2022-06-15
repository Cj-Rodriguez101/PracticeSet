package com.example.practiceset2.local.goRest

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface UserDtoCacheDao {

    @Query("SELECT * FROM userDtoTable")
    fun getUserList(): PagingSource<Int, UserDtoCache>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUser(userList: List<UserDtoCache>)

    @Query("UPDATE userDtoTable SET name =:name, email =:email, gender =:gender, status =:status WHERE id =:id")
    fun updateUser(name: String, email: String, gender: String, status: String, id: Long)

    @Query("DELETE FROM userDtoTable")
    fun clearUserDtoTable()

    @Delete
    fun deleteUser(userDtoCache: UserDtoCache)

    @Query("DELETE FROM userDtoTable WHERE id =:id")
    fun deleteUserById(id: Int)
}