package com.shepherdapp.app.ui.component.lockBoxDocInfo.adapter

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.webkit.*
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.viewpager.widget.PagerAdapter
import com.google.android.material.imageview.ShapeableImageView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.lock_box.create_lock_box.Documents

import com.shepherdapp.app.ui.component.lockBoxDocInfo.LockBoxDocInfoFragment
import com.shepherdapp.app.utils.extensions.showError
import com.shepherdapp.app.utils.loadImageCentreCrop
import java.io.File
import java.util.*


/**
 * Created by Nikita kohli  on 28-07-22
 */

class UploadedDocumentImagesAdapter(
    var context: Context,
    var list: ArrayList<Documents>?,
    var fragment:  LockBoxDocInfoFragment
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

        imageView.setOnClickListener {
            if (list!![position].url!!.lowercase()
                    .endsWith(".png") || list!![position].url!!.lowercase()
                    .endsWith(".jpg") || list!![position].url!!.lowercase()
                    .endsWith("jpeg")|| list!![position].url!!.lowercase()
                    .endsWith("gif")|| list!![position].url!!.lowercase()
                    .endsWith("heic")|| list!![position].url!!.lowercase()
                    .endsWith("heif")
            ) {
                itemView.post {
                    showChooseFileDialog(list!![position].url!!, "image")
                }

            } else {
                itemView.post {
                    showChooseFileDialog(list!![position].url!!, "web")
                }
            }
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun showChooseFileDialog(path: String, type: String) {
        val dialog =
            Dialog(fragment.requireContext(), android.R.style.Theme_Translucent_NoTitleBar)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_show_file)
        val tvClose = dialog.findViewById(R.id.tvClose) as AppCompatTextView
        val image = dialog.findViewById(R.id.imageShowIV) as AppCompatImageView
        val webview = dialog.findViewById(R.id.webview) as WebView
        val progress = dialog.findViewById(R.id.progess) as ProgressBar
        image.visibility = View.GONE
        webview.visibility = View.GONE
        progress.visibility = View.VISIBLE
        when (type) {
            "image" -> {
                image.visibility = View.VISIBLE
                progress.visibility = View.GONE
                image.loadImageCentreCrop(
                    R.drawable.image,
                    path
                )

            }
            else -> {

                webview.settings.javaScriptEnabled = true
                webview.webChromeClient = WebChromeClient()
                webview.settings.allowFileAccessFromFileURLs = true
                webview.settings.allowUniversalAccessFromFileURLs = true
                webview.webViewClient = object : WebViewClient() {
                    override fun onReceivedError(
                        view: WebView?,
                        request: WebResourceRequest?,
                        error: WebResourceError?
                    ) {
                        super.onReceivedError(view, request, error)
                        progress.visibility = View.GONE
                        webview.visibility = View.VISIBLE
                        showError(context,context.getString(R.string.error_while_show))
                    }
                    override fun onPageFinished(view: WebView, url: String) {

                    }
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                        progress.visibility = View.GONE
                        webview.visibility = View.VISIBLE
                    }
                }
                webview.loadUrl("https://docs.google.com/gview?embedded=true&url=$path")
            }
        }
        tvClose.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(false)
        dialog.show()
    }
}