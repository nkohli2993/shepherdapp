package com.app.shepherd.ui.component.onBoarding.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.app.shepherd.R
import com.app.shepherd.utils.loadImage
import java.util.*

class OnBoardingImagesAdapter(
    var context: Context,
    var images: List<Int>?
) : PagerAdapter() {
    private var mLayoutInflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return images?.size!!
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ImageView
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View =
            mLayoutInflater.inflate(R.layout.adapter_onboarding_pager, container, false)

        val imageView = itemView.findViewById<View>(R.id.imageViewMain) as ImageView

        imageView.loadImage(images!![position])

        Objects.requireNonNull(container).addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ImageView)
    }

}