package vinhlong.ditagis.com.khaosatdongho.utities

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
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

        vibrate(activity, 2000)
    }

    private fun vibrate(activity: Activity, miliSeconds: Long) {
        val v = activity.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(miliSeconds, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            //deprecated in API 26
            v.vibrate(miliSeconds)
        }
    }
}