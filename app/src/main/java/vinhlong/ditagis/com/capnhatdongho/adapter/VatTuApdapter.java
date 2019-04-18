
package vinhlong.ditagis.com.capnhatdongho.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import vinhlong.ditagis.com.capnhatdongho.R;

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
            convertView = inflater.inflate(R.layout.item_text_text_image, null);
        }
        VatTu vatTu = vatTus.get(position);
        TextView textViewItem1 = (TextView) convertView.findViewById(R.id.txtItem1);
        TextView textViewItem2 = (TextView) convertView.findViewById(R.id.txtItem2);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.img_Item);
        textViewItem1.setText(vatTu.getdBDongHoNuoc());
        textViewItem2.setText(vatTu.getTenMau());
        if (vatTu.isView) {
            imageView.setVisibility(View.VISIBLE);
        } else imageView.setVisibility(View.GONE);
        return convertView;
    }

    public static class VatTu {
        private String OBJECTID;
        private String dBDongHoNuoc;
        private String tenMau;
        private Boolean isView;

        public VatTu() {
        }

        public Boolean isView() {
            return isView;
        }

        public void setView(Boolean isView) {
            this.isView = isView;
        }

        public String getOBJECTID() {
            return OBJECTID;
        }

        public void setOBJECTID(String OBJECTID) {
            this.OBJECTID = OBJECTID;
        }

        public String getdBDongHoNuoc() {
            return dBDongHoNuoc;
        }

        public void setdBDongHoNuoc(String dBDongHoNuoc) {
            this.dBDongHoNuoc = dBDongHoNuoc;
        }

        public String getTenMau() {
            return tenMau;
        }

        public void setTenMau(String tenMau) {
            this.tenMau = tenMau;
        }
    }

}
