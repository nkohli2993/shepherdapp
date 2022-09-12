package com.shepherdapp.app.ui.component.carePoints.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.data.dto.chat.MessageGroupData
import com.shepherdapp.app.databinding.AdapterCommentBinding
import com.shepherdapp.app.utils.extensions.getChatDate
import com.shepherdapp.app.view_model.CreatedCarePointsViewModel
import java.util.*

class CarePointEventCommentAdapter(
    private var viewModel: CreatedCarePointsViewModel,
    var commentList: ArrayList<MessageGroupData> = ArrayList(),
) :
    RecyclerView.Adapter<CarePointEventCommentAdapter.CarePointsEventsViewHolder>() {
    lateinit var binding: AdapterCommentBinding
    lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarePointsEventsViewHolder {
        context = parent.context
        binding =
            AdapterCommentBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CarePointsEventsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return commentList.size

    }

    override fun onBindViewHolder(holder: CarePointsEventsViewHolder, position: Int) {
        holder.bind(position)
    }


    inner class CarePointsEventsViewHolder(private val itemBinding: AdapterCommentBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        @SuppressLint("SimpleDateFormat", "SetTextI18n")
        fun bind(position: Int) {
            val chatData = commentList[position]

            binding.dateTV.text = chatData.date.getChatDate("yyyy-MM-dd")
            val adapter = CarePointChatAdapter(viewModel)
            itemBinding.messageRV.adapter = adapter
            adapter.addData(chatData.messageList)

            /* itemBinding.let {
                 Picasso.get().load(commentData.user_details.profilePhoto)
                     .placeholder(R.drawable.ic_defalut_profile_pic)
                     .into(it.imageViewUser)
                 if(commentData.user_details.id == carePointsViewModel.getUserDetail()?.userId){
                     it.tvUsername.text = "Me"
                     it.viewCL.setBackgroundResource(R.drawable.rounded_box_green)
                 }
                 else{
                     it.tvUsername.text = commentData.user_details.firstname.plus(" ")
                         .plus(if(commentData.user_details.lastname == null) "" else commentData.user_details.lastname)
                     it.viewCL.setBackgroundResource(R.drawable.rounded_box_grey)
                 }

                 it.appCompatTextViewMessage.text = commentData.comment
                 //setDate of comment
                 val dateTime = (commentData.created_at?:"").replace(".000Z","").replace("T"," ")
                 val commentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(
                     dateTime
                 )
                 val df: SimpleDateFormat =
                     SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
                 df.timeZone = TimeZone.getTimeZone("UTC")
                 val date: Date = df.parse(SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(commentTime!!))!!
                 df.timeZone = TimeZone.getDefault()
                 it.tvtime.text = SimpleDateFormat("hh:mm a").format(date);
             }*/
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

   /* fun updateAddedComment(commentAddedList: ArrayList<EventCommentUserDetailModel>) {
        this.commentList = commentAddedList
        notifyDataSetChanged()
    }*/

    fun addData(chatData: MutableList<MessageGroupData>) {
        this.commentList.clear()
        this.commentList.addAll(chatData)
        notifyDataSetChanged()
    }


}