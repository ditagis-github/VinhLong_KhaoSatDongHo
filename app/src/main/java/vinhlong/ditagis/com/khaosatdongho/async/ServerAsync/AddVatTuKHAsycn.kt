package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.DPreference
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@SuppressLint("StaticFieldLeak")
class AddVatTuKHAsycn( private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<ArrayList<VatTuApdapter.VatTu>, Void, Boolean>() {

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }



    private fun getCred(vatTu: VatTuApdapter.VatTu): JSONObject {
        val cred = JSONObject()
        try {
            if (vatTu.maVatTu != null) {
                cred.put(Constant.VatTuFields.MaVatTu, vatTu.maVatTu)
            }
            if (vatTu.soLuongVatTu != null) {
                cred.put(Constant.VatTuFields.SoLuong, vatTu.soLuong)
            }
            if (vatTu.getiDKhachHang() != -1L) {
                cred.put(Constant.VatTuFields.ID, vatTu.getiDKhachHang())
                //                cred.put(Constant.VatTuFields.ID, 12345);
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return cred
    }

    override fun doInBackground(vararg params: ArrayList<VatTuApdapter.VatTu>): Boolean? {
        val vatTus = params[0]
        var countSuccess = 0
        if (vatTus != null && vatTus.size > 0) {
            try {
                val url = URL(Constant.API_URL.INSERT_VATTU)
                for (vatTu in vatTus) {
                    val conn = url.openConnection() as HttpURLConnection
                    try {
                        conn.requestMethod = Constant.METHOD.POST
                        conn.setRequestProperty("Authorization", DPreference.instance.loadPreference(mActivity.getString(R.string.preference_login_api)))
                        conn.setRequestProperty("Content-Type", "application/json")
                        val outputStream = conn.outputStream
                        val wr = OutputStreamWriter(outputStream)
                        val cred = getCred(vatTu)
                        wr.write(cred.toString())
                        wr.flush()
                        conn.connect()
                        conn.inputStream
                        wr.close()
                        outputStream.close()
                        countSuccess++
                    } catch (e: Exception) {
                        Log.e("Lỗi thêm vật tư", e.toString())
                    } finally {
                        conn.disconnect()
                    }
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message, e)
            }

        }
        return countSuccess == vatTus.size
    }


    override fun onPostExecute(success: Boolean?) {

        mDelegate.processFinish(success)
    }
}