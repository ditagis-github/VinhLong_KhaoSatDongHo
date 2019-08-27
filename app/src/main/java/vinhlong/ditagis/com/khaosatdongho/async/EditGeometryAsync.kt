package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import java.util.concurrent.ExecutionException

/**
 * Created by ThanLe on 4/16/2018.
 */

class EditGeometryAsync(@field:SuppressLint("StaticFieldLeak")
                        private val mActivity: Activity, private val mServiceFeatureTable: ServiceFeatureTable,
                        private val mSelectedArcGISFeature: ArcGISFeature, private val mDelegate: AsyncResponse) : AsyncTask<Point, Boolean, Void>() {


    override fun doInBackground(vararg params: Point): Void? {
        if (params != null && params.isNotEmpty()) {
            mSelectedArcGISFeature.geometry = params[0]
            val updateFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature)
            updateFuture.addDoneListener {
                try {
                    // track the update
                    updateFuture.get()
                    // apply edits once the update has completed
                    if (updateFuture.isDone) {
                        applyEditsToServer()
                    } else {
                        publishProgress()
                    }
                } catch (e1: InterruptedException) {
                    publishProgress()
                } catch (e1: ExecutionException) {
                    publishProgress()
                }
            }
        } else
            publishProgress()
        return null
    }

    private fun applyEditsToServer() {
        val applyEditsFuture = (mSelectedArcGISFeature
                .featureTable as ServiceFeatureTable).applyEditsAsync()
        applyEditsFuture.addDoneListener {
            try {
                // get results of edit
                val featureEditResultsList = applyEditsFuture.get()
                if (!featureEditResultsList[0].hasCompletedWithErrors()) {
                    publishProgress(true)
                } else {
                    publishProgress()
                }
            } catch (e: InterruptedException) {
                publishProgress()
            } catch (e: ExecutionException) {
                publishProgress()
            }
        }
    }

    override fun onProgressUpdate(vararg values: Boolean?) {
        super.onProgressUpdate(*values)
        if (values != null && values.isNotEmpty())
            mDelegate.processFinish(values[0])
        else
            mDelegate.processFinish(null)
    }

    interface AsyncResponse {
        fun processFinish(isSuccess: Boolean?)
    }

}

