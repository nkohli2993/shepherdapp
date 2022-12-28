package com.shepherdapp.app.ui.component.resources.adapter

import android.content.Context
import android.graphics.Color
import android.text.Html
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
import com.shepherdapp.app.databinding.AdapterMedicalHistoryTopicsBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.view_model.ResourceViewModel


class MedicalHistoryTopicsAdapter(
    private val viewModel: ResourceViewModel,
    var resourceList: MutableList<AllResourceData> = ArrayList()
) :
    RecyclerView.Adapter<MedicalHistoryTopicsAdapter.MedicalHistoryTopicsViewHolder>() {
    lateinit var binding: AdapterMedicalHistoryTopicsBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openSelectedResource(itemData[0] as AllResourceData)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicalHistoryTopicsViewHolder {
        context = parent.context
        binding =
            AdapterMedicalHistoryTopicsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MedicalHistoryTopicsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return resourceList.size
    }

    override fun onBindViewHolder(holder: MedicalHistoryTopicsViewHolder, position: Int) {
        holder.bind(resourceList[position], onItemClickListener)
    }


    inner class MedicalHistoryTopicsViewHolder(private val itemBinding: AdapterMedicalHistoryTopicsBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(resourceData: AllResourceData, recyclerItemListener: RecyclerItemListener) {
            // Set Title
            itemBinding.textViewTitle.text = resourceData.title

            /*  if (resourceData.title?.length!! > 50) {
                  setNotesClickForLong(
                      resourceData.title!!,
                      true,
                      itemBinding.textViewTitle,
                      resourceData,
                      recyclerItemListener
                  )
              } else {
                  itemBinding.textViewTitle.text = resourceData.title
              }*/


            //Set Description
            val content = Html.fromHtml(resourceData.content ?: "").toString()
            itemBinding.txtDesc.text = content
            /*  if (resourceData.content?.length!! > 50) {
                  setNotesClickForLong(
                      content,
                      true,
                      itemBinding.txtDesc,
                      resourceData,
                      recyclerItemListener
                  )
              } else {
                  val desc = Html.fromHtml(resourceData.content).toString().trim()
                  itemBinding.txtDesc.text = desc

                  *//* itemBinding.txtDesc.text = HtmlCompat.fromHtml(
                     resourceData.content ?: "",
                     HtmlCompat.FROM_HTML_MODE_COMPACT
                 )*//*
            }*/

            /* if(resourceData.thumbnailUrl!=null && resourceData.thumbnailUrl!=""){
                 Picasso.get().load(resourceData.thumbnailUrl)
                     .placeholder(R.drawable.image)
                     .into(itemBinding.imageViewTopic)
             }*/
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

    fun addData(resourceList: MutableList<AllResourceData>, isSearch: Boolean) {
      /*  if (isSearch) {
            this.resourceList.clear()
            this.resourceList.addAll(resourceList)
        } else {
            this.resourceList.addAll(resourceList)
        }*/

        this.resourceList.clear()
        this.resourceList.addAll(resourceList)

        this.resourceList = this.resourceList.distinctBy {
            it.id
        } as ArrayList<AllResourceData>

        notifyDataSetChanged()
    }

}