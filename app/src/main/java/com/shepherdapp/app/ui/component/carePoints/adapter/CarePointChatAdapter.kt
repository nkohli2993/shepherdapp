package com.shepherdapp.app.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherdapp.app.R
import com.shepherdapp.app.ShepherdApp
import com.shepherdapp.app.data.dto.chat.MessageData
import com.shepherdapp.app.data.dto.login.UserProfile
import com.shepherdapp.app.databinding.AdapterCarePointChatBinding
import com.shepherdapp.app.ui.base.listeners.RecyclerItemListener
import com.shepherdapp.app.utils.Const
import com.shepherdapp.app.utils.Prefs
import com.shepherdapp.app.utils.extensions.changeDateFormat
import com.shepherdapp.app.utils.setImageFromUrl
import com.shepherdapp.app.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso


class CarePointChatAdapter(
    private val viewModel: CreatedCarePointsViewModel,
    var requestList: MutableList<MessageData> = ArrayList()
) : RecyclerView.Adapter<CarePointChatAdapter.ChatViewHolder>() {
    lateinit var binding: AdapterCarePointChatBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChatViewHolder {
        context = parent.context
        binding =
            AdapterCarePointChatBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return ChatViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return requestList.size
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.bind(requestList[position], onItemClickListener)
    }


    class ChatViewHolder(private val itemBinding: AdapterCarePointChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(messageData: MessageData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.messageData = messageData
            // Get loggedIn User Id
//            val loggedInUserId = Prefs.with(ShepherdApp.appContext)!!.getInt(Const.USER_ID)
            val loggedInUser = Prefs.with(ShepherdApp.appContext)!!.getObject(
                Const.USER_DETAILS,
                UserProfile::class.java
            )
            val loggedInUserId = loggedInUser?.userId

            itemBinding.userId = loggedInUserId.toString()

            itemBinding.executePendingBindings()

            itemBinding.tvReceivedTime.text = messageData.date?.changeDateFormat(
                sourceDateFormat = "yyyy-MM-dd HH:mm:ss",
                targetDateFormat = "hh:mm a"
            )
            itemBinding.tvSendTime.text = messageData.date?.changeDateFormat(
                sourceDateFormat = "yyyy-MM-dd HH:mm:ss",
                targetDateFormat = "hh:mm a"
            )

          //  if (messageData.senderProfilePic != null) {
                if (messageData.senderID == loggedInUserId.toString()) {
                    itemBinding.let {
                        it.imageViewUserSender.setImageFromUrl(
                            messageData.senderProfilePic,
                            messageData.senderName,""
                        )

                    }
                } else {
                    itemBinding.let {
                        it.imageViewUserReceiver.setImageFromUrl(
                            messageData.senderProfilePic,
                            messageData.senderName,""
                        )

                    }
                }


                /* itemBinding.let {
                     Picasso.get().load(messageData.senderProfilePic)
                         .placeholder(R.drawable.ic_defalut_profile_pic)
                         .into(it.imageViewUserSender)
                 }*/
        //    }

            itemBinding.root.setOnClickListener {
                recyclerItemListener.onItemSelected(
                    messageData
                )
            }
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(messageData: MutableList<MessageData>) {
        this.requestList.clear()
        this.requestList.addAll(messageData)
        notifyDataSetChanged()
    }

}