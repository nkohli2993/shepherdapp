package com.shepherd.app.ui.component.lockBoxDocInfo.adapter
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.DocumentUrl
import com.shepherd.app.utils.loadImageCentreCrop
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by Nikita kohli  on 28-07-22
 */

class UploadedDocumentImagesAdapter(
    var context: Context,
    var list: ArrayList<DocumentUrl>?
) : PagerAdapter() {
    private var mLayoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return list?.size!!
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            mLayoutInflater.inflate(R.layout.adapter_document_pager, container, false)

        val imageView = itemView.findViewById<View>(R.id.imgDoc) as ShapeableImageView
        if(list!![position].url!!.lowercase().endsWith(".png") ||list!![position].url!!.lowercase().endsWith(".jpg")  ||list!![position].url!!.lowercase().endsWith("jpeg") ) {
            imageView.loadImageCentreCrop(
                R.drawable.ic_defalut_profile_pic,
                list!![position].url!!.plus("?thumbnail=100")
            )
        }
        else{
            imageView.loadImageCentreCrop(
                R.drawable.ic_defalut_profile_pic,
                list!![position].url!!
            )
        }
        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    }

}