package com.shepherdapp.app.ui.component.resources.adapter

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.data.dto.resource.AllResourceData
import com.shepherdapp.app.databinding.AdapterTopicsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.ResourceViewModel
import com.squareup.picasso.Picasso


class TopicsAdapter(
    private val viewModel: ResourceViewModel,
    var trendingResourceList: MutableList<AllResourceData> = ArrayList()
) :
    RecyclerView.Adapter<TopicsAdapter.TopicsViewHolder>() {
    lateinit var binding: AdapterTopicsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSelectedResource(itemData[0] as AllResourceData)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopicsViewHolder {
        context = parent.context
        binding =
            AdapterTopicsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return TopicsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return trendingResourceList.size
    }

    override fun onBindViewHolder(holder: TopicsViewHolder, position: Int) {
        holder.bind(trendingResourceList[position], onItemClickListener)
    }


    inner class TopicsViewHolder(private val itemBinding: AdapterTopicsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(resourceData: AllResourceData, recyclerItemListener: RecyclerItemListener) {
            if (resourceData.title?.length!! > 50) {
                setNotesClickForLong(
                    resourceData.title!!,
                    true,
                    itemBinding.textViewTitle,
                    resourceData,
                    recyclerItemListener
                )
            } else {
                itemBinding.textViewTitle.text = resourceData.title
            }

            if (resourceData.thumbnailUrl != null && resourceData.thumbnailUrl != "") {
                Picasso.get().load(resourceData.thumbnailUrl)
                    .placeholder(R.drawable.image)
                    .into(itemBinding.imageViewTopic)
            }
            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    resourceData
                )
            }
        }

        private fun setNotesClickForLong(
            notes: String,
            isSpanned: Boolean,
            textView: AppCompatTextView,
            resourceData: AllResourceData,
            recyclerItemListener: RecyclerItemListener
        ) {
            val showNotes = if (isSpanned) {
                notes.substring(0, 50).plus(" ... Read more.")
            } else {
                notes.plus(" ... Read less.")
            }
            val ss = SpannableString(showNotes)
            val clickableSpan: ClickableSpan = object : ClickableSpan() {

                override fun onClick(p0: View) {
                    //click to show whole note
                    // setNotesClickForLong(notes, !isSpanned, textView)

                    // Redirect to Resources Detail Screen on clicking View More
                    recyclerItemListener.onItemSelected(
                        resourceData
                    )
                }

                override fun updateDrawState(ds: TextPaint) {
                    super.updateDrawState(ds)
                    ds.isUnderlineText = false
                    ds.isFakeBoldText = true
                    ds.color = ContextCompat.getColor(context, R.color._399282)
                    ds.linkColor = ContextCompat.getColor(context, R.color._399282)
                }
            }
            if (showNotes.length <= 65) {
                ss.setSpan(clickableSpan, 50, 65, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            } else {
                ss.setSpan(
                    clickableSpan,
                    showNotes.length - 15,
                    showNotes.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }

            textView.text = ss
            textView.movementMethod = LinkMovementMethod.getInstance()
            textView.highlightColor = Color.GREEN
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(dashboard: MutableList<AllResourceData>) {
        this.trendingResourceList.addAll(dashboard)
        notifyDataSetChanged()
    }

}