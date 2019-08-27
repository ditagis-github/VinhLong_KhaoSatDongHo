package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

@SuppressLint("StaticFieldLeak")
class GetTenMauAsycn(private val mActivity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<Void, String, Void>() {

    interface AsyncResponse {
        fun processFinish(tenMaus: String?)
    }


    override fun doInBackground(vararg params: Void): Void? {
        try {
            val url = URL(Constant.API_URL.TENMAU_LIST)
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
                    if (line == null) break
                    buffer.append(line)
                }
                publishProgress(buffer.toString())
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }

        return null
    }

    override fun onProgressUpdate(vararg values: String) {
        super.onProgressUpdate(*values)
        if (values == null || values.isEmpty())
            this.mDelegate.processFinish(null)
        else
            this.mDelegate.processFinish(values[0])

    }

    override fun onPostExecute(value: Void) {}
}
