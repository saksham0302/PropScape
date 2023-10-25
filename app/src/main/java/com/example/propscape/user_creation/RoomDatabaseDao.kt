package com.example.propscape.user_creation

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface RoomDatabaseDao {

    @Insert
    suspend fun insert(user: RoomDatabaseUser)

    @Update
    suspend fun update(user: RoomDatabaseUser)

    @Query("SELECT * FROM user WHERE username = :username")
    fun getUser(username: String): RoomDatabaseUser?
}