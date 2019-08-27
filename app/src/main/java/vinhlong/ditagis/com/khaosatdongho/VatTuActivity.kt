package vinhlong.ditagis.com.khaosatdongho

import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.InputFilter
import android.text.InputType
import android.view.View
import android.view.Window
import android.widget.*
import com.esri.arcgisruntime.data.*
import org.json.JSONArray
import org.json.JSONException
import vinhlong.ditagis.com.khaosatdongho.adapter.ChiTietVatTuAdapter
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter
import vinhlong.ditagis.com.khaosatdongho.async.NotifyVatTuDongHoAdapterChangeAsync
import vinhlong.ditagis.com.khaosatdongho.async.QueryDMVatTuAsync
import vinhlong.ditagis.com.khaosatdongho.async.RefreshVatTuAsync
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.*
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import vinhlong.ditagis.com.khaosatdongho.utities.MySnackBar
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*
import java.util.concurrent.ExecutionException

class VatTuActivity : AppCompatActivity() {
    private var vatTuApdapter: VatTuApdapter? = null
    private var table_feature: List<Feature>? = null
    private var feature: ArcGISFeature? = null
    private var dmVatTuFeatures: ArrayList<Feature>? = null
    private var mAdapter: ArrayAdapter<*>? = null
    internal lateinit var tenMaus: MutableList<String>
    internal var danhSachVatTu: ArrayList<QueryDanhSachVatTuAsycn.VatTu>? = null
    private var mApplication: DApplication? = null
    var formatter: NumberFormat = DecimalFormat("###,###,###")
    private var mListView: ListView? = null
    private var mSpin_thietlapmau: Spinner? = null
    private var mTxtAdd: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vat_tu)
        mApplication = application as DApplication
        mListView = findViewById(R.id.vattu_listview)
        mSpin_thietlapmau = findViewById(R.id.spin_thietlapmau)
        mTxtAdd = findViewById(R.id.txtAdd)


        vatTuApdapter = VatTuApdapter(this@VatTuActivity, ArrayList())
        mListView!!.adapter = vatTuApdapter
        QueryDMVatTuAsync(this@VatTuActivity, mApplication!!.dmVatTuKHSFT!!, object : QueryDMVatTuAsync.AsyncResponse {
            override fun processFinish(dmVatTus: ArrayList<Feature>?) {
                this@VatTuActivity.dmVatTuFeatures = dmVatTus
            }
        }).execute()
        this.getDanhSachVatTu()
        this.getMauThietLaps()


    }

    private fun getDanhSachVatTu() {
        QueryDanhSachVatTuAsycn(this@VatTuActivity, object : QueryDanhSachVatTuAsycn.AsyncResponse {
            override fun processFinish(vatTus: ArrayList<QueryDanhSachVatTuAsycn.VatTu>?) {
                if (danhSachVatTu != null) {
                    this@VatTuActivity.danhSachVatTu = danhSachVatTu
                }
            }
        }
        ).execute()
    }

    private fun getMauThietLaps() {
        tenMaus = ArrayList()
        QueryTenMauAsycn(this@VatTuActivity, object : QueryTenMauAsycn.AsyncResponse {
            override fun processFinish(tenMaus: String?) {

                val jsonArray: JSONArray
                try {
                    jsonArray = JSONArray(tenMaus)
                    this@VatTuActivity.tenMaus.add(Constant.TENMAU.SELECT)
                    for (i in 0 until jsonArray.length()) {
                        this@VatTuActivity.tenMaus.add(jsonArray.get(i).toString())
                    }
                    showDanhSachVatTu(mApplication!!.selectedFeature)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }


            }
        }
        ).execute()
    }

    private fun getVatTu(v: View?, maKhachHang: Long) {
        QueryVatTuDongHoAsycn(this@VatTuActivity, object : QueryVatTuDongHoAsycn.AsyncResponse {
            override fun processFinish(vatTus: ArrayList<VatTuApdapter.VatTu>?) {
                if (vatTus != null) {
                    for (vatTu in vatTus) {
                        for (vatTu1 in danhSachVatTu!!) {
                            if (vatTu.maVatTu == vatTu1.maVatTu) {
                                vatTu.tenVatTu = vatTu1.tenVatTu
                                vatTu.donViTinh = vatTu1.donViTinh
                                break
                            }
                        }
                    }
                    vatTuApdapter!!.vatTus = vatTus

                } else {
                    Snackbar.make(v!!, "Chưa có vật tư!", 1500).show()
                    vatTuApdapter!!.clear()
                }
                vatTuApdapter!!.notifyDataSetChanged()
            }
        }
        ).execute(maKhachHang)
    }

    private fun getVatTuTheoMau(tenThietLapMau: String, idDongHo: Long?) {
        val vatTusAdapter = ArrayList<VatTuApdapter.VatTu>()
        QueryVatTuTheoMauAsycn(this@VatTuActivity, object : QueryVatTuTheoMauAsycn.AsyncResponse {
            override fun processFinish(vatTus: ArrayList<QueryVatTuTheoMauAsycn.VatTu>?) {
                if (vatTus != null) {
                    for (i in vatTus!!.indices) {
                        val vatTuAdapter = VatTuApdapter.VatTu(i + 1)
                        val vatTu = vatTus!!.get(i)
                        val maVatTu = vatTu.maVatTu
                        if (maVatTu != null) {
                            val loaiVatTu = getLoaiVatTu(maVatTu)
                            vatTuAdapter.maVatTu = maVatTu
                            vatTuAdapter.tenVatTu = loaiVatTu!!.tenVatTu
                            vatTuAdapter.setSoLuongVatTu(vatTu.soLuong)
                            vatTuAdapter.setGiaNC(loaiVatTu.getnC()!!)
                            vatTuAdapter.setGiaVT(loaiVatTu.getvT()!!)
                            vatTuAdapter.donViTinh = loaiVatTu.donViTinh
                            vatTuAdapter.setiDKhachHang(idDongHo!!)
                            vatTusAdapter.add(vatTuAdapter)
                        }
                    }
                    vatTuApdapter!!.vatTus = vatTusAdapter
                    vatTuApdapter!!.notifyDataSetChanged()
                }
            }
        }
        ).execute(tenThietLapMau)
    }

    private fun getLoaiVatTu(maVatTu: String?): QueryDanhSachVatTuAsycn.VatTu? {
        if (maVatTu != null) {
            for (vatTu in this.danhSachVatTu!!) {
                if (vatTu.maVatTu == maVatTu) {
                    return vatTu
                }
            }
        }
        return null
    }


    fun showDanhSachVatTu(featureDHKH: ArcGISFeature?) {
        this.feature = featureDHKH
        val attributes = featureDHKH!!.attributes
        val idDongHo = attributes[Constant.DongHoKhachHangFields.ID]
        //        idDongHo = 12345;
        if (idDongHo != null) {


            mAdapter = ArrayAdapter(this@VatTuActivity, android.R.layout.simple_spinner_dropdown_item, tenMaus)
            mSpin_thietlapmau!!.adapter = mAdapter

            mSpin_thietlapmau!!.setSelection(0)
            mSpin_thietlapmau!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parentView: AdapterView<*>, selectedItemView: View, position: Int, id: Long) {
                    val itemAtPosition = mSpin_thietlapmau!!.getItemAtPosition(position)
                    if (itemAtPosition != null) {
                        val tenThietLapMau = itemAtPosition.toString()
                        if (tenThietLapMau != Constant.TENMAU.SELECT) {
                            getVatTuTheoMau(tenThietLapMau, (idDongHo as Int).toLong())
                            setEnable(mTxtAdd, true)
                        } else {
                            getVatTu(mTxtAdd, (idDongHo as Int).toLong())
                            setEnable(mTxtAdd, false)
                        }
                    }
                }

                override fun onNothingSelected(parentView: AdapterView<*>) {
                    // your code here
                }

            }


            mTxtAdd!!.setOnClickListener { v -> addTableVatTu(v, mSpin_thietlapmau!!.selectedItem as String) }

            //            getRefreshTableVatTuAsync();


        } else {
            MySnackBar.make(mTxtAdd!!, this@VatTuActivity.getString(R.string.DATA_NOT_FOUND), true)
            return
        }
    }

    private fun setEnable(v: TextView?, enable: Boolean) {
        if (enable) {
            v!!.isClickable = true
            v.setTextColor(resources.getColor(R.color.colorPrimary))
        } else {
            v!!.isClickable = false
            v.setTextColor(Color.LTGRAY)
        }
    }

    private fun getLoaiVatTu_Ma(maVatTu: String?): Feature? {
        if (maVatTu != null) {
            for (feature in this.dmVatTuFeatures!!) {
                val attributes = feature.attributes
                val maVT = attributes[Constant.LoaiVatTuFields.MaVatTu]
                if (maVT != null && maVT.toString() == maVatTu) {
                    return feature
                }
            }
        }
        return null
    }

    private fun getLoaiVatTu_Ten(tenVatTu: String?): Feature? {
        if (tenVatTu != null) {
            for (feature in this.dmVatTuFeatures!!) {
                val attributes = feature.attributes
                val tenVT = attributes[Constant.LoaiVatTuFields.TenVatTu]
                if (tenVT != null && tenVT.toString() == tenVatTu) {
                    return feature
                }
            }
        }
        return null
    }

    private fun showInfosSelectedItem(selectedFeature: Feature) {
        val attributes = selectedFeature.attributes
        val layout_chitiet_vattudongho = this@VatTuActivity.layoutInflater.inflate(R.layout.layout_title_listview, null)
        val listview_chitiet_maudanhgia = layout_chitiet_vattudongho.findViewById<ListView>(R.id.vattu_listview)
        if (attributes[Constant.VatTuFields.DBDongHo] != null) {
            (layout_chitiet_vattudongho.findViewById<View>(R.id.txtTitle) as TextView).text = attributes[Constant.VatTuFields.DBDongHo].toString()
        } else {
            (layout_chitiet_vattudongho.findViewById<View>(R.id.txtTitle) as TextView).text = this@VatTuActivity.getString(R.string.title_chitietvattu)
        }
        val items = ArrayList<ChiTietVatTuAdapter.Item>()
        val fields = mApplication!!.vatTuKHSFT!!.fields
        val updateFields = mApplication!!.vatTuDHDTG!!.updateFields
        val unedit_Fields = this@VatTuActivity.resources.getStringArray(R.array.unedit_VT_Fields)
        for (field in fields) {
            val item = ChiTietVatTuAdapter.Item()
            item.alias = field.alias
            item.fieldName = field.name
            item.fieldType = field.fieldType
            if (field.name == Constant.VatTuFields.MaVatTu) {
                item.alias = Constant.VatTuAlias.LoaiVatTu
            }
            val value = attributes[field.name]
            if (value != null) {
                if (field.name == Constant.VatTuFields.MaVatTu) {
                    val loaiVatTu = getLoaiVatTu_Ma(value.toString())
                    if (loaiVatTu != null) {
                        item.value = loaiVatTu.attributes[Constant.LoaiVatTuFields.TenVatTu].toString()
                    }
                } else if (field.domain != null) {
                    val codedValues = (field.domain as CodedValueDomain).codedValues
                    val valueDomain = getValueDomain(codedValues, value.toString())!!.toString()
                    if (valueDomain != null) item.value = valueDomain
                } else
                    when (field.fieldType) {
                        Field.Type.DATE -> item.value = Constant.DATE_FORMAT.format((value as Calendar).time)
                        else -> if (attributes[field.name] != null)
                            item.value = attributes[field.name].toString()
                    }
            }
            if (this.mApplication!!.vatTuDHDTG!!.action!!.isEdit) {
                if (updateFields!!.size > 0) {
                    if (updateFields[0] == "*" || updateFields[0] == "") {
                        item.isEdit = true
                    } else {
                        for (updateField in updateFields) {
                            if (item.fieldName == updateField) {
                                item.isEdit = true
                                break
                            }
                        }
                    }
                }
                for (unedit_Field in unedit_Fields) {
                    if (unedit_Field.toUpperCase() == item.fieldName!!.toUpperCase()) {
                        item.isEdit = false
                        break
                    }
                }
            }
            items.add(item)
        }
        val chiTietVatTuAdapter = ChiTietVatTuAdapter(this@VatTuActivity, items)
        if (items != null) listview_chitiet_maudanhgia.adapter = chiTietVatTuAdapter
        val builder = AlertDialog.Builder(this@VatTuActivity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen)
        builder.setView(layout_chitiet_vattudongho)
        if (this.mApplication!!.vatTuDHDTG!!.action!!.isEdit) {
            builder.setPositiveButton(this@VatTuActivity.getString(R.string.btn_Accept), null)
        }
        if (this.mApplication!!.vatTuDHDTG!!.action!!.isDelete) {
            builder.setNegativeButton(this@VatTuActivity.getString(R.string.btn_Delete), null)
        }
        builder.setNeutralButton(this@VatTuActivity.getString(R.string.btn_Esc), null)
        listview_chitiet_maudanhgia.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
            if (mApplication!!.vatTuDHDTG!!.action!!.isEdit) {
                editValueAttribute(parent, view, position, id)
            }
        }
        val dialog = builder.create()
        builder.setPositiveButton(android.R.string.ok, null)
        dialog.show()
        // Chỉnh sửa
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { v ->
            for (item in items) {
                val domain = mApplication!!.vatTuKHSFT!!.getField(item.fieldName!!).domain
                var codeDomain: Any? = null
                if (item.fieldName == Constant.VatTuFields.NgayCapNhat) {
                    val currentTime = Calendar.getInstance()
                    item.value = Constant.DATE_FORMAT.format(currentTime.time)
                } else if (domain != null) {
                    val codedValues = (domain as CodedValueDomain).codedValues
                    codeDomain = getCodeDomain(codedValues, item.value)
                }
                if (item.fieldName == Constant.VatTuFields.MaVatTu) {
                    val loaiVatTu_ten = getLoaiVatTu_Ten(item.value)
                    if (loaiVatTu_ten != null)
                        selectedFeature.attributes[item.fieldName] = loaiVatTu_ten.attributes[Constant.LoaiVatTuFields.MaVatTu].toString()
                } else {
                    when (item.fieldType) {
                        Field.Type.DATE -> if (item.calendar != null)
                            selectedFeature.attributes[item.fieldName] = item.calendar
                        Field.Type.DOUBLE -> if (item.value != null)
                            selectedFeature.attributes[item.fieldName] = java.lang.Double.parseDouble(item.value)
                        Field.Type.SHORT -> if (codeDomain != null) {
                            selectedFeature.attributes[item.fieldName] = java.lang.Short.parseShort(codeDomain.toString())
                        } else if (item.value != null)
                            selectedFeature.attributes[item.fieldName] = java.lang.Short.parseShort(item.value)
                        Field.Type.TEXT -> if (codeDomain != null) {
                            selectedFeature.attributes[item.fieldName] = codeDomain.toString()
                        } else if (item.value != null)
                            selectedFeature.attributes[item.fieldName] = item.value
                    }
                }
            }
            chiTietVatTuAdapter.notifyDataSetChanged()
            val currentTime = Calendar.getInstance()
            selectedFeature.attributes[Constant.VatTuFields.NgayCapNhat] = currentTime
            updateFeature(selectedFeature)
        }
        // Xóa
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener { v ->
            deleteFeature(selectedFeature)
            dialog.dismiss()
        }
        // Thoát
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener { v -> dialog.dismiss() }

        capNhatDanhBo(selectedFeature, dialog)
    }

    private fun getRefreshTableVatTuAsync() {
        val attributes = this.feature!!.attributes
        val idDongHo = (attributes[Constant.DongHoKhachHangFields.ID] as Int).toLong()
        RefreshVatTuAsync(this@VatTuActivity, mApplication!!.vatTuKHSFT!!, this.dmVatTuFeatures!!, vatTuApdapter!!, this.mApplication!!.vatTuDHDTG!!.action!!,
                object : RefreshVatTuAsync.AsyncResponse {
                    override fun processFinish(features: List<Feature>?) {

                        table_feature = features
                        var sum = 0.0
                        if (features != null)
                            for (feature in features) {
                                val maVatTu = feature.getAttributes().get(Constant.VatTuFields.MaVatTu)
                                val soLuong = feature.getAttributes().get(Constant.VatTuFields.SoLuong)
                                if (maVatTu != null && soLuong != null) {
                                    val loaiVatTu = getLoaiVatTu12(maVatTu!!.toString())
                                    if (loaiVatTu != null) {
                                        val giaVatTu = loaiVatTu.attributes[Constant.LoaiVatTuFields.GiaVatTu]
                                        if (giaVatTu != null) {
                                            try {
                                                val giaVT = java.lang.Double.parseDouble(giaVatTu.toString())
                                                val soLuongVT = Integer.parseInt(soLuong!!.toString())
                                                sum += soLuongVT * giaVT
                                            } catch (e: Exception) {
                                            }

                                        }
                                    }
                                }
                            }
                    }
                }
        ).execute(idDongHo)
    }

    private fun getLoaiVatTu12(maVatTu: String?): Feature? {
        if (maVatTu != null) {
            for (feature in this.dmVatTuFeatures!!) {
                val attributes = feature.attributes
                val maVT = attributes[Constant.VatTuFields.MaVatTu]
                if (maVT != null && maVT.toString() == maVatTu) {
                    return feature
                }
            }
        }
        return null
    }

    private fun capNhatDanhBo(selectedFeature: Feature, alertDialog: AlertDialog) {
        val attributes = feature!!.attributes
        val dBDongHoNuoc = attributes[Constant.DongHoKhachHangFields.ID]
        val dbDongHoVatTu = selectedFeature.attributes[Constant.VatTuFields.DBDongHo]
        if (dBDongHoNuoc != null && dbDongHoVatTu != null && dbDongHoVatTu != dBDongHoNuoc) {
            val builder = AlertDialog.Builder(this@VatTuActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
            builder.setTitle("Danh bộ của đồng hồ nước khác với danh bộ vật tư")
            builder.setMessage(R.string.question_capnhatdanhbo)
            builder.setPositiveButton("Có") { dialog, which ->
                val currentTime = Calendar.getInstance()
                selectedFeature.attributes[Constant.VatTuFields.NgayCapNhat] = currentTime
                selectedFeature.attributes[Constant.VatTuFields.DBDongHo] = dBDongHoNuoc.toString()
                updateFeature(selectedFeature)
                dialog.dismiss()
                alertDialog.dismiss()
                showInfosSelectedItem(selectedFeature)
            }.setNegativeButton("Không") { dialog, which -> dialog.dismiss() }.setCancelable(false)
            val dialog = builder.create()
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            dialog.show()
        }
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

    private fun addTableVatTu(v: View, item: String) {
        if (!mApplication!!.vatTuDHDTG!!.action!!.isCreate) {
            Snackbar.make(v, "Không thể Cập nhật vật tư!", 2000).show()
        } else if (item == Constant.TENMAU.SELECT) {
            Snackbar.make(v, "Vui lòng chọn mẫu!", 2000).show()
        } else {
            DeleteVatTuKHAsycn(this@VatTuActivity, object : DeleteVatTuKHAsycn.AsyncResponse {
                override fun processFinish(isSuccess: Boolean?) {

                    if (isSuccess!!) {
                        AddVatTuKHAsycn(this@VatTuActivity, object : AddVatTuKHAsycn.AsyncResponse {
                            override fun processFinish(isSuccess: Boolean?) {
                                if (isSuccess!!) {
                                    Toast.makeText(this@VatTuActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                                } else
                                    Snackbar.make(v, "Cật nhật thất bại!", 2000).show()
                            }
                        }).execute(vatTuApdapter!!.vatTus)
                    } else
                        Snackbar.make(v, "Cật nhật thất bại!", 2000).show()
                }
            }
            ).execute(vatTuApdapter!!.vatTus!![0].getiDKhachHang())

        }

    }

    private fun getCodeDomain(codedValues: List<CodedValue>, value: String?): Any? {
        var code: Any? = null
        for (codedValue in codedValues) {
            if (codedValue.name == value) {
                code = codedValue.code
                break
            }

        }
        return code
    }


    private fun addFeature(feature: Feature) {
        val mapViewResult = mApplication!!.vatTuKHSFT!!.addFeatureAsync(feature)
        mapViewResult.addDoneListener {
            val listListenableEditAsync = mApplication!!.vatTuKHSFT!!.applyEditsAsync()
            listListenableEditAsync.addDoneListener {
                try {
                    val featureEditResults = listListenableEditAsync.get()
                    if (featureEditResults.size > 0) {
                        Toast.makeText(this@VatTuActivity.applicationContext, this@VatTuActivity.getString(R.string.DATA_SUCCESSFULLY_INSERTED), Toast.LENGTH_SHORT).show()
                        getRefreshTableVatTuAsync()
                    } else {
                        Toast.makeText(this@VatTuActivity.applicationContext, this@VatTuActivity.getString(R.string.FAILED_TO_INSERT_DATA), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }


            }
        }
    }


    private fun deleteFeature(feature: Feature) {
        val mapViewResult = mApplication!!.vatTuKHSFT!!.deleteFeatureAsync(feature)
        mapViewResult.addDoneListener {
            val listListenableEditAsync = mApplication!!.vatTuKHSFT!!.applyEditsAsync()
            listListenableEditAsync.addDoneListener {
                try {
                    val featureEditResults = listListenableEditAsync.get()
                    if (featureEditResults.size > 0) {
                        Toast.makeText(this@VatTuActivity.applicationContext, this@VatTuActivity.getString(R.string.DATA_SUCCESSFULLY_DELETED), Toast.LENGTH_SHORT).show()
                        getRefreshTableVatTuAsync()
                    } else {
                        Toast.makeText(this@VatTuActivity.applicationContext, this@VatTuActivity.getString(R.string.FAILED_TO_DELETE_DATA), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }


            }
        }
    }

    private fun updateFeature(feature: Feature) {
        val mapViewResult = mApplication!!.vatTuKHSFT!!.updateFeatureAsync(feature)
        mapViewResult.addDoneListener {
            val listListenableEditAsync = mApplication!!.vatTuKHSFT!!.applyEditsAsync()
            listListenableEditAsync.addDoneListener {
                try {
                    val featureEditResults = listListenableEditAsync.get()
                    if (featureEditResults.size > 0) {
                        Toast.makeText(this@VatTuActivity.applicationContext, this@VatTuActivity.getString(R.string.DATA_SUCCESSFULLY_UPDATED), Toast.LENGTH_SHORT).show()
                        getRefreshTableVatTuAsync()
                    } else {
                        Toast.makeText(this@VatTuActivity.applicationContext, this@VatTuActivity.getString(R.string.FAILED_TO_UPDATE_DATA), Toast.LENGTH_SHORT).show()
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                } catch (e: ExecutionException) {
                    e.printStackTrace()
                }


            }
        }
    }

    private fun editValueAttribute(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        val item = parent.getItemAtPosition(position) as ChiTietVatTuAdapter.Item
        if (item.isEdit) {
            val calendar = arrayOfNulls<Calendar>(1)
            val builder = AlertDialog.Builder(this@VatTuActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
            builder.setTitle("Cập nhật thuộc tính")
            builder.setMessage(item.alias)
            builder.setCancelable(false).setNegativeButton("Hủy") { dialog, which -> dialog.dismiss() }
            val layout = this@VatTuActivity.layoutInflater.inflate(R.layout.layout_dialog_update_feature_listview, null) as LinearLayout
            builder.setView(layout)
            val layoutTextView = layout.findViewById<FrameLayout>(R.id.layout_edit_viewmoreinfo_TextView)
            val textView = layout.findViewById<TextView>(R.id.txt_edit_viewmoreinfo)
            val img_selectTime = layout.findViewById<ImageView>(R.id.img_selectTime)
            val layoutEditText = layout.findViewById<LinearLayout>(R.id.layout_edit_viewmoreinfo_Editext)
            val editText = layout.findViewById<EditText>(R.id.etxt_edit_viewmoreinfo)
            val layoutSpin = layout.findViewById<LinearLayout>(R.id.layout_edit_viewmoreinfo_Spinner)
            val spin = layout.findViewById<Spinner>(R.id.spin_edit_viewmoreinfo)
            val autoCompleteTextView = layout.findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
            val field = mApplication!!.vatTuKHSFT!!.getField(item.fieldName!!)
            val domain = field.domain
            if (field.name == Constant.VatTuFields.MaVatTu) {
                layout.findViewById<View>(R.id.layout_edit_viewmoreinfo_AutoComplete).visibility = View.VISIBLE
                if (this.dmVatTuFeatures != null) {
                    val loaiVatTu = ArrayList<String>()
                    for (feature in this.dmVatTuFeatures!!)
                        loaiVatTu.add(feature.attributes[Constant.LoaiVatTuFields.TenVatTu].toString())
                    val adapter = ArrayAdapter(layout.context, android.R.layout.simple_list_item_1, loaiVatTu)
                    autoCompleteTextView.threshold = 1
                    autoCompleteTextView.setAdapter(adapter)
                    if (item.value != null)
                        autoCompleteTextView.setText(item.value)
                }
            } else if (domain != null) {
                layoutSpin.visibility = View.VISIBLE
                val codedValues = (domain as CodedValueDomain).codedValues
                if (codedValues != null) {
                    val codes = ArrayList<String>()
                    for (codedValue in codedValues)
                        codes.add(codedValue.name)
                    val adapter = ArrayAdapter(layout.context, android.R.layout.simple_list_item_1, codes)
                    spin.adapter = adapter
                    if (item.value != null) spin.setSelection(codes.indexOf(item.value!!))
                }
            } else
                when (item.fieldType) {
                    Field.Type.DATE -> {
                        layoutTextView.visibility = View.VISIBLE
                        textView.text = item.value
                        img_selectTime.setOnClickListener { v ->
                            val dialogView = View.inflate(this@VatTuActivity, R.layout.date_time_picker, null)
                            val alertDialog = android.app.AlertDialog.Builder(this@VatTuActivity).create()
                            dialogView.findViewById<View>(R.id.date_time_set).setOnClickListener { view1 ->
                                val datePicker = dialogView.findViewById<DatePicker>(R.id.date_picker)
                                calendar[0] = GregorianCalendar(datePicker.year, datePicker.month, datePicker.dayOfMonth)
                                val date = String.format("%02d/%02d/%d", datePicker.dayOfMonth, datePicker.month + 1, datePicker.year)
                                textView.text = date
                                alertDialog.dismiss()
                            }
                            alertDialog.setView(dialogView)
                            alertDialog.show()
                        }
                    }
                    Field.Type.TEXT -> {
                        layoutEditText.visibility = View.VISIBLE
                        editText.setText(item.value)
                    }
                    Field.Type.SHORT -> {
                        layoutEditText.visibility = View.VISIBLE
                        editText.inputType = InputType.TYPE_CLASS_NUMBER
                        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                        editText.setText(item.value)
                    }
                    Field.Type.DOUBLE -> {
                        layoutEditText.visibility = View.VISIBLE
                        editText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
                        editText.filters = arrayOf<InputFilter>(InputFilter.LengthFilter(8))
                        editText.setText(item.value)
                    }
                }
            builder.setPositiveButton("Cập nhật") { dialog, which ->
                if (field.name == Constant.VatTuFields.MaVatTu) {
                    val text = autoCompleteTextView.text
                    if (text != null) {
                        val feature = getLoaiVatTu_Ten(text.toString())
                        if (feature != null)
                            item.value = text.toString()
                        else
                            Toast.makeText(this@VatTuActivity, this@VatTuActivity.getString(R.string.INCORRECT_INPUT_FORMAT_WITH_TEXT), Toast.LENGTH_LONG).show()
                    }
                } else if (domain != null) {
                    item.value = spin.selectedItem.toString()
                } else {
                    when (item.fieldType) {
                        Field.Type.DATE -> {
                            item.value = textView.text.toString()
                            item.calendar = calendar[0]
                        }
                        Field.Type.DOUBLE -> try {
                            val x = java.lang.Double.parseDouble(editText.text.toString())
                            item.value = editText.text.toString()
                        } catch (e: Exception) {
                            Toast.makeText(this@VatTuActivity, this@VatTuActivity.getString(R.string.INCORRECT_INPUT_FORMAT), Toast.LENGTH_LONG).show()
                        }

                        Field.Type.TEXT -> item.value = editText.text.toString()
                        Field.Type.SHORT -> try {
                            val x = java.lang.Short.parseShort(editText.text.toString())
                            item.value = editText.text.toString()
                        } catch (e: Exception) {
                            Toast.makeText(this@VatTuActivity, this@VatTuActivity.getString(R.string.INCORRECT_INPUT_FORMAT), Toast.LENGTH_LONG).show()
                        }

                    }
                }
                val adapter = parent.adapter as ChiTietVatTuAdapter
                NotifyVatTuDongHoAdapterChangeAsync(this@VatTuActivity).execute(adapter)
            }
            builder.setView(layout)
            val dialog = builder.create()
            dialog.show()

        }
    }

}
