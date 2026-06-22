package com.gorokhov.notes.di

import android.content.Context
import com.gorokhov.notes.data.AppDatabase
import com.gorokhov.notes.data.NoteDao
import com.gorokhov.notes.data.RoomNotesRepositoryImpl
import com.gorokhov.notes.domain.NotesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
interface DataModule {


    @Singleton
    @Binds
    fun provideRoomNotesRepository(impl: RoomNotesRepositoryImpl): NotesRepository

    companion object {

        @Singleton
        @Provides
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return AppDatabase.getInstance(context)
        }

        @Singleton
        @Provides
        fun provideNotesDao(
            appDatabase: AppDatabase
        ): NoteDao {
            return appDatabase.noteDao()
        }
    }
}