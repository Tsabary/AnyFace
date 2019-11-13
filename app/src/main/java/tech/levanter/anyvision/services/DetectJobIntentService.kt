package tech.levanter.anyvision.services

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.app.JobIntentService
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.room.PhotoDatabase
import tech.levanter.anyvision.viewModels.AllPhotosViewModel
import java.io.File

class DetectJobIntentService() : JobIntentService() {

    private val TAG = "DetectJobIntentServi22"

    fun enqueueWork(context: Context, work : Intent){
        enqueueWork(context, DetectJobIntentService::class.java, 12, work)
    }

    override fun onCreate() {
        super.onCreate()

        Log.d(TAG, "onCreate")
    }

    override fun onHandleWork(intent: Intent) {
        Log.d(TAG, "onHandleWork")

        PhotoDatabase.getDatabase(applicationContext)


        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        for (file in allPhotos) {
            val image = FirebaseVisionImage.fromFilePath(this.context!!, Uri.parse(file.uri))

            detector.detectInImage(image).addOnSuccessListener {
                if (it.isNotEmpty()) {
                    file.hasFaces = 1
                    allPhotosViewModel.update(file)
                } else {
                    file.hasFaces = 2
                    allPhotosViewModel.update(file)
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()

        Log.d(TAG, "onDestroy")

    }

    override fun onStopCurrentWork(): Boolean {

        Log.d(TAG, "onStopCurrentWork")
        return super.onStopCurrentWork()

    }
}