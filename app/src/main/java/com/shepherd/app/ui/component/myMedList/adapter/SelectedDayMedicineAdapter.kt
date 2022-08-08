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
<<<<<<< HEAD
import com.shepherd.app.databinding.AdapterSelectedDayMedicineBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.extensions.showInfo
=======
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Medlist
import com.shepherd.app.data.dto.med_list.loved_one_med_list.Payload
import com.shepherd.app.databinding.AdapterSelectedDayMedicineBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
>>>>>>> b94fcb01d70a12cb18efce56683f01e28c20dbe8
import com.shepherd.app.view_model.MedListViewModel
import com.shepherd.app.view_model.MyMedListViewModel


class SelectedDayMedicineAdapter(
    private val viewModel: MyMedListViewModel,
    var payload: MutableList<Payload> = ArrayList()
) :
    RecyclerView.Adapter<SelectedDayMedicineAdapter.SelectedDayMedicineViewHolder>() {
    lateinit var binding: AdapterSelectedDayMedicineBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
<<<<<<< HEAD
            viewModel.openMedDetail(itemData[0] as String)
=======
//            viewModel.openMedDetail(itemData[0] as Medlist)
>>>>>>> b94fcb01d70a12cb18efce56683f01e28c20dbe8
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SelectedDayMedicineViewHolder {
        context = parent.context
        binding =
            AdapterSelectedDayMedicineBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return SelectedDayMedicineViewHolder(binding)
    }

    override fun getItemCount(): Int {
        //  return requestList.size
        return payload.size
    }

    override fun onBindViewHolder(holder: SelectedDayMedicineViewHolder, position: Int) {
//        holder.bind(requestList[position], onItemClickListener)
        holder.bind(payload[position].medlist, onItemClickListener)
    }


    inner class SelectedDayMedicineViewHolder(private val itemBinding: AdapterSelectedDayMedicineBinding) :
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
<<<<<<< HEAD
                showPopupReportPost(
=======
                openEditOption(
>>>>>>> b94fcb01d70a12cb18efce56683f01e28c20dbe8
                    absoluteAdapterPosition,
                    itemBinding.imgMore,
                    context,
                    recyclerItemListener
                )
<<<<<<< HEAD
            }
        }

        private fun popupLocateView(v: View?): Rect? {
            val locInt = IntArray(2)
            if (v == null) return null
            try {
                v.getLocationOnScreen(locInt)
            } catch (npe: NullPointerException) {
                //Happens when the view doesn't exist on screen anymore.
                return null
=======
>>>>>>> b94fcb01d70a12cb18efce56683f01e28c20dbe8
            }
            val location = Rect()
            location.left = locInt[0]
            location.top = locInt[1]
            location.right = location.left + v.width
            location.bottom = location.top + v.height
            return location
        }

<<<<<<< HEAD
        @SuppressLint("ClickableViewAccessibility")
        private fun showPopupReportPost(
            position: Int,
            view: AppCompatImageView,
            ctx: Context,
            itemListener: RecyclerItemListener
        ) {
            val popupView: View =
                LayoutInflater.from(this@SelectedDayMedicineAdapter.context).inflate(
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
                showInfo(context, "Edit")
            }
            deleteTV.setOnClickListener {
                showInfo(context, "delete")
            }

        }
=======
        private fun openEditOption(
            position: Int,
            optionsImg: AppCompatImageView,
            context: Context,
            recyclerItemListener: RecyclerItemListener
        ) {

            val popup = PopupMenu(context, optionsImg)
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

>>>>>>> b94fcb01d70a12cb18efce56683f01e28c20dbe8
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addData(payload: ArrayList<Payload>) {
//        this.requestList.clear()
//        this.requestList.addAll(dashboard)
        this.payload = payload
        notifyDataSetChanged()
    }

}