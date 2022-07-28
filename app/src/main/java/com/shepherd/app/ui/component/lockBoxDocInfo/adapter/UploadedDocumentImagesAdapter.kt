package com.shepherd.app.ui.component.lockBoxDocInfo.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.shepherd.app.R
import com.shepherd.app.utils.loadImage
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Nikita kohli  on 28-07-22
 */

class UploadedDocumentImagesAdapter(
    var context: Context,
    var list: ArrayList<String>?
) : PagerAdapter() {
    private var mLayoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return list?.size!!
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as LinearLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            mLayoutInflater.inflate(R.layout.adapter_document_pager, container, false)

        val imageView = itemView.findViewById<View>(R.id.imgDoc) as ShapeableImageView
        imageView.loadImage(list!![position])
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as LinearLayout)
    }

}