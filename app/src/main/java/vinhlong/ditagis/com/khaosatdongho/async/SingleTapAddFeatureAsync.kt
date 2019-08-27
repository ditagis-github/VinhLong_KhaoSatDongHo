package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.MapView
import com.esri.arcgisruntime.tasks.geocode.LocatorTask
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.MySnackBar
import java.util.*
import java.util.concurrent.ExecutionException

@SuppressLint("StaticFieldLeak")
class SingleTapAddFeatureAsync(private val mActivity: MainActivity, private val mapView: MapView, private val dongHoKHSFT: ServiceFeatureTable, private val delegate: SingleTapAddFeatureAsync.AsyncResponse) : AsyncTask<Point, ArcGISFeature, Void>() {
    private val locatorTask = LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer")
    private val dApplication: DApplication = mActivity.application as DApplication

    interface AsyncResponse {
        fun processFinish(feature: ArcGISFeature?)
    }

    override fun doInBackground(vararg params: Point): Void? {
        val clickPoint = params[0]
        val feature = this.dongHoKHSFT.createFeature()
        feature.geometry = clickPoint
        val listListenableFuture = locatorTask.reverseGeocodeAsync(clickPoint)
        listListenableFuture.addDoneListener {
            try {
                val geocodeResults = listListenableFuture.get()
                if (geocodeResults.size > 0) {
                    val geocodeResult = geocodeResults[0]
                    val attrs = HashMap<String, Any>()
                    for (key in geocodeResult.attributes.keys) {
                        geocodeResult.attributes[key]?.let { attrs.put(key, it) }
                    }
                    val address = geocodeResult.attributes["LongLabel"].toString()
                    feature.attributes[Constant.DongHoKhachHangFields.GHI_CHU] = address
                }
                addFeatureAsync(feature)
            } catch (e: InterruptedException) {
                notifyError()
                e.printStackTrace()
            } catch (e: ExecutionException) {
                notifyError()
                e.printStackTrace()
            }
        }
        return null
    }

    private fun notifyError() {
        publishProgress()
        MySnackBar.make(mapView, mActivity.getString(R.string.ERROR_OCCURRED), false)
    }

    private fun addFeatureAsync(featureAdd: Feature) {
        val currentTime = Calendar.getInstance()
        featureAdd.attributes[Constant.DongHoKhachHangFields.NGAY_CAP_NHAT] = currentTime
        featureAdd.attributes[Constant.DongHoKhachHangFields.NGUOI_CAP_NHAT] = this.dApplication.user?.userName

        val voidListenableFuture = this.dongHoKHSFT.addFeatureAsync(featureAdd)
        voidListenableFuture.addDoneListener {
            try {
                voidListenableFuture.get()
                val listListenableEditAsync = this.dongHoKHSFT.applyEditsAsync()
                listListenableEditAsync.addDoneListener {
                    try {
                        val featureEditResults = listListenableEditAsync.get()
                        if (featureEditResults.size > 0) {
                            val objectId = featureEditResults[0].objectId
                            val queryParameters = QueryParameters()
                            val query = "OBJECTID = $objectId"
                            queryParameters.whereClause = query
                            val queryResultListenableFuture = this.dongHoKHSFT.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
                            queryResultListenableFuture.addDoneListener {
                                try {
                                    val features = queryResultListenableFuture.get()
                                    val iterator = features.iterator()
                                    if (iterator.hasNext()) {
                                        val feature = iterator.next()
                                        publishProgress(feature as ArcGISFeature)
                                        delegate.processFinish(feature)
                                    } else {
                                        notifyError()
                                    }
                                } catch (e: InterruptedException) {
                                    notifyError()
                                    e.printStackTrace()
                                } catch (e: ExecutionException) {
                                    notifyError()
                                    e.printStackTrace()
                                }
                            }
                        } else
                            publishProgress()
                    } catch (e: InterruptedException) {
                        notifyError()
                        e.printStackTrace()
                    } catch (e: ExecutionException) {
                        notifyError()
                        e.printStackTrace()
                    }


                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }
    }

    override fun onProgressUpdate(vararg values: ArcGISFeature) {
        mActivity.dismissPin()
        super.onProgressUpdate(*values)

    }

}
