package tech.levanter.anyvision.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.galleries_layout.*
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.R
import tech.levanter.anyvision.adapters.SinglePhoto
import tech.levanter.anyvision.viewModels.AllPhotosViewModel
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.services.DetectJobIntentService


class AllPhotosFragment : Fragment() {
    private var allPhotos = mutableListOf<Photo>()
    lateinit var emptyIllustration: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.galleries_layout, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity


        val recycler = gallery_recycler
        val adapter = GroupAdapter<ViewHolder>()
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this.context, 3)

        val emptyGalleryNotice = gallery_no_photos_container
        emptyIllustration = gallery_no_photos_illustration

        val detectButton = gallery_detect_button
        detectButton.visibility = View.VISIBLE


        activity.let {
            ViewModelProviders.of(this).get(AllPhotosViewModel::class.java).getAllPhotos()
                .observe(it, Observer { list ->
                    adapter.clear()
                    allPhotos.clear()
                    allPhotos = list

                    // Show or hide the 'empty 'gallery' illustration and message
                    if (list.isEmpty()) emptyGalleryNotice.visibility =
                        View.VISIBLE else emptyGalleryNotice.visibility = View.GONE

                    // Add photos to the recycler
                    for (file in list) {
                        adapter.add(SinglePhoto(Uri.parse(file.uri)))
                    }

                    // Add an extra line of empty items to allow for more scrolling
                    val buffer = 6 - (adapter.itemCount % 3)
                    for (i in 0 until buffer) {
                        adapter.add((SinglePhoto(Uri.EMPTY)))
                    }

                    //start recycler animation
                    adapter.notifyDataSetChanged()
//                recycler.scheduleLayoutAnimation()

//                if(isFirstOpen && adapter.itemCount >buffer+5){
//                    recycler.scheduleLayoutAnimation()
//                    isFirstOpen = false
//                }
                })
        }

        detectButton.setOnClickListener {

            startDetection()
        }
    }


    private fun startDetection() {
        val serviceIntent = Intent(this.context, DetectJobIntentService::class.java)
//        DetectJobIntentService().enqueueWork(this.context!!, serviceIntent)
        (activity as MainActivity).startService(serviceIntent)
    }

/*
    fun detectFaces() {
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
                //                    if (it.isNotEmpty()) imagesWithFaces.add(file) else imagesWithNoFaces.add(file)

                if (it.isNotEmpty()) {
                    file.hasFaces = 1
                    allPhotosViewModel.update(file)
                } else {
                    file.hasFaces = 2
                    allPhotosViewModel.update(file)
                }


                /*
                adapter.remove(SinglePhoto(file)) should remove items as they get sorted but currently crashes, need to see why
                adapter.notifyDataSetChanged()
                 */
//                    if (allPhotos.size == imagesWithFaces.size + imagesWithNoFaces.size + failedDetectionOperations) postLists()
            }.addOnFailureListener {
                failedDetectionOperations++
            }
        }

    }
*/
}
