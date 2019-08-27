package vinhlong.ditagis.com.khaosatdongho.utities

import android.app.Activity
import android.view.MotionEvent
import android.widget.Toast
import com.esri.arcgisruntime.data.*
import com.esri.arcgisruntime.geometry.GeometryEngine
import com.esri.arcgisruntime.geometry.SpatialReferences
import com.esri.arcgisruntime.mapping.Viewpoint
import com.esri.arcgisruntime.mapping.view.MapView
import kotlinx.android.synthetic.main.activity_main.*
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter
import vinhlong.ditagis.com.khaosatdongho.async.EditGeometryAsync
import vinhlong.ditagis.com.khaosatdongho.async.SingleTapAddFeatureAsync
import vinhlong.ditagis.com.khaosatdongho.async.SingleTapMapViewAsync
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import java.util.concurrent.ExecutionException


/**
 * Created by ThanLe on 2/2/2018.
 */

class MapViewHandler(private val mapView: MapView, private val mActivity: MainActivity, private val popup: Popup) : Activity() {
    private var dongHoKHSFT: ServiceFeatureTable? = null
    private val mApplication: DApplication = mActivity.application as DApplication

    fun setDongHoKHSFT(dongHoKHSFT: ServiceFeatureTable) {
        this.dongHoKHSFT = dongHoKHSFT
    }

    fun addFeature() {
        val add_point = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry.extent.center
        mApplication.progressDialog.show(mActivity, mActivity.container_main, "Đang thêm đồng hồ khách hàng...")
        SingleTapAddFeatureAsync(mActivity, mapView, this.dongHoKHSFT!!, object : SingleTapAddFeatureAsync.AsyncResponse {
            override fun processFinish(feature: ArcGISFeature?) {
                if (feature != null) {
                    this@MapViewHandler.popup.showPopup(feature)
                } else
                    this@MapViewHandler.popup.dimissCallout()
                mApplication.progressDialog.dismiss()
            }

            //            this.mApplication.getMainActivity().dismissPin();

        }).execute(add_point)

    }

    fun updateGeometry(feature: ArcGISFeature) {
        val point = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry.extent.center
        mApplication.progressDialog.show(mActivity, mActivity.container_main, "Đang cập nhật tọa độ...")
        //Kiểm tra cùng ngày, cùng vị trí đã có sự cố nào chưa, nếu có thì cảnh báo, chưa thì thêm bình thường
        EditGeometryAsync(mActivity, this.dongHoKHSFT!!, feature, object : EditGeometryAsync.AsyncResponse {
            override fun processFinish(isSuccess: Boolean?) {
                if (isSuccess != null && isSuccess) {
                    mActivity.setChangingGeometry(false, null)

                    Toast.makeText(mActivity, "Đổi vị trí thành công", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(mActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
                mApplication.progressDialog.dismiss()
            }

        }).execute(point)
    }

    fun onScroll(): DoubleArray {
        val center = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).targetGeometry.extent.center
        val project = GeometryEngine.project(center, SpatialReferences.getWgs84())
        return doubleArrayOf(project.extent.center.x, project.extent.center.y)
    }

    fun onSingleTapMapView(e: MotionEvent) {
        val point = android.graphics.Point(e.x.toInt(), e.y.toInt())
        mApplication.progressDialog.show(mActivity, mActivity.container_main, "Đang xác định đồng hồ khách hàng...")
        SingleTapMapViewAsync(mActivity, mapView, object : SingleTapMapViewAsync.AsyncResponse {
            override fun processFinish(feature: ArcGISFeature?) {
                mApplication.progressDialog.dismiss()
                if (feature != null) {
                    this@MapViewHandler.popup.showPopup(feature)
                } else
                    this@MapViewHandler.popup.dimissCallout()
            }


        }).execute(point)

    }


    fun showPopup(selectedFeature: Feature?) {
        if (selectedFeature != null) {
            popup.showPopup(selectedFeature as ArcGISFeature)
        }
    }

    fun querySearch(searchStr: String, adapter: DanhSachDongHoKHAdapter) {
        adapter.clear()
        adapter.notifyDataSetChanged()
        val queryParameters = QueryParameters()
        val builder = StringBuilder()
        for (field in dongHoKHSFT!!.fields) {
            when (field.fieldType) {
                Field.Type.OID, Field.Type.INTEGER, Field.Type.SHORT -> try {
                    val search = Integer.parseInt(searchStr)
                    builder.append(String.format("%s = %s", field.name, search))
                    builder.append(" or ")
                } catch (e: Exception) {

                }

                Field.Type.FLOAT, Field.Type.DOUBLE -> try {
                    val search = java.lang.Double.parseDouble(searchStr)
                    builder.append(String.format("%s = %s", field.name, search))
                    builder.append(" or ")
                } catch (e: Exception) {

                }

                Field.Type.TEXT -> {
                    builder.append(field.name + " like N'%" + searchStr + "%'")
                    builder.append(" or ")
                }
            }
        }
        builder.append(" 1 = 2 ")
        queryParameters.whereClause = builder.toString()
        queryParameters.maxFeatures = 100
        val featureQueryResultListenableFuture = dongHoKHSFT!!.queryFeaturesAsync(queryParameters)
        featureQueryResultListenableFuture.addDoneListener {
            try {
                val result = featureQueryResultListenableFuture.get()
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val feature = iterator.next() as Feature

                    adapter.add(feature)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } catch (e: ExecutionException) {
                e.printStackTrace()
            }
        }

    }
}