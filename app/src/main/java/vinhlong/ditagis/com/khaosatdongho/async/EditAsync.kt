package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.CodedValue
import com.esri.arcgisruntime.data.CodedValueDomain
import com.esri.arcgisruntime.data.Domain
import com.esri.arcgisruntime.data.FeatureEditResult
import com.esri.arcgisruntime.data.FeatureType
import com.esri.arcgisruntime.data.Field
import com.esri.arcgisruntime.data.ServiceFeatureTable

import java.text.ParseException
import java.util.Calendar
import java.util.Date
import java.util.HashMap
import java.util.concurrent.ExecutionException

import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewMoreInfoAdapter
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.MySnackBar

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class EditAsync( private val mapView: View, private val mainActivity: Activity, private val mServiceFeatureTable: ServiceFeatureTable, selectedArcGISFeature: ArcGISFeature, private val mDelegate: AsyncResponse) : AsyncTask<HashMap<String, Any>, Boolean, Void>() {
    private var mDialog: BottomSheetDialog? = null
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private val dApplication: DApplication

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }

    init {
        mSelectedArcGISFeature = selectedArcGISFeature
        this.dApplication = mainActivity.application as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mainActivity)
        val view = mainActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        (view.findViewById<View>(R.id.txt_progress_dialog_title) as TextView).text = "Đang cập nhật thông tin..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()

    }

    override fun doInBackground(vararg params: HashMap<String, Any>): Void? {
        if (params != null && params.size > 0) {
            val attributes = params[0]
            for (alias in attributes.keys) {
                for (field in mServiceFeatureTable.fields) {
                    if (field.alias == alias) {
                        try {
                            val value = attributes[alias]
                            if (value == null)
                                mSelectedArcGISFeature!!.attributes[field.name] = null
                            else {
                                val valueString = value.toString().trim { it <= ' ' }

                                when (field.fieldType) {
                                    Field.Type.TEXT -> mSelectedArcGISFeature!!.attributes[field.name] = valueString
                                    Field.Type.DOUBLE -> {
                                        mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Double.parseDouble(valueString)
                                        mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Float.parseFloat(valueString)
                                        mSelectedArcGISFeature!!.attributes[field.name] = Integer.parseInt(valueString)
                                        mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Short.parseShort(valueString)
                                    }
                                    Field.Type.FLOAT -> {
                                        mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Float.parseFloat(valueString)
                                        mSelectedArcGISFeature!!.attributes[field.name] = Integer.parseInt(valueString)
                                        mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Short.parseShort(valueString)
                                    }
                                    Field.Type.INTEGER -> {
                                        mSelectedArcGISFeature!!.attributes[field.name] = Integer.parseInt(valueString)
                                        mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Short.parseShort(valueString)
                                    }
                                    Field.Type.SHORT -> mSelectedArcGISFeature!!.attributes[field.name] = java.lang.Short.parseShort(valueString)
                                    else -> {
                                    }
                                }
                            }

                        } catch (e: Exception) {
                            mSelectedArcGISFeature!!.attributes[field.name] = null
                            Log.e("Lỗi thêm điểm", e.toString())
                        }

                        break
                    }
                }
            }
        }
        val currentTime = Calendar.getInstance()
        mSelectedArcGISFeature!!.attributes[Constant.DongHoKhachHangFields.NGAY_CAP_NHAT] = currentTime
        mSelectedArcGISFeature!!.attributes[Constant.DongHoKhachHangFields.NGUOI_CAP_NHAT] = this.dApplication.user?.userName
        val voidListenableFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature!!)
        voidListenableFuture.addDoneListener {
            try {
                voidListenableFuture.get()
                val listListenableFuture = mServiceFeatureTable.applyEditsAsync()
                listListenableFuture.addDoneListener {
                    try {
                        val featureEditResults = listListenableFuture.get()
                        if (featureEditResults.size > 0) {
                            if (!featureEditResults[0].hasCompletedWithErrors()) {
                                publishProgress(true)
                            } else {
                                publishProgress()
                            }
                        } else {
                            publishProgress()
                        }
                    } catch (e: InterruptedException) {
                        publishProgress()
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        publishProgress()
                        e.printStackTrace()
                    }


                }
            } catch (e: InterruptedException) {
                publishProgress()
                e.printStackTrace()
            } catch (e: ExecutionException) {
                publishProgress()
                e.printStackTrace()
            }
        }
        return null
    }

    private fun notifyError() {
        publishProgress()
        MySnackBar.make(mapView, "Đã xảy ra lỗi", false)
    }

    private fun getIdFeatureTypes(featureTypes: List<FeatureType>, value: String): Any? {
        var code: Any? = null
        for (featureType in featureTypes) {
            if (featureType.name == value) {
                code = featureType.id
                break
            }
        }
        return code
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

     override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values != null && values[0]!!) {
            mDelegate.processFinish(true)
        } else {
            notifyError()
            mDelegate.processFinish(false)
        }
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
    }

}

