package vinhlong.ditagis.com.khaosatdongho.entities

import android.app.Application
import android.net.Uri
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import vinhlong.ditagis.com.khaosatdongho.utities.DAlertDialog
import vinhlong.ditagis.com.khaosatdongho.utities.DProgressDialog
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG
import vinhlong.ditagis.com.khaosatdongho.utities.Constant

class DApplication : Application() {
    lateinit var progressDialog: DProgressDialog
    lateinit var alertDialog: DAlertDialog
    var lstFeatureLayerDTG: List<LayerInfoDTG>? = null
    val definitionFeature: String
        get() = String.format("%s = '%s' and %s = '%s'", Constant.DongHoKhachHangFields.NV_KHAO_SAT,
                user!!.userName, Constant.DongHoKhachHangFields.TINH_TRANG,
                Constant.TinhTrangDongHoKhachHang.DANG_KHAO_SAT)

    var dongHoKHDTG: FeatureLayerDTG? = null
    var vatTuDHDTG: FeatureLayerDTG? = null
    var dongHoKHSFT: ServiceFeatureTable? = null
    var vatTuKHSFT: ServiceFeatureTable? = null
    var dmVatTuKHSFT: ServiceFeatureTable? = null

    lateinit var tenMauThietLaps: MutableList<String>
    lateinit var vatTus: MutableList<VatTu>

    // user
    var user: User? = null

    var mainActivity: MainActivity? = null

    // URI
    var uri: Uri? = null
    var selectedFeature: ArcGISFeature? = null

}