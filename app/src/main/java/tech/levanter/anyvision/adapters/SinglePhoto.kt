package tech.levanter.anyvision.adapters

import android.net.Uri
import com.bumptech.glide.Glide
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.single_photo.view.*
import tech.levanter.anyvision.R


class SinglePhoto(
    private val imageUri: Uri
) : Item<ViewHolder>() {

    override fun getLayout(): Int = R.layout.single_photo

    override fun bind(viewHolder: ViewHolder, position: Int) {
        val image = viewHolder.itemView.single_photo_photo
        Glide.with(viewHolder.root.context).load(imageUri).into(image)
    }
}