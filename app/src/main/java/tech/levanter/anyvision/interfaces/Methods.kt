package tech.levanter.anyvision.interfaces

import android.Manifest
import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProviders
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.enums.*
import com.daimajia.androidanimations.library.Techniques
import com.daimajia.androidanimations.library.YoYo
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.R
import tech.levanter.anyvision.adapters.SinglePhoto
import tech.levanter.anyvision.models.Photo
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

    fun loadAllPhotos(activity: MainActivity) {

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

    fun navigateToFragment(
        activity: MainActivity,
        fm: FragmentManager,
        active: Fragment,
        targetFragment: Fragment
    ) {
        fm.beginTransaction().hide(active).show(targetFragment).commit()
        activity.active = targetFragment
        shake(
            when (targetFragment) {
                activity.allPhotosFragment -> activity.allPhotosFragment.emptyIllustration
                activity.facesPhotosFragment -> activity.facesPhotosFragment.emptyIllustration
                activity.noFacesPhotosFragment -> activity.noFacesPhotosFragment.emptyIllustration
                else -> return
            }
        )
    }


    private fun shake(view: View) {
        YoYo.with(Techniques.Shake)
            .duration(700)
            .playOn(view)
    }

    fun detectFaces(activity: MainActivity, viewModel : AllPhotosViewModel, allPhotos : List<Photo>) {

        activity.allPhotosFragment.detectButton.isClickable = false

        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .setMinFaceSize(0.15f)
            .build()

        val detector = FirebaseVision.getInstance()
            .getVisionFaceDetector(options)

        var hasFace = 0
        var noFace = 0
        var numOfFaces = 0
        var failedDetections = 0

        for (photo in allPhotos) {
            val image = FirebaseVisionImage.fromFilePath(activity, Uri.parse(photo.uri))

            detector.detectInImage(image).addOnSuccessListener { list ->
                if (list.isNotEmpty()) {
                    photo.hasFaces = 1
                    var totalJoy = 0f
                    for (face in list) {
                        totalJoy += face.smilingProbability
                    }
                    photo.joyLevel = totalJoy / list.size

                    viewModel.update(photo)
                    hasFace++
                    numOfFaces += list.size
                } else {
                    photo.hasFaces = 2
                    viewModel.update(photo)
                    noFace++
                }

                if (hasFace + noFace + failedDetections == allPhotos.size) {
                    activity.operationResultText =
                        "$numOfFaces faces were found in $hasFace photos"
                    activity.isDetecting = false


                    if (!activity.isAppInForeground) {

                        val notificationIntent = Intent(activity, MainActivity::class.java)
                        val pendingIntent = PendingIntent.getActivity(
                            activity,
                            0, notificationIntent, 0
                        )

                        val notification =
                            NotificationCompat.Builder(
                                activity,
                                activity.getString(R.string.channel_id)
                            )
                                .setContentTitle("Face detection finished")
                                .setContentText(activity.operationResultText)
                                .setSmallIcon(R.drawable.ic_face)
                                .setContentIntent(pendingIntent)
                                .setAutoCancel(true)


                        with(NotificationManagerCompat.from(activity)) {
                            // notificationId is a unique int for each notification that you must define
                            notify(111, notification.build())
                        }
                    }
                }
            }.addOnFailureListener {
                failedDetections++
            }
        }

    }


    fun changeButtonText(activity: MainActivity, button: TextView) {
        val text = button.text.toString()
        val analyzing = ".Analyzing."
        val handler = Handler()
        handler.postDelayed({
            if (activity.isDetecting) {

                if(text.contains("D")){
                    button.text = text.dropLast(1)
                } else {
                    if(!text.contains(analyzing)){
                        button.text = analyzing.subSequence(0,button.length()+1)
                    } else{
                        when {
                            text.contains("......") -> button.text = text.replace("...", ".")
                            text.contains(".....") -> button.text = text.replace("...", "......")
                            text.contains("....") -> button.text = text.replace("...", ".....")
                            text.contains("...") -> button.text = text.replace("...", "....")
                            text.contains("..") -> button.text = text.replace("..", "...")
                            text.contains(".") -> button.text = text.replace(".", "..")
                        }
                    }

                }

                changeButtonText(activity, button)
            }
            else {
                if(text.contains(".")){
                    button.text = text.dropLast(1)
                    changeButtonText(activity, button)
                } else {
                    makeButtonVisibleEverywhere(activity)
                }
            }
        }, 100)
    }

    fun makeButtonVisibleEverywhere(activity: MainActivity){
        activity.facesPhotosFragment.detectButton.visibility = View.VISIBLE
        activity.noFacesPhotosFragment.detectButton.visibility = View.VISIBLE

        addExtraLinesToRecycler(activity.facesPhotosFragment.adapter, activity)
        addExtraLinesToRecycler(activity.noFacesPhotosFragment.adapter, activity)

        buildFinalTextStringForAllButtons(activity)
    }

    fun buildFinalTextStringForAllButtons(activity: MainActivity){
        val allButton = activity.allPhotosFragment.detectButtonText
        val withFaceButton = activity.facesPhotosFragment.detectButtonText
        val noFaceButton = activity.noFacesPhotosFragment.detectButtonText

        val handler = Handler()
        handler.postDelayed({
        if (allButton.text != activity.operationResultText){
            allButton.text = activity.operationResultText.subSequence(0,allButton.length()+1)
            withFaceButton.text = activity.operationResultText.subSequence(0,withFaceButton.length()+1)
            noFaceButton.text = activity.operationResultText.subSequence(0,noFaceButton.length()+1)

            buildFinalTextStringForAllButtons(activity)
        }
        }, 100)
    }

    fun addExtraLinesToRecycler(adapter:GroupAdapter<ViewHolder>, activity: MainActivity){
        val buffer = 6 - (adapter.itemCount % 3)
        for (i in 0 until buffer) {
            adapter.add((SinglePhoto(Uri.EMPTY, activity)))
        }
    }


}