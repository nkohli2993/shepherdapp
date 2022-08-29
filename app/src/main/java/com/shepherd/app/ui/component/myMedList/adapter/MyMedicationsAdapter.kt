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
import com.shepherd.app.data.dto.med_list.loved_one_med_list.UserMedicationData
import com.shepherd.app.databinding.AdapterMyMedicationsListBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.MedListAction
import com.shepherd.app.view_model.MyMedListViewModel


class MyMedicationsAdapter(
    private val viewModel: MyMedListViewModel,
    var payload: MutableList<UserMedicationData> = ArrayList()
) :
    RecyclerView.Adapter<MyMedicationsAdapter.MyMedicationsViewHolder>() {
    lateinit var binding: AdapterMyMedicationsListBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
             viewModel.openMedicineDetail(itemData[0] as UserMedicationData)
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
        holder.bind(payload[position], onItemClickListener)
    }


    inner class MyMedicationsViewHolder(private val itemBinding: AdapterMyMedicationsListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(medList: UserMedicationData, recyclerItemListener: RecyclerItemListener) {
            itemBinding.data = medList.medlist
            itemBinding.root.setOnClickListener {
                medList.let { it1 ->
                    medList.actionType = MedListAction.View.value
                    medList.deletePosition = absoluteAdapterPosition
                    recyclerItemListener.onItemSelected(
                        it1
                    )
                }
            }
            itemBinding.imgMore.setOnClickListener {
                showPopupReportPost(
                    absoluteAdapterPosition,
                    itemBinding.imgMore,
                    recyclerItemListener, medList
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

    fun addData(payload: ArrayList<UserMedicationData>) {
//        this.payload.clear()
        this.payload = payload
        notifyDataSetChanged()
    }

    private fun popupLocateView(v: View?): Rect? {
        val locInt = IntArray(2)
        if (v == null) return null
        try {
            v.getLocationOnScreen(locInt)
        } catch (npe: NullPointerException) {
            //Happens when the view doesn't exist on screen anymore.
            return null
        }
        val location = Rect()
        location.left = locInt[0]
        location.top = locInt[1]
        location.right = location.left + v.width
        location.bottom = location.top + v.height
        return location
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun showPopupReportPost(
        position: Int,
        view: AppCompatImageView,
        itemListener: RecyclerItemListener,
        medList: UserMedicationData
    ) {
        val popupView: View =
            LayoutInflater.from(this@MyMedicationsAdapter.context).inflate(
                R.layout.popup_report,
                null
            )
        val width: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val height: Int = LinearLayout.LayoutParams.WRAP_CONTENT
        val focusable = true // lets taps outside the popup also dismiss it
        val popupWindow = PopupWindow(popupView, width, height, focusable)
        val editTV: AppCompatTextView = popupView.findViewById(R.id.editTV) as AppCompatTextView
        val deleteTV: AppCompatTextView =
            popupView.findViewById(R.id.deleteTV) as AppCompatTextView
        popupWindow.showAtLocation(
            popupView,
            Gravity.TOP or Gravity.LEFT,
            popupLocateView(view)?.left!!,
            popupLocateView(view)?.top!!
        )
        // show the dim background
        val container: View = if (popupWindow.background == null) {
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
        val context: Context = popupWindow.contentView.context
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.6f
        p.horizontalMargin = 10.0f
        wm.updateViewLayout(container, p)
        popupView.setOnTouchListener { v, event ->
            popupWindow.dismiss()
            true
        }

        editTV.setOnClickListener {
            medList.actionType = MedListAction.EDIT.value
            medList.deletePosition = position
            itemListener.onItemSelected(
                medList
            )
            popupWindow.dismiss()
        }
        deleteTV.setOnClickListener {
            //add delete scehduled medication
            medList.actionType = MedListAction.Delete.value
            medList.deletePosition = position
            itemListener.onItemSelected(
                medList
            )
            popupWindow.dismiss()
        }

    }
}