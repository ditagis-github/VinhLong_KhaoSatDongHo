package vinhlong.ditagis.com.khaosatdongho.utities

import android.app.Activity
import android.app.Dialog
import android.support.design.widget.BottomSheetDialog
import android.view.ViewGroup
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*
import vinhlong.ditagis.com.khaosatdongho.R

class DProgressDialog {
    private var mDialog: Dialog? = null
    private lateinit var mLayout: LinearLayout

    fun show(activity: Activity, view: ViewGroup, title: String) {
        mDialog = BottomSheetDialog(activity)
        mLayout =
            activity.layoutInflater.inflate(
                R.layout.layout_progress_dialog,
                view,
                false
            ) as LinearLayout
        mLayout.txt_progress_dialog_title.text = title

        mDialog?.setCancelable(false)
        mDialog?.setContentView(mLayout)


        if (mDialog != null && mDialog!!.isShowing)
            mDialog?.dismiss()
        mDialog?.show()
    }

    fun changeTitle(activity: Activity, view: ViewGroup, title: String) {
        if (mDialog != null && mDialog!!.isShowing) {
            mLayout.txt_progress_dialog_title.text = title
        } else {
            show(activity, view, title)
        }
    }
    fun dismiss() {
        if (mDialog != null && mDialog!!.isShowing)
            mDialog?.dismiss()
    }
}