package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync

import android.annotation.SuppressLint
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

@SuppressLint("StaticFieldLeak")
class GetDanhSachVatTuAsycn(private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Void, ArrayList<GetDanhSachVatTuAsycn.VatTu>, Void>() {

    interface AsyncResponse {
        fun processFinish(vatTus: ArrayList<VatTu>?)
    }

    override fun doInBackground(vararg params: Void): Void? {
        try {
            val url = URL(Constant.API_URL.VATTU_LIST)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = Constant.METHOD.GET
                //                conn.setRequestProperty("Authorization", DPreference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect()
                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val buffer = StringBuffer()
                var line: String?
                while (true) {
                    line = bufferedReader.readLine()
                    if (line == null)
                        break
                    buffer.append(line)
                }
                val vatTus = getVatTus(buffer.toString())
                publishProgress(vatTus)
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
                publishProgress()
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
                val tenMau = jsonObject.get("TenVatTu")
                if (tenMau != null) {
                    vatTu.tenVatTu = tenMau.toString()
                }
                val donViTinh = jsonObject.get("DonViTinh")
                if (donViTinh != null) {
                    vatTu.donViTinh = donViTinh.toString()
                }
                val vt = jsonObject.get("VT")
                if (vt != null) {
                    vatTu.setvT(vt.toString())
                }
                val nc = jsonObject.get("NC")
                if (nc != null) {
                    vatTu.setnC(nc.toString())
                }
                val mtc = jsonObject.get("MTC")
                if (mtc != null) {
                    vatTu.setmTC(mtc.toString())
                }
                val maHSDG = jsonObject.get("MaHSDG")
                if (maHSDG != null) {
                    vatTu.maHSDG = maHSDG.toString()
                }
                vatTus.add(vatTu)
            }
        }
        return vatTus
    }

    override fun onProgressUpdate(vararg values: ArrayList<VatTu>) {
        super.onProgressUpdate(*values)
        if (values == null || values.isEmpty())
            this.mDelegate.processFinish(null)
        else
            this.mDelegate.processFinish(values[0])

    }

    override fun onPostExecute(value: Void) {}

    class VatTu {
        var maVatTu: String? = null
        var tenVatTu: String? = null
        var donViTinh: String? = null
        private var vT: String? = null
        private var nC: String? = null
        private var mTC: String? = null
        var maHSDG: String? = null

        constructor() {}

        constructor(maVatTu: String, tenVatTu: String, donViTinh: String, vT: String, nC: String, mTC: String, maHSDG: String) {
            this.donViTinh = donViTinh
            this.maVatTu = maVatTu
            this.tenVatTu = tenVatTu
            this.vT = vT
            this.nC = nC
            this.mTC = mTC
            this.maHSDG = maHSDG
        }

        fun getvT(): String? {
            return vT
        }

        fun setvT(vT: String) {
            this.vT = vT
        }

        fun getnC(): String? {
            return nC
        }

        fun setnC(nC: String) {
            this.nC = nC
        }

        fun getmTC(): String? {
            return mTC
        }

        fun setmTC(mTC: String) {
            this.mTC = mTC
        }
    }
}
