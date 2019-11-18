package tech.levanter.anyvision.services

import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.room.PhotoDao
import tech.levanter.anyvision.room.PhotoDatabase
import tech.levanter.anyvision.room.PhotoRepository
import tech.levanter.anyvision.viewModels.AllPhotosViewModel
import java.io.File
import androidx.appcompat.app.AppCompatActivity
import android.app.DownloadManager
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T



class DetectJobIntentServiceOld : JobIntentService() {

    private val TAG = "DetectJobIntentServi22"
    lateinit var repo: PhotoRepository

    lateinit var observer : Observer<List<Photo>>

    fun enqueueWork(context: Context, work: Intent) {
        enqueueWork(context, DetectJobIntentServiceOld::class.java, 12, work)
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork")

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        repo = PhotoRepository(application)

        observer = Observer {
            for (file in it) {
                val image = FirebaseVisionImage.fromFilePath(application, Uri.parse(file.uri))

                detector.detectInImage(image).addOnSuccessListener { list ->
                    if (list.isNotEmpty()) {
                        file.hasFaces = 1
                        repo.update(file)
                    } else {
                        file.hasFaces = 2
                        repo.update(file)
                    }
                }
            }
        }

        repo.getAllPhotos().observeForever(observer)

//      repo.getAllPhotos().observeForever {
//            for (file in it) {
//                val image = FirebaseVisionImage.fromFilePath(application, Uri.parse(file.uri))
//
//                detector.detectInImage(image).addOnSuccessListener { list ->
//                    if (list.isNotEmpty()) {
//                        file.hasFaces = 1
//                        repo.update(file)
//                    } else {
//                        file.hasFaces = 2
//                        repo.update(file)
//                    }
//                }
//            }
//        }



//        for (file in repo.getAllPhotosStatic()) {
//            val image = FirebaseVisionImage.fromFilePath(application, Uri.parse(file.uri))
//
//            detector.detectInImage(image).addOnSuccessListener { list ->
//                if (list.isNotEmpty()) {
//                    file.hasFaces = 1
//                    repo.update(file)
//                } else {
//                    file.hasFaces = 2
//                    repo.update(file)
//                }
//            }
//        }
    }

    override fun onDestroy() {
        super.onDestroy()
        repo.getAllPhotos().removeObserver(observer)
        Log.d(TAG, "onDestroy")
    }

    override fun onStopCurrentWork(): Boolean {

        Log.d(TAG, "onStopCurrentWork")
        return super.onStopCurrentWork()

    }
}


//for (file in allPhotos) {
//    val image = FirebaseVisionImage.fromFilePath(application, Uri.parse(file.uri))
//
//    detector.detectInImage(image).addOnSuccessListener {
//        if (it.isNotEmpty()) {
//            file.hasFaces = 1
//            repo.update(file)
//        } else {
//            file.hasFaces = 2
//            repo.update(file)
//        }
//    }
//}