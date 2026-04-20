package com.emergency.aria.di

import android.content.Context
import com.emergency.aria.CommunicationManager
import com.emergency.aria.bluetooth.BluetoothService
import com.emergency.aria.service.OnlineService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        // 🟢 FIXED: Last mein '/' hona bohot zaroori hai!
        .baseUrl("http://192.168.1.43:8080/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    @Provides
    @Singleton
    fun provideOnlineService(retrofit: Retrofit): OnlineService {
        // 🟢 FIXED: Retrofit correctly creates the interface implementation
        return retrofit.create(OnlineService::class.java)
    }

    @Provides
    @Singleton
    fun provideBluetoothService(@ApplicationContext ctx: Context): BluetoothService =
        BluetoothService(ctx)

    @Provides
    @Singleton
    fun provideCommunicationManager(
        onlineService: OnlineService,
        bluetoothService: BluetoothService
    ): CommunicationManager = CommunicationManager(onlineService, bluetoothService)
}