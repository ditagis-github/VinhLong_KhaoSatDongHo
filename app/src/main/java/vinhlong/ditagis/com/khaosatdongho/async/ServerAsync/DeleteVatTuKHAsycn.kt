package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.util.Log
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.DPreference
import java.net.HttpURLConnection
import java.net.URL

class DeleteVatTuKHAsycn(activity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Long, Void, Boolean>() {
    private val mContext: Context
    private val mActivity: Activity

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }

    init {
        this.mContext = activity
        this.mActivity = activity
    }


    override fun doInBackground(vararg params: Long?): Boolean? {
        if (params != null && params.size > 0) {
            try {
                val idKhachHang = params[0]
                val urlAPI = String.format(Constant.API_URL.DELETE_VATTUS, idKhachHang)
                val url = URL(urlAPI)
                val conn = url.openConnection() as HttpURLConnection
                try {
                    conn.doOutput = true
                    conn.requestMethod = Constant.METHOD.DELETE
                    conn.setRequestProperty("Authorization", DPreference.instance.loadPreference(mContext.getString(R.string.preference_login_api)))
                    conn.connect()
                    conn.inputStream
                } catch (e: Exception) {
                    Log.e("Lỗi xóa vật tư", e.toString())
                    return false
                } finally {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message, e)
            }

        }
        return true
    }


    override fun onPostExecute(rs: Boolean?) {
        this.mDelegate.processFinish(rs)
    }

}