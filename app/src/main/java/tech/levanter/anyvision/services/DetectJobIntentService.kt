package tech.levanter.anyvision.services

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.room.PhotoRepository


class DetectJobIntentService : Service() {

    lateinit var repo: PhotoRepository
    lateinit var observer: Observer<List<Photo>>

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        repo = PhotoRepository(application)
        Log.d("isitrunning", "righ before")

        observer = Observer {

            CoroutineScope(IO).launch {

                Log.d("isitrunning", "yes running")

                for (file in it) {
                    val image = FirebaseVisionImage.fromFilePath(application, Uri.parse(file.uri))
                    detector.detectInImage(image).addOnSuccessListener { list ->
                        if (list.isNotEmpty()) {
                            file.hasFaces = 1
                            repo.update(file)
                            Log.d("isitrunning", "has face")

                        } else {
                            file.hasFaces = 2
                            repo.update(file)
                            Log.d("isitrunning", "no face")
                        }
                    }
                }
            }
            repo.getAllPhotos().observeForever(observer)

        }


        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        val notification =
            NotificationCompat.Builder(this, getString(tech.levanter.anyvision.R.string.channel_id))
                .setContentTitle("Detecting faces..")
                .setContentText("64 photos detected")
                .setSmallIcon(tech.levanter.anyvision.R.drawable.ic_face)
                .setContentIntent(pendingIntent)
                .build()

        startForeground(1, notification)

        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        repo.getAllPhotos().removeObserver(observer)
    }

}
