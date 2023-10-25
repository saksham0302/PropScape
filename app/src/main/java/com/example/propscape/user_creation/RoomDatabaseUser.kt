package com.example.propscape.user_creation

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user")
data class RoomDatabaseUser(

    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val username: String,
    val password: String,

){
    constructor(username: String, password: String) : this(0, username, password)
}
