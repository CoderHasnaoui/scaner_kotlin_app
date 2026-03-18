package com.example.mimi_projet_zentech.di
import com.example.mimi_projet_zentech.BuildConfig
import com.example.mimi_projet_zentech.data.local.SessionManager
import com.example.mimi_projet_zentech.data.local.TokenManager
import com.example.mimi_projet_zentech.data.remote.AuthApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
        @Provides
        @Singleton
        fun povideGsonConverterFactory(): GsonConverterFactory{
            return GsonConverterFactory.create()
        }
    @Provides
    @Singleton
    @Named("private")
    fun providePrivateApi(
        gsonConverter: GsonConverterFactory ,
        tokenManager : TokenManager
    ): AuthApi   {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val privateClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor { chain ->
                val token = tokenManager.getToken()
                val request = chain.request().newBuilder().apply {
                    if (!token.isNullOrEmpty()) {
                        addHeader("Authorization", "Bearer $token")
                    }
                }.build()

                val response = chain.proceed(request)

                if (response.code == 401) {
                    tokenManager.clearToken()
                    SessionManager.notifyTokenExpired()
                }

                response
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BuildConfig.Base_Url)
            .client(privateClient)
            .addConverterFactory(gsonConverter)
            .build()
            .create(AuthApi::class.java)
    }
    @Provides
    @Singleton
    @Named("public")
    fun povidePublicApi(
        gsonConverter: GsonConverterFactory ): AuthApi
    {
        return Retrofit.Builder().baseUrl(BuildConfig.Base_Url)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(gsonConverter)
            .build()
            .create(AuthApi::class.java)
    }



}