package com.example.amirassignment.di

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache
import coil.memory.MemoryCache
import com.example.amirassignment.data.local.MarketplaceDatabase
import com.example.amirassignment.data.local.ProductDao
import com.example.amirassignment.data.local.CartDao
import com.example.amirassignment.data.local.InteractionLogDao
import com.example.amirassignment.data.local.RemoteKeysDao
import com.example.amirassignment.data.remote.DummyJsonApi
import com.example.amirassignment.data.repository.AnalyticsRepositoryImpl
import com.example.amirassignment.data.repository.CartRepositoryImpl
import com.example.amirassignment.data.repository.ProductRepositoryImpl
import com.example.amirassignment.domain.repository.AnalyticsRepository
import com.example.amirassignment.domain.repository.CartRepository
import com.example.amirassignment.domain.repository.ProductRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ImageModule {

    @Provides
    @Singleton
    fun provideImageLoader(@ApplicationContext context: Context): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(false)
            .memoryCache {
                MemoryCache.Builder(context)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(context.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
}

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(OkHttp) {
        install(HttpTimeout) {
            connectTimeoutMillis = 30_000
            socketTimeoutMillis = 60_000
            requestTimeoutMillis = 60_000
        }
        install(ContentNegotiation) {
            json(json)
        }
    }

    @Provides
    @Singleton
    fun provideDummyJsonApi(client: HttpClient): DummyJsonApi = DummyJsonApi(client)
}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MarketplaceDatabase =
        androidx.room.Room.databaseBuilder(
            context,
            MarketplaceDatabase::class.java,
            "marketplace.db",
        ).fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideProductDao(db: MarketplaceDatabase): ProductDao = db.productDao()

    @Provides
    fun provideInteractionLogDao(db: MarketplaceDatabase): InteractionLogDao = db.interactionLogDao()

    @Provides
    fun provideRemoteKeysDao(db: MarketplaceDatabase): RemoteKeysDao = db.remoteKeysDao()

    @Provides
    fun provideCartDao(db: MarketplaceDatabase): CartDao = db.cartDao()
}

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProductRepository(impl: ProductRepositoryImpl): ProductRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(impl: AnalyticsRepositoryImpl): AnalyticsRepository

    @Binds
    @Singleton
    abstract fun bindCartRepository(impl: CartRepositoryImpl): CartRepository
}
