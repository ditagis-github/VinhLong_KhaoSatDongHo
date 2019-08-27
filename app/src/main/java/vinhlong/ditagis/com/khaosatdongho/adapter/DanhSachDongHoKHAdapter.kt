package vinhlong.ditagis.com.khaosatdongho.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import com.esri.arcgisruntime.data.Feature

import java.util.Calendar

import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.libs.TimeAgo
import vinhlong.ditagis.com.khaosatdongho.utities.Constant

class DanhSachDongHoKHAdapter(private var mContext: Context, private var items: MutableList<Feature>?) : ArrayAdapter<Feature>(mContext, 0, items) {

    override fun getContext(): Context {
        return mContext
    }

    fun getItems(): List<Feature>? {
        return items
    }

    fun setItems(items: MutableList<Feature>) {
        this.items = items
    }

    override fun clear() {
        items!!.clear()
    }

    override fun getCount(): Int {
        return items!!.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor", "SetTextI18n")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_tracuu, null)
        }
        val item = items!![position]
        val txt_thoigian = convertView!!.findViewById<TextView>(R.id.txt_thoigian)
        val txt_diachi = convertView.findViewById<TextView>(R.id.txt_diachi)
        val txt_sodienthoai = convertView.findViewById<TextView>(R.id.txt_sodienthoai)
        val attributes = item.attributes
        val ngayCapNhat = attributes[Constant.DongHoKhachHangFields.NGAY_CAP_NHAT]
        var time = ngayCapNhat
        if (ngayCapNhat != null) {
            val endTime = Calendar.getInstance().timeInMillis
            val startTime = (ngayCapNhat as Calendar).timeInMillis
            time = TimeAgo.DateDifference(endTime - startTime)

        }
        txt_thoigian.text = attributes[Constant.DongHoKhachHangFields.TEN_KH].toString() + " được giao " + time
        txt_diachi.text = "Tại địa chỉ: " + attributes[Constant.DongHoKhachHangFields.DIA_CHI]
        txt_sodienthoai.text = "SĐT: " + attributes[Constant.DongHoKhachHangFields.SO_DIEN_THOAI]
        return convertView
    }

}
