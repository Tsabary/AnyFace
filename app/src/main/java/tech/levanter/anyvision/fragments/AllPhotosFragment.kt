package tech.levanter.anyvision.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.galleries_layout.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.R
import tech.levanter.anyvision.adapters.SinglePhoto
import tech.levanter.anyvision.interfaces.Methods
import tech.levanter.anyvision.models.Photo
import tech.levanter.anyvision.viewModels.AllPhotosViewModel


class AllPhotosFragment : Fragment(), Methods {
    private var allPhotos = listOf<Photo>()
    lateinit var emptyIllustration: ImageView
    lateinit var detectButton : ConstraintLayout
    lateinit var detectButtonText: TextView

    lateinit var allPhotosViewModel: AllPhotosViewModel
    var isFirstOpen = true

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

        detectButton = gallery_detect_button
        detectButton.visibility = View.VISIBLE
        detectButtonText = gallery_detect_button_text
        detectButtonText.text = activity.getString(R.string.detect_faces)

        activity.let {
            allPhotosViewModel = ViewModelProviders.of(this).get(AllPhotosViewModel::class.java)

            allPhotosViewModel.getAllPhotos()
                .observe(it, Observer { list ->
                    adapter.clear()
                    allPhotos = list

                    // Show or hide the 'empty 'gallery' illustration and message
                    if (list.isEmpty()) emptyGalleryNotice.visibility =
                        View.VISIBLE else emptyGalleryNotice.visibility = View.GONE

                    // Add photos to the recycler
                    for (file in list) {
                        adapter.add(SinglePhoto(Uri.parse(file.uri), activity))
                    }

                    // Add an extra line of empty items to allow for more scrolling
                    addExtraLinesToRecycler(adapter, activity)

                    //start recycler animation
                    adapter.notifyDataSetChanged()
//                recycler.scheduleLayoutAnimation()

                    if (isFirstOpen && list.isNotEmpty()) {
                        recycler.scheduleLayoutAnimation()
                        isFirstOpen = false
                    }
                })
        }

        detectButton.setOnClickListener {
            activity.isDetecting = true
            changeButtonText(activity, detectButtonText)
            CoroutineScope(IO).launch {
                detectFaces(activity, allPhotosViewModel, allPhotos)
            }
        }
    }
}
