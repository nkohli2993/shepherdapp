package com.app.shepherd.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.app.shepherd.R


object ProgressBarDialog {


    var dialog: Dialog? = null

    fun showProgressBar(activity: Context, title: String? = null) {

        var msg = title
        dismissProgressDialog()

        if (msg == null)
            msg = activity.getString(R.string.loading)
        try {
            if ("".equals(title, ignoreCase = true)) {
                msg = activity.getString(R.string.loading)
            }
            dialog = Dialog(
                activity,
                R.style.AppTheme
            )

            dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

            val layoutParams = dialog!!.window!!.attributes
            layoutParams.dimAmount = 0.4F
            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)

            dialog!!.window!!.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            dialog!!.window!!.statusBarColor =
                ContextCompat.getColor(activity, R.color.colorBlackTrans50)


            dialog!!.setCancelable(false)
            dialog!!.setCanceledOnTouchOutside(false)


            dialog!!.setContentView(R.layout.dialog_progress_bar)
            val tvProgressText = dialog!!.findViewById(R.id.tvProgressText) as TextView

            /* tvProgressText.text = msg
             if (msg.isNullOrEmpty() || msg == " ")
                 tvProgressText.visibility = View.GONE
             else
                 tvProgressText.visibility = View.VISIBLE*/
            dialog!!.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    fun dismissProgressDialog() {
        try {
            if (dialog != null) {
                if (dialog!!.isShowing) {
                    dialog!!.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun isLoading(): Boolean {
        return dialog != null && dialog!!.isShowing
    }


}
