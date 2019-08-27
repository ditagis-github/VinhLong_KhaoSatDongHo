package vinhlong.ditagis.com.khaosatdongho.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.ArrayList

import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.utities.Constant

class VatTuApdapter(private val mContext: Context, var vatTus: ArrayList<VatTuApdapter.VatTu>?) : ArrayAdapter<VatTuApdapter.VatTu>(mContext, 0, vatTus) {

    override fun clear() {
        vatTus!!.clear()
    }

    override fun getCount(): Int {
        return vatTus!!.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_vattu, null)
        }
        val vatTu = vatTus!![position]
        val txtStt = convertView!!.findViewById<TextView>(R.id.txtStt)
        val txtTenVatTu = convertView.findViewById<TextView>(R.id.txtTenVatTu)
        val txtDonGiaVatTu = convertView.findViewById<TextView>(R.id.txtDonGiaVatTu)
        val txtSoLuong = convertView.findViewById<TextView>(R.id.txtSoLuong)
        txtStt.text = vatTu.getStt()
        txtTenVatTu.text = vatTu.tenVatTu
        txtDonGiaVatTu.text = vatTu.getGiaNC()
        txtSoLuong.text = vatTu.soLuongVatTu
        return convertView
    }

    class VatTu {
        private var stt: Int = 0
        var maVatTu: String? = null
        var tenVatTu: String? = null
        var soLuong: Double = 0.toDouble()
            private set
        var donViTinh: String? = null
        private var giaNC: Double = 0.toDouble()
        private var giaVT: Double = 0.toDouble()
        private var iDKhachHang: Long = 0
        var formatter: NumberFormat = DecimalFormat("###,###,###")

        val soLuongVatTu: String
            get() {
                val soLuong: String
                if (this.soLuong == Math.floor(this.soLuong) && !java.lang.Double.isInfinite(this.soLuong)) {
                    soLuong = this.soLuong.toString()
                } else
                    soLuong = this.soLuong.toString()
                return soLuong + " (" + this.donViTinh + ")"
            }

        constructor() {}

        fun getStt(): String {
            return stt.toString()
        }

        constructor(stt: Int) {
            this.stt = stt
        }

        fun setSoLuongVatTu(soLuongVatTu: Double) {
            try {
                this.soLuong = soLuongVatTu
            } catch (e: Exception) {
            }

        }

        fun setGiaNC(giaNC: String) {
            try {
                this.giaNC = java.lang.Double.parseDouble(giaNC)
            } catch (e: Exception) {
            }

        }

        fun getGiaNC(): String {
            return formatter.format(giaNC) + Constant.DefineST.DonViTien
        }

        fun setGiaVT(giaVT: String) {
            try {
                this.giaVT = java.lang.Double.parseDouble(giaVT)
            } catch (e: Exception) {
            }

        }

        fun getGiaVT(): String {
            return formatter.format(giaVT) + Constant.DefineST.DonViTien
        }

        fun getiDKhachHang(): Long {
            return iDKhachHang
        }

        fun setiDKhachHang(iDKhachHang: Long) {
            this.iDKhachHang = iDKhachHang
        }
    }

}
