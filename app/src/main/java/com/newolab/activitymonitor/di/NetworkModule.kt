package com.newolab.activitymonitor.di

import android.content.Context
import com.newolab.activitymonitor.BuildConfig
import com.newolab.activitymonitor.repository.api.ApiService
import com.newolab.activitymonitor.repository.api.auth.AuthInterceptor
import com.newolab.activitymonitor.util.Constants
import com.newolab.activitymonitor.util.Constants.BASE_URL
import com.newolab.activitymonitor.util.NetworkConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        userPreferences: com.newolab.activitymonitor.repository.datastore.UserPreferences
    ): AuthInterceptor {
        return AuthInterceptor(userPreferences)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Use Level.NONE in production
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor) // Add AuthInterceptor
            .addInterceptor(loggingInterceptor) // Add LoggingInterceptor
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(Constants.BASE_URL) // Replace with your actual base URL
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(
        retrofit: Retrofit
    ): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideNetworkConnectivityObserver(
        @ApplicationContext context: Context
    ): NetworkConnectivityObserver {
        return NetworkConnectivityObserver(context)
    }
}
