package com.example.amirassignment.data.local



import androidx.room.Database

import androidx.room.RoomDatabase



@Database(

    entities = [

        ProductEntity::class,

        InteractionLogEntity::class,

        RemoteKeys::class,

        CartItemEntity::class,

    ],

    version = 4,

    exportSchema = false,

)

abstract class MarketplaceDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    abstract fun interactionLogDao(): InteractionLogDao

    abstract fun remoteKeysDao(): RemoteKeysDao

    abstract fun cartDao(): CartDao

}

