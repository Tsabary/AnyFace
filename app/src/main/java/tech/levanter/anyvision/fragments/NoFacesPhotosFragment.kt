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
import tech.levanter.anyvision.MainActivity
import tech.levanter.anyvision.R
import tech.levanter.anyvision.adapters.SinglePhoto
import tech.levanter.anyvision.viewModels.AllPhotosViewModel

class NoFacesPhotosFragment : Fragment() {

    lateinit var adapter : GroupAdapter<ViewHolder>
    lateinit var emptyIllustration : ImageView
    lateinit var detectButton: ConstraintLayout
    lateinit var detectButtonText : TextView

    private var isFirstOpen = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.galleries_layout, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as MainActivity

        val recycler = gallery_recycler
        adapter = GroupAdapter<ViewHolder>()
        recycler.adapter = adapter
        recycler.layoutManager = GridLayoutManager(this.context, 3)

        val emptyGalleryNotice = gallery_no_photos_container
        emptyIllustration = gallery_no_photos_illustration
        detectButton = gallery_detect_button
        detectButton.visibility = View.GONE
        detectButtonText = gallery_detect_button_text


        activity.let {
            ViewModelProviders.of(it).get(AllPhotosViewModel::class.java).getNoFacePhotos().observe(
                it,
                Observer { list ->
                    adapter.clear()
                    if (list.isEmpty()) emptyGalleryNotice.visibility =
                        View.VISIBLE else emptyGalleryNotice.visibility = View.GONE

                    for (image in list) {
                        adapter.add(SinglePhoto(Uri.parse(image.uri), activity))
                    }

                    adapter.notifyDataSetChanged()
                    if (isFirstOpen) {
                        recycler.scheduleLayoutAnimation()
                        isFirstOpen = false
                    }
                })
        }

    }
}
