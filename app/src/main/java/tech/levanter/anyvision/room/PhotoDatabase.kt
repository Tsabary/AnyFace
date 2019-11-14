package tech.levanter.anyvision.room

import android.content.Context
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.levanter.anyvision.models.Photo
import java.io.File


@Database(entities = [Photo::class], version = 1, exportSchema = false)
abstract class PhotoDatabase : RoomDatabase() {

    abstract fun photoDao(): PhotoDao

    companion object {
        @Volatile
        private var instance: PhotoDatabase? = null
        private var INSTANCE: PhotoDatabase? = null


        fun getInstance(context: Context): PhotoDatabase? {
            if (instance == null) {
                synchronized(PhotoDatabase::class) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        PhotoDatabase::class.java, "photo_database"
                    )
                        .fallbackToDestructiveMigration() // when version increments, it migrates (deletes db and creates new) - else it crashes
//                        .addCallback(RoomCallback())
                        .build()
                }
            }
            return instance
        }


//        fun getDatabase(context: Context, scope: CoroutineScope): PhotoDatabase {
//            val tempInstance = INSTANCE
//            if (tempInstance != null) {
//                return tempInstance
//            }
//            synchronized(this) {
//                val instance = Room.databaseBuilder(
//                    context.applicationContext,
//                    PhotoDatabase::class.java,
//                    "photo_database"
//                ).addCallback(RoomCallback(scope)).build()
//                INSTANCE = instance
//                return instance
//            }
//        }

//        fun destroyInstance() {
//            instance = null
//        }


//        private class RoomCallback(
//            private val scope: CoroutineScope
//        ) : RoomDatabase.Callback() {
//
////            override fun onCreate(db: SupportSQLiteDatabase) {
////                super.onCreate(db)
////                PopulateDbAsyncTask(instance)
////                    .execute()
////            }
//
//            override fun onOpen(db: SupportSQLiteDatabase) {
//                super.onOpen(db)
//                INSTANCE?.let {
//                    scope.launch(Dispatchers.IO) {
//                    }
//                }
//            }
//        }


//        private val RoomCallback = object : RoomDatabase.Callback() {
//            override fun onCreate(db: SupportSQLiteDatabase) {
//                super.onCreate(db)
//                PopulateDbAsyncTask(instance)
//                    .execute()
//            }
//        }
    }
}
/*
class PopulateDbAsyncTask(db: PhotoDatabase?) : AsyncTask<Unit, Unit, Unit>() {

    private val acceptedExtensions = listOf(".jpg", ".jpeg", ".png")

    private val photosDao = db?.photoDao()

    override fun doInBackground(vararg p0: Unit?) {

        val anyVisionDir =
            File(Environment.getExternalStorageDirectory().path + "/download/anyvision")
        loadImages(anyVisionDir)
    }

    private fun loadImages(dir: File?) {
        if (dir != null) {
            if (dir.exists()) {

                val files = dir.listFiles()
                if (files != null) {
                    for (file in files) {
                        val absolutePath = file.absolutePath

                        if (absolutePath.contains(".")) {
                            val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
                            if (acceptedExtensions.contains(extension)) {
                                photosDao?.insert(Photo(Uri.fromFile(file).toString(), 0.0f, 0))
                            }
                        }
                    }
                }
            }
        }
    }

}
*/