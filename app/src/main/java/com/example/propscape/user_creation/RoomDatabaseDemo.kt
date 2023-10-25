package com.example.propscape.user_creation

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomDatabaseUser::class], version = 1)
abstract class RoomDatabaseDemo : RoomDatabase() {

    abstract fun RoomDatabaseDao(): RoomDatabaseDao
}