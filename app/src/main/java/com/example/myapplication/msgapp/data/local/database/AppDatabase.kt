package com.example.myapplication.msgapp.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.myapplication.msgapp.data.local.dao.MessageDao
import com.example.myapplication.msgapp.model.Message

@Database(entities = [Message::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun messageDao(): MessageDao

    companion object{
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(applicationContext: Context): AppDatabase {
            return INSTANCE ?: synchronized(this){
                Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java,
                    "messages-db"
                ).fallbackToDestructiveMigration().build()
            }
        }
    }
}