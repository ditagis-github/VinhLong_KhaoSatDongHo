
package vinhlong.ditagis.com.khaosatdongho.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;

public class VatTuApdapter extends ArrayAdapter<VatTuApdapter.VatTu> {
    private Context context;
    private ArrayList<VatTuApdapter.VatTu> vatTus;
    public VatTuApdapter(Context context, ArrayList<VatTuApdapter.VatTu> vatTus) {
        super(context, 0, vatTus);
        this.context = context;
        this.vatTus = vatTus;
    }

    public ArrayList<VatTuApdapter.VatTu> getVatTus() {
        return vatTus;
    }

    public void setVatTus(ArrayList<VatTuApdapter.VatTu> vatTus) {
        this.vatTus = vatTus;
    }

    public void clear() {
        vatTus.clear();
    }

    @Override
    public int getCount() {
        return vatTus.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_vattu, null);
        }
        VatTuApdapter.VatTu vatTu = vatTus.get(position);
        TextView txtStt = convertView.findViewById(R.id.txtStt);
        TextView txtTenVatTu = convertView.findViewById(R.id.txtTenVatTu);
        TextView txtDonGiaVatTu = convertView.findViewById(R.id.txtDonGiaVatTu);
        TextView txtSoLuong = convertView.findViewById(R.id.txtSoLuong);
        txtStt.setText(vatTu.getStt());
        txtTenVatTu.setText(vatTu.getTenVatTu());
        txtDonGiaVatTu.setText(vatTu.getGiaNC());
        txtSoLuong.setText(vatTu.getSoLuongVatTu());
        return convertView;
    }

    public static class VatTu {
        private int stt;
        private String maVatTu;
        private String tenVatTu;
        private double soLuongVatTu;
        private String donViTinh;
        private double giaNC;
        private double giaVT;
        private long iDKhachHang;
        public NumberFormat formatter = new DecimalFormat("###,###,###");
        public VatTu() {
        }

        public String getStt() {
            return String.valueOf(stt);
        }

        public VatTu(int stt) {
            this.stt = stt;
        }

        public String getMaVatTu() {
            return maVatTu;
        }

        public void setMaVatTu(String maVatTu) {
            this.maVatTu = maVatTu;
        }

        public String getTenVatTu() {
            return tenVatTu;
        }

        public void setTenVatTu(String tenVatTu) {
            this.tenVatTu = tenVatTu;
        }
        public double getSoLuong(){
            return soLuongVatTu;
        }

        public String  getSoLuongVatTu() {
            String soLuong;
            if ((soLuongVatTu == Math.floor(soLuongVatTu)) && !Double.isInfinite(soLuongVatTu)) {
                soLuong = String.valueOf( this.soLuongVatTu);
            }
            else soLuong = String.valueOf(soLuongVatTu);
            return soLuong + " (" + this.donViTinh + ")";
        }

        public void setSoLuongVatTu(double soLuongVatTu) {
            try{
                this.soLuongVatTu = soLuongVatTu;
            }catch (Exception e){
            }
        }
        public void setGiaNC(String giaNC) {
            try{
                this.giaNC = Double.parseDouble(giaNC);
            }catch (Exception e){
            }
        }

        public String getGiaNC() {
            return formatter.format(giaNC) + Constant.DefineST.DonViTien;
        }
        public void setGiaVT(String giaVT) {
            try{
                this.giaVT = Double.parseDouble(giaVT);
            }catch (Exception e){
            }
        }

        public String getGiaVT() {
            return formatter.format(giaVT) + Constant.DefineST.DonViTien;
        }

        public String getDonViTinh() {
            return donViTinh;
        }

        public void setDonViTinh(String donViTinh) {
            this.donViTinh = donViTinh;
        }

        public long getiDKhachHang() {
            return iDKhachHang;
        }

        public void setiDKhachHang(long iDKhachHang) {
            this.iDKhachHang = iDKhachHang;
        }
    }

}
