package vinhlong.ditagis.com.khaosatdongho

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.KeyEvent
import android.view.View
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*

import vinhlong.ditagis.com.khaosatdongho.async.LoginAsycn
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User
import vinhlong.ditagis.com.khaosatdongho.utities.CheckConnectInternet
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.DPreference


class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var mTxtUsername: TextView? = null
    private var mTxtPassword: TextView? = null
    private var isLastLogin: Boolean = false
    private var mTxtValidation: TextView? = null
    private lateinit var mApplication: DApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        mApplication = application as DApplication
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        btnLogin.setOnClickListener(this)
        findViewById<View>(R.id.txt_login_changeAccount).setOnClickListener(this)

        mTxtUsername = findViewById(R.id.txtUsername)
        mTxtPassword = findViewById(R.id.txtPassword)
        mTxtValidation = findViewById(R.id.txt_login_validation)
        create()
        login()
    }

    private fun create() {

        val preference_userName = DPreference.instance.loadPreference(Constant.PreferenceKey.USERNAME)

        //nếu chưa từng đăng nhập thành công trước đó
        //nhập username và password bình thường
        if (preference_userName == null || preference_userName.isEmpty()) {
            findViewById<View>(R.id.layout_login_tool).visibility = View.GONE
            findViewById<View>(R.id.layout_login_username).visibility = View.VISIBLE
            isLastLogin = false
        } else {
            isLastLogin = true
            findViewById<View>(R.id.layout_login_tool).visibility = View.VISIBLE
            findViewById<View>(R.id.layout_login_username).visibility = View.GONE
        }//ngược lại
        //chỉ nhập pasword

    }

    private fun login() {
        if (!CheckConnectInternet.isOnline(this)) {
            mTxtValidation!!.setText(R.string.validate_no_connect)
            mTxtValidation!!.visibility = View.VISIBLE
            return
        }
        mTxtValidation!!.visibility = View.GONE

        val userName: String?
        val passWord: String?
        if (isLastLogin) {
            userName = DPreference.instance.loadPreference(Constant.PreferenceKey.USERNAME)
            passWord = DPreference.instance.loadPreference(Constant.PreferenceKey.PASSWORD)
        } else {
            userName = mTxtUsername!!.text.toString().trim { it <= ' ' }
            passWord = mTxtPassword!!.text.toString().trim { it <= ' ' }
        }
        if (userName!!.isEmpty() || passWord!!.isEmpty()) {
            handleInfoLoginEmpty()
            return
        }
        mApplication.progressDialog.show(this@LoginActivity, container_login, "Đang đăng nhập...")
        val loginAsycn = LoginAsycn(this@LoginActivity, object : LoginAsycn.AsyncResponse {
            override fun processFinish(output: Any) {
                if (output is User) {
                    mApplication!!.user = output
                    handleLoginSuccess(output)
                } else if (output is String) {
                    handleLoginFail(output)
                }
                mApplication.progressDialog.dismiss()
            }


        })
        loginAsycn.execute(userName, passWord)
    }

    private fun handleInfoLoginEmpty() {
        mTxtValidation!!.setText(R.string.info_login_empty)
        mTxtValidation!!.visibility = View.VISIBLE
    }

    private fun handleLoginFail(message: String) {
        mTxtValidation!!.text = message
        mTxtValidation!!.visibility = View.VISIBLE
    }

    private fun handleLoginSuccess(user: User) {
        DPreference.instance.savePreferences(Constant.PreferenceKey.USERNAME, user.userName!!)
        DPreference.instance.savePreferences(Constant.PreferenceKey.PASSWORD, user.passWord!!)
        mTxtUsername!!.text = ""
        mTxtPassword!!.text = ""
        val intent = Intent()
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun changeAccount() {
        mTxtUsername!!.text = ""
        mTxtPassword!!.text = ""

        DPreference.instance.savePreferences(Constant.PreferenceKey.USERNAME, "")
        create()
    }

    override fun onPostResume() {
        super.onPostResume()
        create()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnLogin -> login()
            R.id.txt_login_changeAccount -> changeAccount()
        }

    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_ENTER -> {
                if (mTxtPassword!!.text.toString().trim { it <= ' ' }.length > 0) {
                    login()
                    return true
                }
                return super.onKeyUp(keyCode, event)
            }
            else -> return super.onKeyUp(keyCode, event)
        }
    }
}
