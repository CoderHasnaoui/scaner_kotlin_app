package com.example.mimi_projet_zentech.di
import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.example.mimi_projet_zentech.data.local.SlugManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.local.UserRepository
import com.example.mimi_projet_zentech.data.local.dataStore
import com.example.mimi_projet_zentech.ui.theme.ThemeRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ManagerModule{
@Provides
    @Singleton
    fun provideTokenManage(app: Application): TokenManager{
        return TokenManager(app)
    }

    @Provides
    @Singleton
    fun provideSlugManagerrr(app: Application): SlugManager{
        return SlugManager(app)
    }

    @Provides
    @Singleton
    fun provideDataStorer(app : Application) : DataStore<Preferences>{
        return app.dataStore
    }
    @Provides
    @Singleton
    fun provideUserRepository(dataStore: DataStore<Preferences>): UserRepository{
        return UserRepository(dataStore)

    }
    @Provides
    @Singleton
    fun provideThemeRepository(app: Application): ThemeRepository {
        return ThemeRepository(app)
    }

}