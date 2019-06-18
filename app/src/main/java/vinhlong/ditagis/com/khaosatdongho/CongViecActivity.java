package vinhlong.ditagis.com.khaosatdongho;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.khaosatdongho.adapter.ThongKeAdapter;
import vinhlong.ditagis.com.khaosatdongho.async.QueryDongHoKhachHangAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;

public class CongViecActivity extends AppCompatActivity {
    private TextView txtTongItem;
    private ServiceFeatureTable serviceFeatureTable;
    private ThongKeAdapter thongKeAdapter;
    private DApplication dApplication;
    private LinearLayout mBottomLayout;
    private BottomSheetDialog mBottomSheetDialog;


    private DanhSachDongHoKHAdapter.Item mSelectedDongHo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congviec);
        init();


        serviceFeatureTable = (ServiceFeatureTable) dApplication.getDongHoKHDTG().getFeatureLayer().getFeatureTable();
        String whereClause = "1 = 1";
        getQueryDiemDanhGiaAsync(whereClause);
    }

    private void init() {
        dApplication = (DApplication) getApplication();
        initBottomSheet();

    }

    private void initBottomSheet() {
        mBottomSheetDialog = new BottomSheetDialog(CongViecActivity.this);
        mBottomLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.layout_handle_item_cong_viec, null);
        mBottomLayout.findViewById(R.id.llayout__handle_item_cong_viec_tim_duong).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();

                if (mSelectedDongHo.getDiaChi() == null || mSelectedDongHo.getDiaChi().equals("null")) {
                    Toast.makeText(CongViecActivity.this, "Chưa có thông tin địa chỉ!", Toast.LENGTH_SHORT).show();
                } else {
                    String uri = String.format("google.navigation:q=%s", Uri.encode(mSelectedDongHo.getDiaChi()));
                    Uri gmmIntentUri = Uri.parse(uri);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }
            }
        });
        mBottomLayout.findViewById(R.id.llayout__handle_item_cong_viec_ban_do).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mBottomSheetDialog.isShowing())
                    mBottomSheetDialog.dismiss();
                Intent returnIntent = new Intent();
                returnIntent.putExtra(getString(R.string.ket_qua_objectid), mSelectedDongHo.getObjectID());
                setResult(Activity.RESULT_OK, returnIntent);


                finish();
            }
        });
        mBottomSheetDialog.setContentView(mBottomLayout);
    }

    private void setBottomLayout(DanhSachDongHoKHAdapter.Item dongHo) {
        LinearLayout layout = mBottomLayout.findViewById(R.id.llayout__handle_item_cong_viec__info);

        layout.removeAllViews();
        layout.addView(getLayoutInfo("Mã đồng hồ", dongHo.getIdDongHo()));
        layout.addView(getLayoutInfo("Mã khách hàng", dongHo.getMaKhachHang()));
        layout.addView(getLayoutInfo("Tên khách hàng", dongHo.getTenKhachHang()));
        layout.addView(getLayoutInfo("Số điện thoại", dongHo.getSoDienThoai()));
        layout.addView(getLayoutInfo("Địa chỉ", dongHo.getDiaChi()));
        layout.addView(getLayoutInfo("Ngày giao", dongHo.getThoiGian()));

    }

    private View getLayoutInfo(String alias, String value) {
        LinearLayout view = (LinearLayout) CongViecActivity.this.getLayoutInflater().inflate(R.layout.item_viewinfo, null);
        ((TextView) view.findViewById(R.id.txt_viewinfo_alias)).setText(alias);

        String tempValue = value;
        if (value == null || value.equals("null"))
            tempValue = "Chưa xác định";
        ((TextView) view.findViewById(R.id.txt_viewinfo_value)).setText(tempValue);

        return view;
    }

    private void getQueryDiemDanhGiaAsync(String whereClause) {
        ListView listView = findViewById(R.id.listview);
        final List<DanhSachDongHoKHAdapter.Item> items = new ArrayList<>();
        final DanhSachDongHoKHAdapter adapter = new DanhSachDongHoKHAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            mSelectedDongHo = adapter.getItems().get(position);

            setBottomLayout(mSelectedDongHo);
            mBottomSheetDialog.show();
        });
        if (serviceFeatureTable != null)
            new QueryDongHoKhachHangAsync(this, serviceFeatureTable, txtTongItem, adapter, features -> {

            }).execute(whereClause);
    }


}
