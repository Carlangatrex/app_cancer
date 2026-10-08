package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        CompanionProfileEntity::class,
        ChatMessageEntity::class,
        CaregiverCheckInEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class RefugioDatabase : RoomDatabase() {
    abstract fun refugioDao(): RefugioDao

    companion object {
        @Volatile
        private var INSTANCE: RefugioDatabase? = null

        fun getInstance(context: Context): RefugioDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RefugioDatabase::class.java,
                    "refugio_cuidador_db"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
