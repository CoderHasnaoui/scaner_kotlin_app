package com.example.mimi_projet_zentech.ui.theme.data.remote

import androidx.compose.ui.platform.LocalContext
import com.example.mimi_projet_zentech.ui.theme.data.local.TokenManager
import com.example.mimi_projet_zentech.ui.theme.data.model.Env
import com.example.mimi_projet_zentech.ui.theme.data.model.Login.AuthApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

//object RetrofitInstance {
//
//    private  val BASE_URL = Env.SANDBOX.baseUrl
//
//
//    fun getApi(tokenManager: TokenManager): AuthApi {
//
//        val client = OkHttpClient.Builder()
//            .addInterceptor { chain ->
//                val token = tokenManager.getToken()
//                val newRequest = chain.request().newBuilder()
//                if (!token.isNullOrEmpty()) {
//                    newRequest.addHeader("Authorization", "Bearer $token")
//                }
//                chain.proceed(newRequest.build())
//            }
//            .build()
//
//        return Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .client(client)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//            .create(AuthApi::class.java) //
//
//    }
//}



object RetrofitInstance {
    private val BASE_URL = Env.SANDBOX.baseUrl
    private val gsonConverter = GsonConverterFactory.create()

    val publicApi: AuthApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(gsonConverter)
            .build()
            .create(AuthApi::class.java)
    }

    fun getPrivateApi(
        tokenManager: TokenManager,
        onTokenExpired: () -> Unit = {}
    ): AuthApi {
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
                    onTokenExpired()
                }

                response
            }
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(privateClient)
            .addConverterFactory(gsonConverter)
            .build()
            .create(AuthApi::class.java)
    }
}
