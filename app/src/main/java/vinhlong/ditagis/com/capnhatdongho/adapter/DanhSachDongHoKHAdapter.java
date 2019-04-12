
package vinhlong.ditagis.com.capnhatdongho.adapter;

        import android.annotation.SuppressLint;
        import android.content.Context;
        import android.support.annotation.NonNull;
        import android.view.LayoutInflater;
        import android.view.View;

        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;

        import java.util.List;

        import vinhlong.ditagis.com.capnhatdongho.R;
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
        TextView txt_tracuu_id = (TextView) convertView.findViewById(R.id.txt_tracuu_id);
        TextView txt_tracuu_ngaycapnhat = (TextView) convertView.findViewById(R.id.txt_tracuu_ngaycapnhat);
        TextView txt_tracuu_diachi = (TextView) convertView.findViewById(R.id.txt_tracuu_diachi);
        txt_tracuu_id.setText(item.getDbDongHoNuoc());
        txt_tracuu_ngaycapnhat.setText(item.getMaKhachHang());
        txt_tracuu_diachi.setText(item.getTenThueBao());
        return convertView;
    }

    public static class Item{
        private String objectID;
        private String dbDongHoNuoc;
        private String maKhachHang;
        private String tenThueBao;

        public Item() {
        }

        public Item(String objectID, String dbDongHoNuoc, String maKhachHang) {
            this.objectID = objectID;
            this.dbDongHoNuoc = dbDongHoNuoc;
            this.maKhachHang = maKhachHang;
        }

        public String getObjectID() {
            return objectID;
        }

        public void setObjectID(String objectID) {
            this.objectID = objectID;
        }

        public String getDbDongHoNuoc() {
            return dbDongHoNuoc;
        }

        public void setDbDongHoNuoc(String dbDongHoNuoc) {
            this.dbDongHoNuoc = dbDongHoNuoc;
        }

        public String getMaKhachHang() {
            return maKhachHang;
        }

        public void setMaKhachHang(String maKhachHang) {
            this.maKhachHang = maKhachHang;
        }

        public String getTenThueBao() {
            return tenThueBao;
        }

        public void setTenThueBao(String tenThueBao) {
            this.tenThueBao = tenThueBao;
        }
    }

}
