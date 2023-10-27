package com.example.propscape.user_creation

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RoomDatabaseDao {

    @Insert
    suspend fun insert(user: RoomDatabaseUser)

    @Delete
    suspend fun delete(user: RoomDatabaseUser)

    @Query("SELECT * FROM user WHERE username = :username")
    fun getUser(username: String): RoomDatabaseUser?
}