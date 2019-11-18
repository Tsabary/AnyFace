package tech.levanter.anyvision.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.levanter.anyvision.models.Photo


@Database(entities = [Photo::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var instance: PhotoDatabase? = null

        fun getInstance(context: Context): PhotoDatabase? {
            if (instance == null) {
                synchronized(PhotoDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PhotoDatabase::class.java, "photo_database"
                    )
                        .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
                        .build()
                }
            }
            return instance
        }
    }
}