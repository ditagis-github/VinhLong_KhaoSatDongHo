package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.widget.LinearLayout
import android.widget.TextView

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Attachment
import com.esri.arcgisruntime.data.FeatureEditResult
import com.esri.arcgisruntime.data.ServiceFeatureTable
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*
import java.util.concurrent.ExecutionException

import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant

/**
 * Created by ThanLe on 4/16/2018.
 */

class UpdateAttachmentAsync(private val mActivity: MainActivity, selectedArcGISFeature: ArcGISFeature, private val mImage: ByteArray, private val mDelegate: AsyncResponse) : AsyncTask<Void, Boolean, Void>() {
    private var mDialog: BottomSheetDialog? = null
    private val mServiceFeatureTable: ServiceFeatureTable
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }

    init {
        mServiceFeatureTable = selectedArcGISFeature.featureTable as ServiceFeatureTable
        mSelectedArcGISFeature = selectedArcGISFeature
        this.mApplication = mActivity.application as DApplication
    }

    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mActivity)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang cập nhật thông tin..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()
    }

    override fun doInBackground(vararg params: Void): Void? {
        val attachmentName = String.format(Constant.AttachmentName.UPDATE,
                mApplication.user?.userName, System.currentTimeMillis())
        val addResult = mSelectedArcGISFeature!!.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName)
        addResult.addDoneListener {
            try {
                val attachment = addResult.get()
                if (attachment.size > 0) {
                    val voidListenableFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature!!)
                    voidListenableFuture.addDoneListener {
                        val applyEditsAsync = mServiceFeatureTable.applyEditsAsync()
                        applyEditsAsync.addDoneListener {
                            try {
                                val featureEditResults = applyEditsAsync.get()
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
                                e.printStackTrace()
                                publishProgress()
                            } catch (e: ExecutionException) {
                                e.printStackTrace()
                                publishProgress()
                            }


                        }


                    }
                }

            } catch (e: InterruptedException) {
                e.printStackTrace()
                publishProgress()
            } catch (e: ExecutionException) {
                e.printStackTrace()
                publishProgress()
            }
        }
        return null
    }

    protected override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
        if (values != null && values.isNotEmpty() && values[0]!!) {
            mDelegate.processFinish(true)
        } else
            mDelegate.processFinish(false)

    }


}

