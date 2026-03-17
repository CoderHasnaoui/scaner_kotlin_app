package com.example.mimi_projet_zentech.di

import android.app.Application
import androidx.room.Dao
import androidx.room.Room
import com.example.mimi_projet_zentech.data.local.dao.MerchantDao
import com.example.mimi_projet_zentech.data.local.dao.UserAccountDao
import com.example.mimi_projet_zentech.data.local.db.AppDatabase
import com.example.mimi_projet_zentech.data.local.db.DatabaseProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object  DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase {
        return Room.databaseBuilder(
            app.applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }

    @Provides
    fun provideMerchantDao(dao  : AppDatabase): MerchantDao{
        return dao.merchantDao()
    }
    @Provides
    fun provideAccountUserDao(dao : AppDatabase): UserAccountDao{
        return dao.userAccountDao()
    }


}