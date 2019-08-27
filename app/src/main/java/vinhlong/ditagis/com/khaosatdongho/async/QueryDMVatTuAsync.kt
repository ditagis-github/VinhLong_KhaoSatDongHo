package vinhlong.ditagis.com.khaosatdongho.async

import android.app.Activity
import android.os.AsyncTask
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import java.util.*
import java.util.concurrent.ExecutionException


/**
 * Created by ThanLe on 4/16/2018.
 */

class QueryDMVatTuAsync(private val mActivity: Activity, private val mServiceFeatureTable: ServiceFeatureTable, delegate: AsyncResponse) : AsyncTask<Void, ArrayList<Feature>, Void>() {

    var delegate: AsyncResponse? = null

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
                publishProgress(features)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
        return null
    }


    override fun onProgressUpdate(vararg values: ArrayList<Feature>?) {
        super.onProgressUpdate(*values)
        values[0]?.let { delegate!!.processFinish(it) }

    }


}


