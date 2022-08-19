package com.shepherd.app.ui.component.myMedList.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.shepherd.app.R
import com.shepherd.app.data.dto.med_list.loved_one_med_list.MedListReminder
import com.shepherd.app.databinding.AdapterSelectedDayMedicineBinding
import com.shepherd.app.ui.base.listeners.RecyclerItemListener
import com.shepherd.app.utils.MedListAction
import com.shepherd.app.utils.extensions.showError
import com.shepherd.app.view_model.MyMedListViewModel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@SuppressLint("SimpleDateFormat")
class SelectedDayMedicineAdapter(
    private val viewModel: MyMedListViewModel,
    var medListReminderList: MutableList<MedListReminder> = ArrayList()
) :
    RecyclerView.Adapter<SelectedDayMedicineAdapter.SelectedDayMedicineViewHolder>() {
    lateinit var binding: AdapterSelectedDayMedicineBinding
    lateinit var context: Context


    private val onItemClickListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.openMedDetail(itemData[0] as MedListReminder)
        }
    }

    private val onItemCheckedListener: RecyclerItemListener = object : RecyclerItemListener {
        override fun onItemSelected(vararg itemData: Any) {
            viewModel.selectedMedication(itemData[0] as MedListReminder)
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
        return medListReminderList.size
    }

    override fun onBindViewHolder(holder: SelectedDayMedicineViewHolder, position: Int) {
        holder.bind(medListReminderList[position], onItemClickListener, onItemCheckedListener)
    }


    inner class SelectedDayMedicineViewHolder(private val itemBinding: AdapterSelectedDayMedicineBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(
            medListReminder: MedListReminder?,
            recyclerItemListener: RecyclerItemListener,
            recyclerItemListener1: RecyclerItemListener
        ) {
            itemBinding.data = medListReminder
            itemBinding.medicationCL.setOnClickListener {
                medListReminder?.let { it1 ->
                    recyclerItemListener.onItemSelected(
                        it1
                    )
                }
            }
            itemBinding.cbReminder.isChecked = false
            itemBinding.cbReminder.setOnCheckedChangeListener { compoundButton, _isChecked ->
                if (compoundButton.isPressed) {
                    if (itemBinding.cbReminder.isChecked) {
                        val (selectedDate: Date, currentDate) = clickDate(medListReminder)
                        if (selectedDate.after(currentDate)) {
                            itemBinding.cbReminder.isChecked = false
                            showError(
                                context,
                                context.getString(R.string.not_allowed_to_update_future_medication)
                            )
                        } else {
                            medListReminder?.isSelected = _isChecked
                            medListReminder?.let { recyclerItemListener1.onItemSelected(it) }
                        }
                    } else {
                        itemBinding.cbReminder.isChecked = true
                        showError(
                            context,
                            context.getString(R.string.this_medication_record_marked_as_complete)
                        )
                    }

                }
            }
            itemBinding.cbReminder.isChecked = false
            if(medListReminder?.isSelected == true){
                itemBinding.cbReminder.isChecked = true
            }

            val (selectedDate: Date, currentDate) = clickDate(medListReminder)
            itemBinding.cbReminder.setButtonDrawable(R.drawable.checkbox_selector)
            if (selectedDate.after(currentDate)) {
                itemBinding.cbReminder.setButtonDrawable(R.drawable.checkbox_selectot_grey)
            }
            itemBinding.imgMore.setOnClickListener {

                showPopupReportPost(
                    absoluteAdapterPosition,
                    itemBinding.imgMore,
                    recyclerItemListener, medListReminder
                )
            }
        }

        private fun clickDate(medListReminder: MedListReminder?): Pair<Date, Date?> {
            val selectedDate: Date =
                SimpleDateFormat("yyyy-MM-dd").parse(medListReminder?.selectedDate)
            val current = SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().time)
            val currentDate = SimpleDateFormat("yyyy-MM-dd").parse(current)
            return Pair(selectedDate, currentDate)
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
            medList: MedListReminder?
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
                medList?.medlist?.actionType = MedListAction.EDIT.value
                medList?.medlist?.deletePosition = position
                itemListener.onItemSelected(
                    medList!!
                )
                popupWindow.dismiss()
            }
            deleteTV.setOnClickListener {
                //add delete scehduled medication
                medList?.medlist?.actionType = MedListAction.Delete.value
                medList?.medlist?.deletePosition = position
                itemListener.onItemSelected(
                    medList!!
                )
                popupWindow.dismiss()
            }

        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    fun addData(medListReminderList: ArrayList<MedListReminder>) {
//        this.medListReminderList.clear()
        this.medListReminderList = medListReminderList
        notifyDataSetChanged()
    }

}