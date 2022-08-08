package com.shepherd.app.ui.component.myMedList.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Medlist
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.databinding.AdapterMyMedicationsListBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.extensions.showInfo
import com.shepherd.app.view_model.MyMedListViewModel


class MyMedicationsAdapter(
    private val viewModel: MyMedListViewModel,
    var payload: MutableList<Payload> = ArrayList()
) :
    RecyclerView.Adapter<MyMedicationsAdapter.MyMedicationsViewHolder>() {
    lateinit var binding: AdapterMyMedicationsListBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            // viewModel.openDashboardItems(itemData[0] as DashboardModel)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyMedicationsViewHolder {
        context = parent.context
        binding =
            AdapterMyMedicationsListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return MyMedicationsViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return payload.size
    }

    override fun onBindViewHolder(holder: MyMedicationsViewHolder, position: Int) {
        holder.bind(payload[position].medlist, onItemClickListener)
    }


    inner class MyMedicationsViewHolder(private val itemBinding: AdapterMyMedicationsListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(medList: Medlist?, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = medList
            itemBinding.root.setOnClickListener {
                medList?.let { it1 ->
                    recyclerItemListener.onItemSelected(
                        it1
                    )
                }
            }
            itemBinding.imgMore.setOnClickListener {
                showPopupReportPost(context, itemBinding.imgMore)
                openEditOption(
                    absoluteAdapterPosition,
                    itemBinding.imgMore,
                    context,
                    recyclerItemListener
                )
            }
        }

        private fun openEditOption(
            position: Int,
            optionsImg: AppCompatImageView,
            context: Context,
            recyclerItemListener: RecyclerItemListener
        ) {

            val popup = android.widget.PopupMenu(context, optionsImg)
            popup.inflate(R.menu.options_menu_medication)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit_medication -> {

                        true
                    }
                    R.id.delete_medication -> {
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(payload: ArrayList<Payload>) {
//        this.requestList.clear()
//        this.payload.addAll(medLists)
        this.payload.clear()
        this.payload = payload
        notifyDataSetChanged()
    }

    private fun popupLocateView(v: View?): Rect? {
        val loc_int = IntArray(2)
        if (v == null) return null
        try {
            v.getLocationOnScreen(loc_int)
        } catch (npe: NullPointerException) {
            //Happens when the view doesn't exist on screen anymore.
            return null
        }
        val location = Rect()
        location.left = loc_int[0]
        location.top = loc_int[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return location
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopupReportPost(
        context: Context,
        view: View?,
    ) {
        val popupView: View = LayoutInflater.from(context).inflate(
            R.layout.popup_report,
            null
        )
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        val editTV: AppCompatTextView = popupView.findViewById(R.id.editTV) as AppCompatTextView
        val deleteTV: AppCompatTextView = popupView.findViewById(R.id.deleteTV) as AppCompatTextView
        popupWindow.showAtLocation(
            popupView,
            Gravity.TOP or Gravity.LEFT,
            popupLocateView(view)?.left!!,
            popupLocateView(view)?.top!!
        )
        // show the dim background
        val container: View
        container = if (popupWindow.getBackground() == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupWindow.contentView.parent as View
            } else {
                popupWindow.contentView
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                popupWindow.contentView.parent.parent as View
            } else {
                popupWindow.contentView.parent as View
            }
        }
        val context: Context = popupWindow.getContentView().getContext()
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.6f
        wm.updateViewLayout(container, p)
        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }

        editTV.setOnClickListener {
            showInfo(context, "Edit")
        }
        deleteTV.setOnClickListener {
            showInfo(context, "delete")
        }

    }
}