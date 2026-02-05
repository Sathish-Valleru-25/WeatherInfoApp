package com.example.weatherapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class ApiKey


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // This Hilt provider injects the Keys class and uses it to provide the API key.
    @Provides
    @Singleton
    @ApiKey
    fun provideApiKey(keys: Keys): String = keys.apiKey
}