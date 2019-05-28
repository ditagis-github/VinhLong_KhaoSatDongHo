
package vinhlong.ditagis.com.capnhatdongho.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;

public class VatTuApdapter extends ArrayAdapter<VatTuApdapter.VatTu> {
    private Context context;
    private List<VatTu> vatTus;
    public VatTuApdapter(Context context, List<VatTu> vatTus) {
        super(context, 0, vatTus);
        this.context = context;
        this.vatTus = vatTus;
    }

    public List<VatTu> getVatTus() {
        return vatTus;
    }

    public void setVatTus(List<VatTu> vatTus) {
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
        VatTu vatTu = vatTus.get(position);
        TextView txtTenVatTu = convertView.findViewById(R.id.txtTenVatTu);
        TextView txtSoLuongVatTu = convertView.findViewById(R.id.txtSoLuongVatTu);
        TextView txtDonGiaVatTu = convertView.findViewById(R.id.txtDonGiaVatTu);
        TextView txtTongTien = convertView.findViewById(R.id.txtTongTien);
        ImageView imageView = convertView.findViewById(R.id.img_Item);
        txtTenVatTu.setText(vatTu.getTenVatTu());
        txtSoLuongVatTu.setText(vatTu.getSoLuongVatTu());
        txtDonGiaVatTu.setText(vatTu.getDonGiaVatTu());
        txtTongTien.setText(vatTu.getTongTien());
        if (vatTu.isView) {
            imageView.setVisibility(View.VISIBLE);
        } else imageView.setVisibility(View.GONE);
        return convertView;
    }

    public static class VatTu {
        private String objectID;
        private String tenVatTu;
        private int soLuongVatTu;
        private double donGiaVatTu;
        private String donViTinh;
        private double tongTien;
        private Boolean isView;
        public NumberFormat formatter = new DecimalFormat("###,###,###");

        public VatTu() {
        }

        public String getObjectID() {
            return objectID;
        }

        public void setObjectID(String objectID) {
            this.objectID = objectID;
        }

        public String getTenVatTu() {
            return tenVatTu;
        }

        public void setTenVatTu(String tenVatTu) {
            this.tenVatTu = tenVatTu;
        }

        public String  getSoLuongVatTu() {
            return String.valueOf(soLuongVatTu) + " (" + this.donViTinh + ")";
        }

        public void setSoLuongVatTu(String soLuongVatTu) {
            try{
                this.soLuongVatTu = Integer.parseInt(soLuongVatTu);
            }catch (Exception e){
            }
        }

        public String getDonGiaVatTu() {
            return formatter.format(donGiaVatTu) + Constant.DefineST.DonViTien;
        }

        public void setDonGiaVatTu(String donGiaVatTu) {
            try{
                this.donGiaVatTu = Double.parseDouble(donGiaVatTu);
            }catch (Exception e){
            }
        }
        public String getTongTien() {
            this.tongTien = this.donGiaVatTu * this.soLuongVatTu;
            return formatter.format(this.tongTien) + Constant.DefineST.DonViTien;
        }
        public String getDonViTinh() {
            return donViTinh;
        }

        public void setDonViTinh(String donViTinh) {
            this.donViTinh = donViTinh;
        }

        public Boolean getView() {
            return isView;
        }

        public void setView(Boolean view) {
            isView = view;
        }

    }

}
