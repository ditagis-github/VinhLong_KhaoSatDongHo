package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.util.Log
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*
import org.json.JSONException
import org.json.JSONObject
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.UserDangNhap
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.Preference
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
@SuppressLint("StaticFieldLeak")
class LoginAsycn(activity: Activity, private val mDelegate: AsyncResponse) : AsyncTask<String, Void, Any>() {
    private val exception: Exception? = null
    private var mDialog: BottomSheetDialog? = null

    private val mContext: Context
    private val mApplication: DApplication = activity.application as DApplication
    private val mActivity: Activity

    private val displayName: String
        get() {
            val displayName = ""
            try {
                val url = URL(Constant.API_URL.DISPLAY_NAME)
                val conn = url.openConnection() as HttpURLConnection
                try {
                    conn.doOutput = false
                    conn.requestMethod = Constant.METHOD.GET
                    conn.setRequestProperty("Authorization", Preference.instance.loadPreference(mContext.getString(R.string.preference_login_api)))
                    conn.connect()

                    val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                    val line = bufferedReader.readLine()
                    pajsonRouteeJSon(line)

                } catch (e: Exception) {
                    Log.e("error", e.toString())
                } finally {
                    conn.disconnect()
                }
            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                return displayName
            }
        }

    interface AsyncResponse {
        fun processFinish(output: Any)
    }

    init {
        this.mContext = activity
        this.mActivity = activity
    }

    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mContext)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang đăng nhập..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)
        mDialog!!.show()

    }

    override fun doInBackground(vararg params: String): Any {
        val userName = params[0]
        val passWord = params[1]
        //        String passEncoded = (new EncodeMD5()).encode(pin + "_DITAGIS");
        // Do some validation here
        val urlParameters = String.format("Username=%s&Password=%s", userName, passWord)
        val urlWithParam = String.format("%s?%s", Constant.API_URL.LOGIN, urlParameters)
        try {
            val url = URL(urlWithParam)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.requestMethod = Constant.METHOD.POST
                val cred = JSONObject()
                cred.put("Username", userName)
                cred.put("Password", passWord)

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conn.setRequestProperty("Accept", "application/json")
                conn.useCaches = false
                val outputStream = conn.outputStream
                val wr = OutputStreamWriter(outputStream)
                wr.write(cred.toString())
                wr.flush()
                wr.close()
                outputStream.close()
                conn.connect()
                val inputStream = conn.inputStream
                val inputStreamReader = InputStreamReader(inputStream)
                val bufferedReader = BufferedReader(inputStreamReader)
                val stringBuilder = StringBuilder()
                val line= bufferedReader.readLine()

                    stringBuilder.append(line)

                bufferedReader.close()
                inputStreamReader.close()
                inputStream.close()
                val token = stringBuilder.toString().replace("\"", "")
                Preference.instance.savePreferences(mContext.getString(R.string.preference_login_api), token)
                if (checkAccess()!!) {
                    val user = User()
                    user.displayName = displayName
                    user.userName = userName
                    user.passWord = passWord
                    conn.disconnect()
                    return user
                } else {
                    conn.disconnect()
                    return "Không có quyền truy cập ứng dụng này"
                }
            } catch (e: Exception) {
                Log.e("ERROR", e.message, e)
                return e.toString()
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.message, e)
            return e.toString()
        }

    }


    override fun onPostExecute(o: Any) {
        //        if (user != null) {
        mDialog!!.dismiss()
        this.mDelegate.processFinish(o)
        //        }
    }

    private fun checkAccess(): Boolean? {
        var isAccess = false
        try {
            val url = URL(Constant.API_URL.IS_ACCESS)
            val conn = url.openConnection() as HttpURLConnection
            try {
                conn.doOutput = false
                conn.requestMethod = Constant.METHOD.GET
                conn.setRequestProperty("Authorization", Preference.instance.loadPreference(mContext.getString(R.string.preference_login_api)))
                conn.connect()

                val bufferedReader = BufferedReader(InputStreamReader(conn.inputStream))
                val line = bufferedReader.readLine()
                if (line == "true")
                    isAccess = true

            } catch (e: Exception) {
                Log.e("error", e.toString())
            } finally {
                conn.disconnect()
            }
        } catch (e: Exception) {
            Log.e("error", e.toString())
        } finally {
            return isAccess
        }
    }

    @Throws(JSONException::class)
    private fun pajsonRouteeJSon(data: String?) {
        if (data != null) {
            val myData = "{ \"account\": [$data]}"
            val jsonData = JSONObject(myData)
            val jsonRoutes = jsonData.getJSONArray("account")
            //        jsonData.getJSONArray("account");
            for (i in 0 until jsonRoutes.length()) {
                val jsonRoute = jsonRoutes.getJSONObject(i)
                val displayName = jsonRoute.getString(mContext.getString(R.string.sql_coloumn_login_displayname))
                val username = jsonRoute.getString(mContext.getString(R.string.sql_coloumn_login_username))
                UserDangNhap.getInstance().user = User()
                UserDangNhap.getInstance().user?.displayName = displayName
                UserDangNhap.getInstance().user?.userName = username
            }
        }
    }
}