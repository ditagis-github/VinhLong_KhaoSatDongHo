package vinhlong.ditagis.com.khaosatdongho.async

import android.os.AsyncTask
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.MapView
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication

class SingleTapMapViewAsync(private val mActivity: MainActivity, private val mapView: MapView, asyncResponse: AsyncResponse) : AsyncTask<android.graphics.Point, ArcGISFeature, Void>() {
    private val dApplication: DApplication = mActivity.application as DApplication
    private val dongHoKHFL: FeatureLayer
    private var delegate: AsyncResponse? = null

    interface AsyncResponse {
        fun processFinish(feature: ArcGISFeature?)
    }

    init {
        this.dongHoKHFL = this.dApplication.dongHoKHDTG?.featureLayer!!
        this.delegate = asyncResponse
    }


    override fun doInBackground(vararg params: android.graphics.Point): Void? {
        val clickPoint = params[0]
        val identifyFuture = mapView.identifyLayerAsync(dongHoKHFL, clickPoint, 5.0, false, 1)
        identifyFuture.addDoneListener {
            try {
                val layerResult = identifyFuture.get()
                val resultGeoElements = layerResult.elements
                if (resultGeoElements.size > 0) {
                    if (resultGeoElements[0] is ArcGISFeature) {
                        val arcGISFeature = resultGeoElements[0] as ArcGISFeature
                        delegate!!.processFinish(arcGISFeature)
                        publishProgress(arcGISFeature)
                    }
                } else {
                    publishProgress()
                }
            } catch (e: Exception) {
                publishProgress()
            }
        }
        return null
    }


}