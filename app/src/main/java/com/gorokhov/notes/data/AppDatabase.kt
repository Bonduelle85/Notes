package com.gorokhov.notes.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [NoteDbModel::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun noteDao(): NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private val LOCK = Any()

        fun getInstance(context: Context): AppDatabase {
            // Первая проверка (без блокировки)
            INSTANCE?.let { return it }

            // Синхронизация только при необходимости
            synchronized(LOCK) {
                // Вторая проверка (double-check)
                INSTANCE?.let { return it }

                return Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "notes_database"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}