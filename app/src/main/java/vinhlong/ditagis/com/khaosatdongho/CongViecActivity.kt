package vinhlong.ditagis.com.khaosatdongho

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.ServiceFeatureTable
import kotlinx.android.synthetic.main.activity_congviec.*
import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter
import vinhlong.ditagis.com.khaosatdongho.adapter.ThongKeAdapter
import vinhlong.ditagis.com.khaosatdongho.async.QueryDongHoKhachHangAsync
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.util.*

class CongViecActivity : AppCompatActivity() {
    private val txtTongItem: TextView? = null
    private var serviceFeatureTable: ServiceFeatureTable? = null
    private val thongKeAdapter: ThongKeAdapter? = null
    private lateinit var mApplication: DApplication
    private var mBottomLayout: LinearLayout? = null
    private var mBottomSheetDialog: BottomSheetDialog? = null
    private var mSelectedFeature: Feature? = null
    private var mListView: ListView? = null
    //    private DanhSachDongHoKHAdapter.Item mSelectedDongHo;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_congviec)
        init()
        mListView = vattu_listview


        serviceFeatureTable = mApplication!!.dongHoKHDTG!!.featureLayer.featureTable as ServiceFeatureTable

        getQueryDiemDanhGiaAsync(mApplication!!.definitionFeature)
    }

    private fun init() {
        mApplication = application as DApplication
        initBottomSheet()

    }

    private fun initBottomSheet() {
        mBottomSheetDialog = BottomSheetDialog(this@CongViecActivity)
        mBottomLayout = layoutInflater.inflate(R.layout.layout_handle_item_cong_viec, null) as LinearLayout
        mBottomLayout!!.findViewById<View>(R.id.llayout__handle_item_cong_viec_tim_duong).setOnClickListener {
            if (mBottomSheetDialog!!.isShowing)
                mBottomSheetDialog!!.dismiss()
            if (mSelectedFeature!!.attributes[Constant.DongHoKhachHangFields.DIA_CHI_LAP_DAT] == null || mSelectedFeature!!.attributes[Constant.DongHoKhachHangFields.DIA_CHI_LAP_DAT].toString() == "null") {
                Toast.makeText(this@CongViecActivity, "Chưa có thông tin địa chỉ!", Toast.LENGTH_SHORT).show()
            } else {
                val uri = String.format("google.navigation:q=%s", Uri.encode(mSelectedFeature!!.attributes[Constant.DongHoKhachHangFields.DIA_CHI_LAP_DAT].toString()))
                val gmmIntentUri = Uri.parse(uri)
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                startActivity(mapIntent)
            }
        }
        mBottomLayout!!.findViewById<View>(R.id.llayout__handle_item_cong_viec_ban_do).setOnClickListener {
            if (mBottomSheetDialog!!.isShowing)
                mBottomSheetDialog!!.dismiss()
            val returnIntent = Intent()
            mApplication!!.selectedFeature = mSelectedFeature as ArcGISFeature?
            setResult(Activity.RESULT_OK, returnIntent)


            finish()
        }
        mBottomSheetDialog!!.setContentView(mBottomLayout)
    }

    private fun setBottomLayout(feature: Feature) {
        val layout = mBottomLayout!!.findViewById<LinearLayout>(R.id.llayout__handle_item_cong_viec__info)

        layout.removeAllViews()
        mSelectedFeature = feature
        for (field in feature.featureTable.fields) {
            if (Constant.DongHoKhachHangFields.OutFields.contains(field.name)) {
                val value = feature.attributes[field.name]
                var valueString = ""
                if (value != null)
                    valueString = value.toString()
                layout.addView(getLayoutInfo(field.alias, valueString))
            }
        }


        //        layout.addView(getLayoutInfo("Mã khách hàng", dongHo.getCmnd()));
        //        layout.addView(getLayoutInfo("Tên khách hàng", dongHo.getTenKhachHang()));
        //        layout.addView(getLayoutInfo("Số điện thoại", dongHo.getSoDienThoai()));
        //        layout.addView(getLayoutInfo("Địa chỉ", dongHo.getDiaChi()));
        //        layout.addView(getLayoutInfo("Ngày giao", dongHo.getThoiGian()));

    }

    private fun getLayoutInfo(alias: String, value: String?): View {
        val view = this@CongViecActivity.layoutInflater.inflate(R.layout.item_viewinfo, null) as LinearLayout
        (view.findViewById<View>(R.id.txt_viewinfo_alias) as TextView).text = alias

        var tempValue = value
        if (value == null || value == "null")
            tempValue = "Chưa xác định"
        (view.findViewById<View>(R.id.txt_viewinfo_value) as TextView).text = tempValue

        return view
    }

    private fun getQueryDiemDanhGiaAsync(whereClause: String) {
        mApplication.progressDialog.show(this@CongViecActivity, container_cong_viec, "Đang lấy danh sách công việc...")
        if (serviceFeatureTable != null)
            QueryDongHoKhachHangAsync(this, serviceFeatureTable!!, txtTongItem, object : QueryDongHoKhachHangAsync.AsyncResponse {
                override fun processFinish(features: List<Feature>?) {
                    if (features != null && features.isNotEmpty()) {

                        val items = ArrayList<Feature>()
                        for (feature in features) {
                            items.add(feature)
                        }

                        val adapter = DanhSachDongHoKHAdapter(this@CongViecActivity, items)
                        mListView!!.adapter = adapter
                        mListView!!.setOnItemClickListener { parent, view, position, id ->

                            setBottomLayout(adapter.getItems()!![position])
                            mBottomSheetDialog!!.show()
                        }
                    }
                    mApplication.progressDialog.dismiss()
                }


            }).execute(whereClause)


    }


}
