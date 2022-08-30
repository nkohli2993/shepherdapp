package com.shepherd.app.ui.component.carePoints.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.ShepherdApp
import com.shepherd.app.data.dto.chat.MessageData
import com.shepherd.app.databinding.AdapterChatBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.Const
import com.shepherd.app.utils.Prefs
import com.shepherd.app.utils.extensions.changeDateFormat
import com.shepherd.app.view_model.CreatedCarePointsViewModel
import com.squareup.picasso.Picasso


class CarePointChatAdapter(
    private val viewModel: CreatedCarePointsViewModel,
    var requestList: MutableList<MessageData> = ArrayList()
) : RecyclerView.Adapter<CarePointChatAdapter.ChatViewHolder>() {
    lateinit var binding: AdapterChatBinding
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
            AdapterChatBinding.inflate(
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


    class ChatViewHolder(private val itemBinding: AdapterChatBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(messageData: MessageData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.messageData = messageData
            // Get loggedIn User Id
            val loggedInUserId = Prefs.with(ShepherdApp.appContext)!!.getInt(Const.USER_ID)
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

            if (messageData.senderProfilePic != null) {
                if (messageData.senderID == loggedInUserId.toString()) {
                    itemBinding.let {
                        Picasso.get().load(messageData.senderProfilePic)
                            .placeholder(R.drawable.ic_defalut_profile_pic)
                            .into(it.imageViewUserSender)
                    }
                } else {
                    itemBinding.let {
                        Picasso.get().load(messageData.senderProfilePic)
                            .placeholder(R.drawable.ic_defalut_profile_pic)
                            .into(it.imageViewUserReceiver)
                    }
                }


                /* itemBinding.let {
                     Picasso.get().load(messageData.senderProfilePic)
                         .placeholder(R.drawable.ic_defalut_profile_pic)
                         .into(it.imageViewUserSender)
                 }*/
            }

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