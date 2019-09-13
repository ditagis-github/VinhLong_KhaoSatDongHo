package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.util.Log
import android.view.ViewGroup
import com.esri.arcgisruntime.data.*
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.util.*
import java.util.concurrent.ExecutionException

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class EditAsync(private val mRootView: ViewGroup, private val mActivity: Activity, private val mServiceFeatureTable: ServiceFeatureTable, selectedArcGISFeature: ArcGISFeature, private val mDelegate: AsyncResponse) : AsyncTask<HashMap<String, Any>, Boolean, Void>() {
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }

    init {
        mSelectedArcGISFeature = selectedArcGISFeature
        this.mApplication = mActivity.application as DApplication
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mApplication.progressDialog.show(mActivity, mRootView, "Đang cập nhật...")
    }
    override fun doInBackground(vararg params: HashMap<String, Any>): Void? {
        if (params != null && params.isNotEmpty()) {
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
        mSelectedArcGISFeature!!.attributes[Constant.DongHoKhachHangFields.NV_KHAO_SAT] = this.mApplication.user?.userName
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
        mApplication.alertDialog.show(mActivity, "Có lỗi xả ra")
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
         mApplication.progressDialog.dismiss()
        if (values != null && values[0]!!) {
            mDelegate.processFinish(true)
        } else {
            notifyError()
            mDelegate.processFinish(false)
        }
    }

}

