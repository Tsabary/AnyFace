package tech.levanter.anyvision.interfaces

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.room.PhotoDatabase
import tech.levanter.anyvision.viewModels.AllPhotosViewModel
import java.io.File

interface Methods {

    fun hasNoPermissions(context: Context): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    }


    fun requestPermission(activity: Activity, permissions: Array<String>) {
        ActivityCompat.requestPermissions(activity as MainActivity, permissions, 17)
    }


    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }


    fun loadAllPhotos (activity:MainActivity) {

        val allPhotosViewModel = ViewModelProviders.of(activity).get(AllPhotosViewModel::class.java)

        val acceptedExtensions = listOf(".jpg", ".jpeg", ".png")


        val anyVisionDir =
            File(Environment.getExternalStorageDirectory().path + "/download/anyvision")

            if (anyVisionDir.exists()) {

                val files = anyVisionDir.listFiles()
                if (files != null) {
                    for (file in files) {
                        val absolutePath = file.absolutePath

                        if (absolutePath.contains(".")) {
                            val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
                            if (acceptedExtensions.contains(extension)) {
                                allPhotosViewModel.insert(Photo(Uri.fromFile(file).toString(), 0.0f, 0))
                            }
                        }
                    }
                }
            }
    }

//
//    class PopulateDbAsyncTask(db: PhotoDatabase?) : AsyncTask<Unit, Unit, Unit>() {
//
//        private val acceptedExtensions = listOf(".jpg", ".jpeg", ".png")
//
//        private val photosDao = db?.photoDao()
//
//        override fun doInBackground(vararg p0: Unit?) {
//
//            val anyVisionDir =
//                File(Environment.getExternalStorageDirectory().path + "/download/anyvision")
//            loadImages(anyVisionDir)
//        }
//
//        private fun loadImages(dir: File?) {
//            if (dir != null) {
//                if (dir.exists()) {
//
//                    val files = dir.listFiles()
//                    if (files != null) {
//                        for (file in files) {
//                            val absolutePath = file.absolutePath
//
//                            if (absolutePath.contains(".")) {
//                                val extension = absolutePath.substring(absolutePath.lastIndexOf("."))
//                                if (acceptedExtensions.contains(extension)) {
//                                    photosDao?.insert(Photo(Uri.fromFile(file).toString(), 0.0f, 0))
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//    }

}