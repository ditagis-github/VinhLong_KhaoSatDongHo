
package vinhlong.ditagis.com.khaosatdongho.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.esri.arcgisruntime.data.Feature;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.libs.TimeAgo;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;

public class DanhSachDongHoKHAdapter extends ArrayAdapter<Feature> {
    private Context context;
    private List<Feature> items;


    public DanhSachDongHoKHAdapter(Context context, List<Feature> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public List<Feature> getItems() {
        return items;
    }

    public void setItems(List<Feature> items) {
        this.items = items;
    }

    public void clear() {
        items.clear();
    }

    @Override
    public int getCount() {
        return items.size();
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
            convertView = inflater.inflate(R.layout.item_tracuu, null);
        }
        Feature item = items.get(position);
        TextView txt_thoigian = convertView.findViewById(R.id.txt_thoigian);
        TextView txt_diachi = convertView.findViewById(R.id.txt_diachi);
        TextView txt_sodienthoai = convertView.findViewById(R.id.txt_sodienthoai);
        Map<String, Object> attributes = item.getAttributes();
        Object ngayCapNhat = attributes.get(Constant.DongHoKhachHangFields.NGAY_CAP_NHAT);
        Object time = ngayCapNhat;
        if (ngayCapNhat != null) {
            long endTime = Calendar.getInstance().getTimeInMillis();
            long startTime = ((Calendar) ngayCapNhat).getTimeInMillis();
            time = TimeAgo.DateDifference(endTime - startTime);

        }
        txt_thoigian.setText(attributes.get(Constant.DongHoKhachHangFields.TEN_KH) + " được giao " + time);
        txt_diachi.setText("Tại địa chỉ: " + attributes.get(Constant.DongHoKhachHangFields.DIA_CHI));
        txt_sodienthoai.setText("SĐT: " + attributes.get(Constant.DongHoKhachHangFields.SO_DIEN_THOAI));
        return convertView;
    }

}
