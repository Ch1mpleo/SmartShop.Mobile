package com.example.smartshopmobile.di

import com.example.smartshopmobile.data.api.*
import com.example.smartshopmobile.data.network.AuthInterceptor
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
    fun provideOkHttpClient(authInterceptor: AuthInterceptor): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    @Named("MainRetrofit")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://10.0.2.2:5000/") 
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("OsrmRetrofit")
    fun provideOsrmRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://router.project-osrm.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthService(@Named("MainRetrofit") retrofit: Retrofit): AuthService = retrofit.create(AuthService::class.java)

    @Provides
    @Singleton
    fun provideUserService(@Named("MainRetrofit") retrofit: Retrofit): UserService = retrofit.create(UserService::class.java)

    @Provides
    @Singleton
    fun provideCategoryService(@Named("MainRetrofit") retrofit: Retrofit): CategoryService = retrofit.create(CategoryService::class.java)

    @Provides
    @Singleton
    fun provideProductService(@Named("MainRetrofit") retrofit: Retrofit): ProductService = retrofit.create(ProductService::class.java)

    @Provides
    @Singleton
    fun provideStoreLocationService(@Named("MainRetrofit") retrofit: Retrofit): StoreLocationService = retrofit.create(StoreLocationService::class.java)

    @Provides
    @Singleton
    fun provideCartService(@Named("MainRetrofit") retrofit: Retrofit): CartService = retrofit.create(CartService::class.java)

    @Provides
    @Singleton
    fun provideOrderService(@Named("MainRetrofit") retrofit: Retrofit): OrderService = retrofit.create(OrderService::class.java)

    @Provides
    @Singleton
    fun provideCheckoutService(@Named("MainRetrofit") retrofit: Retrofit): CheckoutService = retrofit.create(CheckoutService::class.java)

    @Provides
    @Singleton
    fun provideOsrmService(@Named("OsrmRetrofit") retrofit: Retrofit): OsrmService = retrofit.create(OsrmService::class.java)
}
