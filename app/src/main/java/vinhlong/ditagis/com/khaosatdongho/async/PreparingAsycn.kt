package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.DPreference
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

@SuppressLint("StaticFieldLeak")
class PreparingAsycn(private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Void, Void, ArrayList<LayerInfoDTG>?>() {

    interface AsyncResponse {
        fun processFinish(layerInfoDTGs: ArrayList<LayerInfoDTG>?)
    }

    override fun doInBackground(vararg params: Void): ArrayList<LayerInfoDTG>? {
        try {
            val url = URL(Constant.API_URL.LAYER_INFO)
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
              return  pajsonRouteJSon(buffer.toString())
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
            //            ListFeatureLayerDTGDB listFeatureLayerDTGDB = new ListFeatureLayerDTGDB(mActivity);
            //            ListObjectDB.getInstance().setLstFeatureLayerDTG(listFeatureLayerDTGDB.find(DPreference.getInstance().loadPreference(
            //                    mActivity.getString(R.string.preference_username)
            //            )));
        } catch (e: Exception) {
            Log.e("Lỗi lấy danh sách DMA", e.toString())
        }

        return null
    }

    override fun onProgressUpdate(vararg values: Void) {
        super.onProgressUpdate(*values)


    }

    override fun onPostExecute(value: ArrayList<LayerInfoDTG>?) {
        //        if (khachHang != null) {
        this.mDelegate.processFinish(value)
        //        }
    }

    @Throws(JSONException::class)
    private fun pajsonRouteJSon(data: String?): ArrayList<LayerInfoDTG>? {
        if (data == null)
            return null
        val myData = "{ \"layerInfo\": $data}"
        val jsonData = JSONObject(myData)
        val jsonRoutes = jsonData.getJSONArray("layerInfo")
        val layerDTGS = ArrayList<LayerInfoDTG>()
        for (i in 0 until jsonRoutes.length()) {
            val jsonRoute = jsonRoutes.getJSONObject(i)


            //           LayerInfoDTG layerInfoDTG = new LayerInfoDTG();
            layerDTGS.add(LayerInfoDTG(jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_id)),
                    jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_title)),
                    jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_url)),
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_iscreate)), jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isdelete)),
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isedit)), jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isview)),
                    jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_outfield)), jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_definition))))


        }
        return layerDTGS

    }

}
