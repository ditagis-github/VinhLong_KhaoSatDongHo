package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.DPreference
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class QueryVatTuDongHoAsycn(private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Long, ArrayList<VatTuApdapter.VatTu>, Void>() {

    interface AsyncResponse {
        fun processFinish(vatTus: ArrayList<VatTuApdapter.VatTu>?)
    }

     override fun doInBackground(vararg params: Long?): Void? {
        try {
            if (params != null && params.isNotEmpty()) {
                val maKhachHang = params[0]
                val urlAPI = String.format(Constant.API_URL.VATTUS_DONGHO, maKhachHang)
                val url = URL(urlAPI)
                val conn = url.openConnection() as HttpURLConnection
                try {
                    conn.doOutput = false
                    conn.requestMethod = Constant.METHOD.GET
                    conn.setRequestProperty("Authorization", DPreference.instance.loadPreference(mActivity.getString(R.string.preference_login_api)))
                    conn.connect()
                    val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                    val buffer = StringBuffer()
                    var line: String?
                    while (true) {
                        line = bufferedReader.readLine()
                        if (line == null) break
                        buffer.append(line)
                    }
                    val vatTus = getVatTus(buffer.toString())
                    publishProgress(vatTus)
                } catch (e: Exception) {
                    Log.e("error", e.toString())
                    publishProgress()
                } finally {
                    conn.disconnect()

                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }

        return null
    }

    @Throws(JSONException::class)
    private fun getVatTus(data: String?): ArrayList<VatTuApdapter.VatTu> {
        val vatTus = ArrayList<VatTuApdapter.VatTu>()
        if (data != null) {
            val jsonData = JSONObject(data)
            val jsonArray = jsonData.getJSONArray("value")
            for (i in 0 until jsonArray.length()) {
                val vatTu = VatTuApdapter.VatTu(i + 1)
                val jsonObject = jsonArray.getJSONObject(i)
                val maVatTu = jsonObject.get(Constant.VatTuFields.MaVatTu)
                if (maVatTu != null) {
                    vatTu.maVatTu = maVatTu.toString()
                }
                val soLuong = jsonObject.get(Constant.VatTuFields.SoLuong)
                if (soLuong != null) {
                    vatTu.setSoLuongVatTu(soLuong as Double)
                }
                val dDKhachHang = jsonObject.get(Constant.VatTuFields.ID)
                if (dDKhachHang != null) {
                    vatTu.setiDKhachHang((dDKhachHang as Int).toLong())
                }
                val giaNC = jsonObject.get(Constant.VatTuFields.GIA_NC)
                if (giaNC != null) {
                    vatTu.setGiaNC(giaNC.toString())
                }
                vatTus.add(vatTu)
            }
        }
        return vatTus
    }

    override fun onProgressUpdate(vararg values: ArrayList<VatTuApdapter.VatTu>) {
        super.onProgressUpdate(*values)
        if (values == null || values.isEmpty())
            this.mDelegate.processFinish(null)
        else
            this.mDelegate.processFinish(values[0])

    }

}
