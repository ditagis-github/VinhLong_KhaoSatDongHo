
package vinhlong.ditagis.com.khaosatdongho.adapter;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.support.annotation.NonNull;
        import android.view.LayoutInflater;
        import android.view.View;

        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.List;

        import vinhlong.ditagis.com.khaosatdongho.R;
public class DanhSachDongHoKHAdapter extends ArrayAdapter<DanhSachDongHoKHAdapter.Item> {
    private Context context;
    private List<Item> items;


    public DanhSachDongHoKHAdapter(Context context, List<DanhSachDongHoKHAdapter.Item> items) {
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

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
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
        Item item = items.get(position);
        TextView txt_thoigian = convertView.findViewById(R.id.txt_thoigian);
        TextView txt_diachi = convertView.findViewById(R.id.txt_diachi);
        TextView txt_sodienthoai = convertView.findViewById(R.id.txt_sodienthoai);
        txt_thoigian.setText(item.getTenKhachHang() + " được giao " + item.getThoiGian());
        txt_diachi.setText("Tại địa chỉ: " + item.getDiaChi());
        txt_sodienthoai.setText("SĐT: " + item.getSoDienThoai());
        return convertView;
    }

    public static class Item{
        private String objectID;
        private String idDongHo;
        private String cmnd;
        private String tenKhachHang;
        private String diaChi;
        private String soDienThoai;
        private String thoiGian;

        public Item() {
        }

        public Item(String objectID) {
            this.objectID = objectID;
        }

        public String getObjectID() {
            return objectID;
        }

        public void setObjectID(String objectID) {
            this.objectID = objectID;
        }

        public String getIdDongHo() {
            return idDongHo;
        }

        public void setIdDongHo(String idDongHo) {
            this.idDongHo = idDongHo;
        }

        public String getCmnd() {
            return cmnd;
        }

        public void setCmnd(String cmnd) {
            this.cmnd = cmnd;
        }

        public String getTenKhachHang() {
            return tenKhachHang;
        }

        public void setTenKhachHang(String tenKhachHang) {
            this.tenKhachHang = tenKhachHang;
        }

        public String getDiaChi() {
            return diaChi;
        }

        public void setDiaChi(String diaChi) {
            this.diaChi = diaChi;
        }

        public String getSoDienThoai() {
            return soDienThoai;
        }

        public void setSoDienThoai(String soDienThoai) {
            this.soDienThoai = soDienThoai;
        }

        public String getThoiGian() {
            return thoiGian;
        }

        public void setThoiGian(String thoiGian) {
            this.thoiGian = thoiGian;
        }
    }

}
