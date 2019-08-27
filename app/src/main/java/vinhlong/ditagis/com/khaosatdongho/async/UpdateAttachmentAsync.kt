package vinhlong.ditagis.com.khaosatdongho.async

import android.graphics.Bitmap
import android.os.AsyncTask
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.util.concurrent.ExecutionException

/**
 * Created by ThanLe on 4/16/2018.
 */

class UpdateAttachmentAsync(private val mActivity: MainActivity, selectedArcGISFeature: ArcGISFeature, private val mImage: ByteArray, private val mDelegate: AsyncResponse) : AsyncTask<Void, Boolean, Void>() {
    private val mServiceFeatureTable: ServiceFeatureTable = selectedArcGISFeature.featureTable as ServiceFeatureTable
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private val mApplication: DApplication

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }

    init {
        mSelectedArcGISFeature = selectedArcGISFeature
        this.mApplication = mActivity.application as DApplication
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

     override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values != null && values.isNotEmpty() && values[0]!!) {
            mDelegate.processFinish(true)
        } else
            mDelegate.processFinish(false)

    }


}

