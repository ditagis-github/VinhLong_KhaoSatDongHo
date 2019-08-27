package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.widget.LinearLayout
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.layers.FeatureLayer
import com.esri.arcgisruntime.mapping.view.MapView
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication

class SingleTapMapViewAsync(private val mActivity: MainActivity, private val mapView: MapView, asyncResponse: AsyncResponse) : AsyncTask<android.graphics.Point, ArcGISFeature, Void>() {
    private var mDialog: BottomSheetDialog? = null
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


    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mActivity)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang tìm kiếm thông tin..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()
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

    override fun onProgressUpdate(vararg values: ArcGISFeature) {
        if (mDialog != null && mDialog!!.isShowing) {
            mDialog!!.dismiss()
        }
        super.onProgressUpdate(*values)
    }


}