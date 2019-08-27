package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter
import vinhlong.ditagis.com.khaosatdongho.libs.Action
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.util.*
import java.util.concurrent.ExecutionException

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class RefreshVatTuAsync( private val mActivity: Activity, private val vatTuTable: ServiceFeatureTable, private val dmVatTuFeatures: ArrayList<Feature>, private val vatTuApdapter: VatTuApdapter, private val action: Action, private val delegate: AsyncResponse) : AsyncTask<Long, List<VatTuApdapter.VatTu>, Void>() {

    interface AsyncResponse {
        fun processFinish(features: List<Feature>?)
    }

     override fun doInBackground(vararg params: Long?): Void? {
        val features = ArrayList<Feature>()
        val vatTus = ArrayList<VatTuApdapter.VatTu>()
        val queryParameters = QueryParameters()
        val queryClause = Constant.VatTuFields.MaKhachHang + " = '" + params[0] + "'"
        queryParameters.whereClause = queryClause
        val queryResultListenableFuture = vatTuTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        queryResultListenableFuture.addDoneListener {
            try {
                val result = queryResultListenableFuture.get()
                val iterator = result.iterator()

                while (iterator.hasNext()) {
                    val feature = iterator.next() as Feature
                    val vatTu = VatTuApdapter.VatTu()
                    val maVatTu = feature.attributes[Constant.VatTuFields.MaVatTu]
                    val soLuong = feature.attributes[Constant.VatTuFields.SoLuong]
                    val objectID = feature.attributes[Constant.LayerFields.OBJECTID]
                    vatTu.maVatTu = objectID.toString()
                    if (soLuong != null) {
                        vatTu.setSoLuongVatTu(soLuong as Double)
                    }
                    if (maVatTu != null) {
                        val loaiVatTu = getLoaiVatTu(maVatTu.toString())
                        if (loaiVatTu != null) {
                            val donViTinh = loaiVatTu.attributes[Constant.LoaiVatTuFields.DonViTinh]
                            val giaVatTu = loaiVatTu.attributes[Constant.LoaiVatTuFields.GiaVatTu]
                            val tenVatTu = loaiVatTu.attributes[Constant.LoaiVatTuFields.TenVatTu]
                            if (donViTinh != null)
                                vatTu.donViTinh = donViTinh.toString()
                            if (giaVatTu != null)
                                vatTu.setGiaNC(giaVatTu.toString())
                            if (tenVatTu != null)
                                vatTu.tenVatTu = tenVatTu.toString()
                        }
                    }
                    vatTus.add(vatTu)
                    features.add(feature)
                }
                publishProgress(vatTus)
                delegate.processFinish(features)
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

    private fun getLoaiVatTu(maVatTu: String?): Feature? {
        if (maVatTu != null) {
            for (feature in this.dmVatTuFeatures) {
                val attributes = feature.attributes
                val maVT = attributes[Constant.VatTuFields.MaVatTu]
                if (maVT != null && maVT.toString() == maVatTu) {
                    return feature
                }
            }
        }
        return null
    }

    override fun onProgressUpdate(vararg values: List<VatTuApdapter.VatTu>) {
        vatTuApdapter.clear()
        //        vatTuApdapter.setVatTus(values[0]);
        vatTuApdapter.notifyDataSetChanged()
        super.onProgressUpdate(*values)

    }

}

