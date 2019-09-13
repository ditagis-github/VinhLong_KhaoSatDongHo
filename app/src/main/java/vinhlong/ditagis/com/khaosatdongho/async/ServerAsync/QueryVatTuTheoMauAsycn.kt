package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync

import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*


class QueryVatTuTheoMauAsycn(private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<String, ArrayList<QueryVatTuTheoMauAsycn.VatTu>, Void>() {

    interface AsyncResponse {
        fun processFinish(vatTus: ArrayList<VatTu>?)
    }

    override fun doInBackground(vararg params: String): Void? {
        try {
            if (params != null && params.isNotEmpty()) {
                val tenThietLapMau = params[0]
                val urlAPI = String.format(Constant.API_URL.VATTU_THEOMAU, tenThietLapMau)
                val url = URL(urlAPI)
                val conn = url.openConnection() as HttpURLConnection
                try {
                    conn.doOutput = false
                    conn.requestMethod = Constant.METHOD.GET
                    conn.connect()
                    val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                    val buffer = StringBuffer()
                    var line: String?
                    while (true) {
                        line = bufferedReader.readLine()
                        if(line == null) break
                        buffer.append(line)
                    }
                    val vatTus = getVatTus(buffer.toString())
                    publishProgress(vatTus)
                } catch (e: Exception) {
                    Log.e("error", e.toString())
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
    private fun getVatTus(data: String?): ArrayList<VatTu> {
        val vatTus = ArrayList<VatTu>()
        if (data != null) {
            val jsonData = JSONObject(data)
            val jsonArray = jsonData.getJSONArray("value")
            for (i in 0 until jsonArray.length()) {
                val vatTu = VatTu()
                val jsonObject = jsonArray.getJSONObject(i)
                val maVatTu = jsonObject.get("MaVatTu")
                if (maVatTu != null) {
                    vatTu.maVatTu = maVatTu.toString()
                }
                val tenMau = jsonObject.get("TENMAU")
                if (tenMau != null) {
                    vatTu.tenMau = tenMau.toString()
                }
                val soLuong = jsonObject.get("SoLuong")
                if (soLuong != null) {
                    vatTu.soLuong = soLuong as Double
                }
                val id = jsonObject.get("ID")
                if (id != null) {
                    vatTu.id = id.toString()
                }
                vatTus.add(vatTu)
            }
        }
        return vatTus
    }

    override fun onProgressUpdate(vararg values: ArrayList<VatTu>) {
        super.onProgressUpdate(*values)
        if (values == null || values.size == 0)
            this.mDelegate.processFinish(null)
        this.mDelegate.processFinish(values[0])

    }

    inner class VatTu {
        var maVatTu: String? = null
        var tenMau: String? = null
        var soLuong: Double = 0.toDouble()
        var id: String? = null

        constructor()
    }
}
