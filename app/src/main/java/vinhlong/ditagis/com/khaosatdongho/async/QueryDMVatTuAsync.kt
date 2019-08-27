package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.widget.LinearLayout
import android.widget.TextView

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vinhlong.ditagis.com.khaosatdongho.R


/**
 * Created by ThanLe on 4/16/2018.
 */

class QueryDMVatTuAsync(private val mActivity: Activity, private val mServiceFeatureTable: ServiceFeatureTable, delegate: AsyncResponse) : AsyncTask<Void, ArrayList<Feature>, Void>() {

    var delegate: AsyncResponse? = null

    private var mDialog: BottomSheetDialog? = null
    lateinit var features: ArrayList<Feature>

    interface AsyncResponse {
        fun processFinish(dmVatTus: ArrayList<Feature>?)
    }

    init {
        this.delegate = delegate
    }

    override fun doInBackground(vararg voids: Void): Void? {
        val queryParameters = QueryParameters()
        val query = "1=1"
        queryParameters.whereClause = query
        val featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        featureQueryResultListenableFuture.addDoneListener {
            try {
                val result = featureQueryResultListenableFuture.get()
                val iterator = result.iterator()
                features = ArrayList()
                while (iterator.hasNext()) {
                    val feature = iterator.next()
                    features.add(feature)
                }
                mDialog!!.dismiss()
                publishProgress(features)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mActivity)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang tải dữ liệu..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()
    }


    override fun onProgressUpdate(vararg values: ArrayList<Feature>?) {
        super.onProgressUpdate(*values)
        values[0]?.let { delegate!!.processFinish(it) }

    }

    override fun onPostExecute(result: Void) {
        super.onPostExecute(result)

    }

}


