package com.example.cafechapin.data

import android.content.Context
import androidx.room3.Database
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

@Database(
    entities = [
        FavoriteEntity::class,
        OrderLineEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class StoreDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    abstract fun orderLineDao(): OrderLineDao

    companion object {
        @Volatile
        private var instance: StoreDatabase? = null

        fun getDatabase(context: Context): StoreDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder<StoreDatabase>(
                    context = context.applicationContext,
                    name = "store.db"
                )
                    .setDriver(AndroidSQLiteDriver())
                    .build()
                    .also { instance = it }
            }
        }
    }
}
