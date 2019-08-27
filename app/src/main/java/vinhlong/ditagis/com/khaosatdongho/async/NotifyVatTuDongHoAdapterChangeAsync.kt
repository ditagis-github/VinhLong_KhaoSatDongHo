package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.adapter.ChiTietVatTuAdapter

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class NotifyVatTuDongHoAdapterChangeAsync(private val mActivity: Activity) : AsyncTask<ChiTietVatTuAdapter, Void, Void>() {
    private var mDialog: BottomSheetDialog? = null
    private val mContext: Context? = null

    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mContext!!)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang cập nhật giao diện..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()

    }

    override fun doInBackground(vararg params: ChiTietVatTuAdapter): Void? {
        val adapter = params[0]
        try {
            Thread.sleep(500)
            mActivity.runOnUiThread { adapter.notifyDataSetChanged() }
        } catch (e: InterruptedException) {

        }


        return null
    }

    override fun onPostExecute(result: Void) {
        if (mDialog != null || mDialog!!.isShowing)
            mDialog!!.dismiss()
        super.onPostExecute(result)

    }

}
