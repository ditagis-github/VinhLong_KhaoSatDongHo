package vinhlong.ditagis.com.khaosatdongho

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TextInputEditText
import android.support.design.widget.TextInputLayout
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.CodedValue
import com.esri.arcgisruntime.data.CodedValueDomain
import kotlinx.android.synthetic.main.activity_update.*
import vinhlong.ditagis.com.khaosatdongho.async.EditAsync
import vinhlong.ditagis.com.khaosatdongho.async.LoadingDataFeatureAsync
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import java.util.*

class UpdateActivity : AppCompatActivity() {
    private lateinit var mApplication: DApplication


    private var mmSwipe: SwipeRefreshLayout? = null
    private var mArcGISFeature: ArcGISFeature? = null
    private var mLLayoutField: LinearLayout? = null

    private val attributes: HashMap<String, Any>
        get() {
            val attributes = HashMap<String, Any>()
            if (mLLayoutField == null) return attributes
            var currentAlias = ""
            for (i in 0 until mLLayoutField!!.childCount) {
                val itemAddFeature = mLLayoutField!!.getChildAt(i) as LinearLayout
                for (j in 0 until itemAddFeature.childCount) {
                    val typeInputItemAddFeature = itemAddFeature.getChildAt(j) as TextInputLayout
                    if (typeInputItemAddFeature.visibility == View.VISIBLE) {
                        currentAlias = typeInputItemAddFeature.hint!!.toString()
                        val view = (typeInputItemAddFeature.getChildAt(0) as FrameLayout).getChildAt(0)

                        if (view is TextInputEditText && !currentAlias.isEmpty()) {
                            for (fieldEdittext in mApplication!!.dongHoKHSFT!!.fields) {
                                if (fieldEdittext.alias == currentAlias) {
                                    if (fieldEdittext.domain != null) {
                                        val codedValues = (fieldEdittext.domain as CodedValueDomain).codedValues


                                        val valueDomain = getCodeDomain(codedValues, view.text.toString())
                                        if (valueDomain != null)
                                            attributes[currentAlias] = valueDomain.toString()
                                    } else {
                                        attributes[currentAlias] = view.text.toString()
                                    }
                                    break
                                }
                            }
                        } else if (view is Spinner && !currentAlias.isEmpty()) {
                            for (fieldSpinner in mApplication!!.dongHoKHSFT!!.fields) {
                                if (fieldSpinner.alias == currentAlias) {
                                    if (fieldSpinner.domain != null) {
                                        val codedValues = (fieldSpinner.domain as CodedValueDomain).codedValues

                                        val valueDomain = getCodeDomain(codedValues, view.selectedItem.toString())
                                        if (valueDomain != null)
                                            attributes[currentAlias] = valueDomain.toString()
                                    } else {
                                    }
                                    break
                                }
                            }
                        }

                    }
                }
            }
            return attributes
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update)
        mApplication = application as DApplication
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayShowHomeEnabled(true)
        }
        initViews()
    }

    private fun update() {
        (this@UpdateActivity.findViewById<View>(R.id.llayout_update_feature_progress) as LinearLayout).visibility = View.VISIBLE
        (this@UpdateActivity.findViewById<View>(R.id.llayout_update_feature_main) as LinearLayout).visibility = View.GONE
        (this@UpdateActivity.findViewById<View>(R.id.txt_update_feature_progress) as TextView).text = "Đang lưu..."
        EditAsync(container_update, this@UpdateActivity, mApplication!!.dongHoKHSFT!!,
                mApplication!!.selectedFeature!!, object : EditAsync.AsyncResponse {
            override fun processFinish(isSuccess: Boolean?) {
                this@UpdateActivity.findViewById<View>(R.id.llayout_update_feature_progress).visibility = View.GONE
                (this@UpdateActivity.findViewById<View>(R.id.llayout_update_feature_main) as LinearLayout).visibility = View.VISIBLE
                if (isSuccess != null) {
                    Toast.makeText(this@UpdateActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                } else
                    Toast.makeText(this@UpdateActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
            }

        }).execute(attributes)

    }

    private fun getCodeDomain(codedValues: List<CodedValue>, value: String): Any? {
        var code: Any? = null
        for (codedValue in codedValues) {
            if (codedValue.name == value) {
                code = codedValue.code
                break
            }
        }
        return code
    }

    private fun initViews() {
        mLLayoutField = findViewById(R.id.llayout_update_feature_field)

        findViewById<View>(R.id.btn_update_feature_update).setOnClickListener { update() }
        mmSwipe = findViewById(R.id.swipe_udpate_feature)

        (findViewById<View>(R.id.txt_update_feature_progress) as TextView).text = "Đang khởi tạo thuộc tính..."
        findViewById<View>(R.id.llayout_update_feature_progress).visibility = View.VISIBLE
        findViewById<View>(R.id.llayout_update_feature_main).visibility = View.GONE
        mArcGISFeature = mApplication!!.selectedFeature

        mmSwipe!!.setOnRefreshListener {
            loadData()
            mmSwipe!!.isRefreshing = false
        }

        loadData()
    }

    private fun loadData() {
        mLLayoutField!!.removeAllViews()
        findViewById<View>(R.id.llayout_update_feature_progress).visibility = View.VISIBLE
        findViewById<View>(R.id.llayout_update_feature_main).visibility = View.GONE

        mApplication.progressDialog.show(this@UpdateActivity, container_update, "Đang lấy thông tin...")
        LoadingDataFeatureAsync(this@UpdateActivity, mApplication!!.selectedFeature, object : LoadingDataFeatureAsync.AsyncResponse {
            override fun processFinish(views: List<View>?) {
                if (views != null) {
                    for (view in views) {
                        mLLayoutField!!.addView(view)
                    }
                    (this@UpdateActivity.findViewById<View>(R.id.llayout_update_feature_progress) as LinearLayout).visibility = View.GONE
                    (this@UpdateActivity.findViewById<View>(R.id.llayout_update_feature_main) as LinearLayout).visibility = View.VISIBLE
                }
                mApplication.progressDialog.dismiss()
            }


        }).execute()


    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        try {
            val intent = Intent()
            setResult(Activity.RESULT_CANCELED, intent)
            finish()
        } catch (ex: Exception) {
        }
    }
}
