package vinhlong.ditagis.com.khaosatdongho.utities

import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.LinearLayout
import android.widget.Toast
import com.esri.arcgisruntime.data.*
import com.esri.arcgisruntime.geometry.Point
import com.esri.arcgisruntime.mapping.view.Callout
import com.esri.arcgisruntime.mapping.view.MapView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup.view.*
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.UpdateActivity
import vinhlong.ditagis.com.khaosatdongho.VatTuActivity
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewInfoAdapter
import vinhlong.ditagis.com.khaosatdongho.async.EditAsync
import vinhlong.ditagis.com.khaosatdongho.async.QueryHanhChinhAsync
import vinhlong.ditagis.com.khaosatdongho.async.ViewAttachmentAsync
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG
import java.util.*

class Popup(private val mMainActivity: MainActivity, private val mMapView: MapView, private val mCallout: Callout?) : View.OnClickListener {
    private var featureDHKH: ArcGISFeature? = null
    private var dongHoKHSFT: ServiceFeatureTable? = null
    private var dongHoKHDTG: FeatureLayerDTG? = null
    private var lstFeatureType: MutableList<String>? = null
    private var linearLayout: LinearLayout? = null
    private val dApplication: DApplication
    private var quanhuyen_features: ArrayList<Feature>? = null
    private var quanhuyen_feature: Feature? = null

    init {
        this.dApplication = mMainActivity.application as DApplication
    }

    fun setDongHoKHDTG(dongHoKHDTG: FeatureLayerDTG) {
        this.dongHoKHDTG = dongHoKHDTG
        this.dongHoKHSFT = dongHoKHDTG.featureLayer.featureTable as ServiceFeatureTable
    }


    fun setmSFTHanhChinh(mSFTHanhChinh: ServiceFeatureTable) {
        QueryHanhChinhAsync(this.mMainActivity, mSFTHanhChinh, object : QueryHanhChinhAsync.AsyncResponse {
            override fun processFinish(output: ArrayList<Feature>?) {
                quanhuyen_features = output
            }
        }).execute()
    }

    private fun getHanhChinhFeature(IDHanhChinh: String) {
        quanhuyen_feature = null
        if (quanhuyen_features != null) {
            for (feature in quanhuyen_features!!) {
                val idHanhChinh = feature.attributes["IDHanhChinh"]
                if (idHanhChinh != null && idHanhChinh == IDHanhChinh) {
                    quanhuyen_feature = feature
                }
            }
        }
    }

    private fun refressPopup() {
        val hiddenFields = this.mMainActivity.resources.getStringArray(R.array.hiddenFields)
        val attributes = featureDHKH!!.attributes
        val listView = linearLayout!!.lstview_thongtinsuco
        val featureViewInfoAdapter = FeatureViewInfoAdapter(this.mMainActivity, ArrayList())
        listView.adapter = featureViewInfoAdapter
        var checkHiddenField: Boolean
        val maXa = attributes[Constant.HanhChinhFields.MAXA]
        if (maXa != null) {
            getHanhChinhFeature(maXa.toString())
        }
        for (field in this.featureDHKH!!.featureTable.fields) {
            checkHiddenField = false
            for (hiddenField in hiddenFields) {
                if (hiddenField == field.name) {
                    checkHiddenField = true
                    break
                }
            }
            val value = attributes[field.name]
            if (value != null && !checkHiddenField) {
                val item = FeatureViewInfoAdapter.Item()
                item.alias = field.alias
                item.fieldName = field.name
                if (item.fieldName!!.toUpperCase() == Constant.HanhChinhFields.MAXA) {
                    if (quanhuyen_feature != null)
                        item.value = quanhuyen_feature!!.attributes[Constant.HanhChinhFields.TENHANHCHINH].toString()
                } else if (item.fieldName!!.toUpperCase() == Constant.HanhChinhFields.MAHUYEN) {
                    if (quanhuyen_feature != null)
                        item.value = quanhuyen_feature!!.attributes[Constant.HanhChinhFields.TENHUYEN].toString()
                } else if (field.domain != null) {
                    val codedValues = (this.featureDHKH!!.featureTable.getField(item.fieldName!!).domain as CodedValueDomain).codedValues
                    val valueDomainObject = getValueDomain(codedValues, value.toString())
                    if (valueDomainObject != null) item.value = valueDomainObject.toString()
                } else
                    when (field.fieldType) {
                        Field.Type.DATE -> item.value = Constant.DATE_FORMAT.format((value as Calendar).time)
                        else -> item.value = value.toString()
                    }

                featureViewInfoAdapter.add(item)
                featureViewInfoAdapter.notifyDataSetChanged()
            }
        }
    }

    fun dimissCallout() {
        val featureLayer = dongHoKHDTG!!.featureLayer
        featureLayer.clearSelection()
        if (mCallout != null && mCallout.isShowing) {
            mCallout.dismiss()
        }
    }

    fun showPopup(featureDHKH: ArcGISFeature): LinearLayout? {

        dimissCallout()
        this.featureDHKH = featureDHKH
        val featureLayer = dongHoKHDTG!!.featureLayer
        featureLayer.selectFeature(featureDHKH)
        lstFeatureType = ArrayList()
        for (i in 0 until featureDHKH.featureTable.featureTypes.size) {
            lstFeatureType!!.add(featureDHKH.featureTable.featureTypes[i].name)
        }
        val inflater = LayoutInflater.from(this.mMainActivity.applicationContext)
        linearLayout = inflater.inflate(R.layout.popup, null) as LinearLayout
        linearLayout!!.findViewById<View>(R.id.imgbtn_close_popup)
                .setOnClickListener { view -> if (mCallout != null && mCallout.isShowing) mCallout.dismiss() }
        refressPopup()
        if (dongHoKHDTG!!.action!!.isEdit) {
            val imgBtn_ViewMoreInfo = linearLayout!!.findViewById<LinearLayout>(R.id.llayout_ViewMoreInfo)
            imgBtn_ViewMoreInfo.visibility = View.VISIBLE
            imgBtn_ViewMoreInfo.setOnClickListener { v ->
                dApplication.selectedFeature = this@Popup.featureDHKH
                val intent = Intent(mMainActivity, UpdateActivity::class.java)
                mMainActivity.startActivityForResult(intent, Constant.REQUEST.ID_UPDATE_ATTRIBUTE)

            }


            val imgBtn_vatTu = linearLayout!!.findViewById<LinearLayout>(R.id.llayout_vattu)
            imgBtn_vatTu.visibility = View.VISIBLE
            linearLayout!!.findViewById<View>(R.id.llayout_vattu).setOnClickListener { v ->
                //                this.dApplication.getEditingVatTu().showDanhSachVatTu(featureDHKH);
                dApplication.selectedFeature = this@Popup.featureDHKH
                val intent = Intent(mMainActivity, VatTuActivity::class.java)
                mMainActivity.startActivityForResult(intent, Constant.REQUEST.ID_UPDATE_VAT_TU)
            }
        }
        //        if (dongHoKHDTG.getAction().isDelete()) {
        //            LinearLayout imgBtn_delete = linearLayout.findViewById(R.id.llayout_delete);
        //            imgBtn_delete.setVisibility(View.VISIBLE);
        //            imgBtn_delete.setOnClickListener(new View.OnClickListener() {
        //                @Override
        //                public void onClick(View v) {
        //                    featureDHKH.getFeatureTable().getFeatureLayer().clearSelection();
        //                    deleteFeature();
        //                }
        //            });
        //        }
        if (dongHoKHDTG!!.action!!.isEdit && featureDHKH.canEditAttachments()) {
            val imgBtn_takePics = linearLayout!!.findViewById<LinearLayout>(R.id.llayout_takePics)
            imgBtn_takePics.visibility = View.VISIBLE
            imgBtn_takePics.setOnClickListener { v -> updateAttachment(featureDHKH) }
        }
        if (this.featureDHKH!!.canEditAttachments()) {
            val imgBtn_view_attachment = linearLayout!!.findViewById<LinearLayout>(R.id.llayout_view_attachment)
            imgBtn_view_attachment.visibility = View.VISIBLE
            imgBtn_view_attachment.setOnClickListener { v -> viewAttachment(featureDHKH) }
        }
        if (this.featureDHKH!!.canUpdateGeometry()) {
            val edit_location = linearLayout!!.findViewById<LinearLayout>(R.id.llayout_edit_location)
            edit_location.visibility = View.VISIBLE
            edit_location.setOnClickListener { v -> editLocation(featureDHKH) }
        }
        if (dongHoKHDTG!!.action!!.isEdit && this.featureDHKH!!.attributes[Constant.DongHoKhachHangFields.TINH_TRANG] == Constant.TinhTrangDongHoKhachHang.DANG_KHAO_SAT) {
            val completeLayout = linearLayout!!.findViewById<LinearLayout>(R.id.llayout_complete)
            completeLayout.visibility = View.VISIBLE
            completeLayout.setOnClickListener { hoanTatKhaoSat() }
        }
        linearLayout!!.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val envelope = featureDHKH.geometry.extent
        var scale = mMapView.mapScale
        val minScale = dongHoKHDTG!!.featureLayer.minScale
        if (scale > minScale) scale = minScale

        showCallout(envelope.center, linearLayout, scale)
        return linearLayout
    }

    private fun showCallout(point: Point?, view: View?, scale: Double) {
        mMainActivity.runOnUiThread {
            val viewpointCenterAsync = mMapView.setViewpointCenterAsync(point, scale)
            viewpointCenterAsync.addDoneListener {
                val result = viewpointCenterAsync.get()
                if (result) {
                    mCallout!!.location = point
                    mCallout.content = view
                    mCallout.refresh()
                    mCallout.show()
                } else {
                    val snackBar = Snackbar.make(mMainActivity.container_main, "Có lỗi xảy ra", 5000)
                    snackBar.setAction("Thử lại") {
                        showCallout(point, view, scale)
                    }
                    snackBar.show()
                }
            }

        }
    }

    private fun hoanTatKhaoSat() {
        val builder = AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
        builder.setTitle("Xác nhận")
        builder.setMessage(String.format("Bạn có chắc muốn hoàn tất đồng hồ %s", this@Popup.featureDHKH!!.attributes[Constant.DongHoKhachHangFields.ID]))
        builder.setNegativeButton("Hoàn tất") { dialog, which ->
            this@Popup.featureDHKH!!.attributes[Constant.DongHoKhachHangFields.TINH_TRANG] = Constant.TinhTrangDongHoKhachHang.DANG_THIET_KE
            EditAsync(mMainActivity.container_main, mMainActivity, dongHoKHSFT!!, featureDHKH!!, object : EditAsync.AsyncResponse {
                override fun processFinish(isSuccess: Boolean?) {
                    if (isSuccess!!) {
                        Toast.makeText(mMainActivity, "Đã hoàn tất khảo sát", Toast.LENGTH_SHORT).show()
                        if (this@Popup.mCallout!!.isShowing)
                            this@Popup.mCallout.dismiss()
                    } else
                        Toast.makeText(mMainActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show()
                }
            }).execute()
            dialog.dismiss()
        }.setPositiveButton("Hủy") { dialog, which -> dialog.dismiss() }.setCancelable(false)
        val dialog = builder.create()
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.show()
    }

    private fun editLocation(feature: ArcGISFeature) {
        mMainActivity.setChangingGeometry(true, feature)
        if (mCallout!!.isShowing)
            mCallout.dismiss()
    }

    fun updateAttachment(featureDHKH: ArcGISFeature) {
        val cameraIntent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(mMainActivity.packageManager) != null) {
            val photo = ImageFile.getFile(mMainActivity)
            val uri = Uri.fromFile(photo)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            dApplication.selectedFeature = featureDHKH
            dApplication.uri = uri
            mMainActivity.startActivityForResult(cameraIntent, Constant.REQUEST.ID_UPDATE_ATTACHMENT)
        }
    }

    private fun viewAttachment(featureDHKH: ArcGISFeature) {
        val viewAttachmentAsync = ViewAttachmentAsync(mMainActivity,mMainActivity.container_main, featureDHKH)
        viewAttachmentAsync.execute()
    }

    private fun getValueDomain(codedValues: List<CodedValue>, code: String): Any? {
        var value: Any? = null
        for (codedValue in codedValues) {
            if (codedValue.code.toString() == code) {
                value = codedValue.name
                break
            }

        }
        return value
    }

    private fun getValueFeatureType(featureTypes: List<FeatureType>, code: String): Any? {
        var value: Any? = null
        for (featureType in featureTypes) {
            if (featureType.id.toString() == code) {
                value = featureType.name
                break
            }
        }
        return value
    }


    override fun onClick(v: View) {
        when (v.id) {
            R.id.txtAdd -> {
            }
        }//            @Override
        //
    }
}
