package ditagis.binhduong.utities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import vinhlong.ditagis.com.khaosatdongho.R

class DAlertDialog {
    private var mDialog: Dialog? = null

    fun show(activity: Activity, title: String, vararg message: String) {
        val builder = AlertDialog.Builder(activity, R.style.DDialogBuilder)
        builder.setTitle(title)
            .setPositiveButton("OK") { dialogInterface, _ ->

                dialogInterface.dismiss()
            }.setCancelable(false)
        if (message.isNotEmpty())
            builder.setMessage(message[0])
        mDialog = builder.create()
        mDialog?.show()
    }

}