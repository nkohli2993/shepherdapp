package com.shepherd.app.ui.component.lockBoxDocInfo.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.load.Options
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.imageview.ShapeableImageView
import com.shepherd.app.R
import com.shepherd.app.data.dto.lock_box.get_all_uploaded_documents.DocumentUrl
import com.shepherd.app.utils.loadImageCentreCrop
import java.io.File
import java.util.*


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

    @SuppressLint("SetJavaScriptEnabled")
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            mLayoutInflater.inflate(R.layout.adapter_document_pager, container, false)

        val imageView = itemView.findViewById<View>(R.id.imgDoc) as ShapeableImageView
        val pdfVIew = itemView.findViewById<View>(R.id.pdfView) as PDFView

        if(list!![position].url!!.lowercase().endsWith(".png") ||list!![position].url!!.lowercase().endsWith(".jpg")  ||list!![position].url!!.lowercase().endsWith("jpeg") ) {
            imageView.loadImageCentreCrop(
                R.drawable.ic_defalut_profile_pic,
                list!![position].url!!.plus("?thumbnail=100")
            )
        }
        else if(list!![position].url!!.lowercase().endsWith(".pdf")||list!![position].url!!.lowercase().endsWith(".pdf/x")||list!![position].url!!.lowercase().endsWith(".pdf/a")||list!![position].url!!.lowercase().endsWith(".pdf/e")) {
            imageView.setImageResource(R.drawable.ic_pdf)
            imageView.visibility = View.VISIBLE
        }
        else if(list!![position].url!!.lowercase().endsWith(".doc") || list!![position].url!!.lowercase().endsWith(".docm")  || list!![position].url!!.lowercase().endsWith(".docx") || list!![position].url!!.lowercase().endsWith(".txt")){
            imageView.setImageResource(R.drawable.ic_doc)
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

    private fun getThumbnail(fileLink :String){
        val file = File(fileLink)
        val bitmapOptions = BitmapFactory.Options()
        bitmapOptions.inJustDecodeBounds = true
        BitmapFactory.decodeFile(file.absolutePath, bitmapOptions);
        val desiredWidth = 400
        val desiredHeight = 300
        val widthScale = bitmapOptions.outWidth/desiredWidth
        val heightScale = bitmapOptions.outHeight/desiredHeight
        val scale = widthScale.coerceAtMost(heightScale);
        var sampleSize = 1
        while (sampleSize < scale) {
            sampleSize *= 2
        }
        bitmapOptions.inSampleSize = sampleSize
        bitmapOptions.inJustDecodeBounds = false
        val thumbnail = BitmapFactory.decodeFile(file.absolutePath, bitmapOptions)
        Log.e("catch_exception","bitmap: $thumbnail")
    }

}