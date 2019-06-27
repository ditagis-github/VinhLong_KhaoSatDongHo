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

import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.khaosatdongho.adapter.ThongKeAdapter;
import vinhlong.ditagis.com.khaosatdongho.async.QueryDongHoKhachHangAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.libs.TimeAgo;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;

public class CongViecActivity extends AppCompatActivity {
    private TextView txtTongItem;
    private ServiceFeatureTable serviceFeatureTable;
    private ThongKeAdapter thongKeAdapter;
    private DApplication dApplication;
    private LinearLayout mBottomLayout;
    private BottomSheetDialog mBottomSheetDialog;
    private List<Feature> mFeatures;
    private Feature mSelectedFeature;
//    private DanhSachDongHoKHAdapter.Item mSelectedDongHo;

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
                if (mSelectedFeature.getAttributes().get(Constant.DongHoKhachHangFields.DIA_CHI_LAP_DAT) == null ||
                        mSelectedFeature.getAttributes().get(Constant.DongHoKhachHangFields.DIA_CHI_LAP_DAT).toString().equals("null")) {
                    Toast.makeText(CongViecActivity.this, "Chưa có thông tin địa chỉ!", Toast.LENGTH_SHORT).show();
                } else {
                    String uri = String.format("google.navigation:q=%s", Uri.encode(mSelectedFeature.getAttributes().get(Constant.DongHoKhachHangFields.DIA_CHI_LAP_DAT).toString()));
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
                returnIntent.putExtra(getString(R.string.ket_qua_objectid), mSelectedFeature.getAttributes().get(Constant.DongHoKhachHangFields.OBJECT_ID).toString());
                setResult(Activity.RESULT_OK, returnIntent);


                finish();
            }
        });
        mBottomSheetDialog.setContentView(mBottomLayout);
    }

    private void setBottomLayout(int id) {
        LinearLayout layout = mBottomLayout.findViewById(R.id.llayout__handle_item_cong_viec__info);

        layout.removeAllViews();

        for (Feature feature : mFeatures) {
            if ((int) feature.getAttributes().get(Constant.DongHoKhachHangFields.ID) == id) {
                mSelectedFeature = feature;
                for (Field field : feature.getFeatureTable().getFields()) {
                    if (Constant.DongHoKhachHangFields.OutFields.contains(field.getName())) {
                        Object value = feature.getAttributes().get(field.getName());
                        String valueString = "";
                        if (value != null)
                            valueString = value.toString();
                        layout.addView(getLayoutInfo(field.getAlias(), valueString));
                    }
                }
                break;
            }
        }

//        layout.addView(getLayoutInfo("Mã khách hàng", dongHo.getCmnd()));
//        layout.addView(getLayoutInfo("Tên khách hàng", dongHo.getTenKhachHang()));
//        layout.addView(getLayoutInfo("Số điện thoại", dongHo.getSoDienThoai()));
//        layout.addView(getLayoutInfo("Địa chỉ", dongHo.getDiaChi()));
//        layout.addView(getLayoutInfo("Ngày giao", dongHo.getThoiGian()));

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
        ListView listView = findViewById(R.id.vattu_listview);

        if (serviceFeatureTable != null)
            new QueryDongHoKhachHangAsync(this, serviceFeatureTable, txtTongItem, features -> {
                mFeatures = features;
                if (features != null && features.size() > 0) {

                    final List<DanhSachDongHoKHAdapter.Item> items = new ArrayList<>();
                    for (Feature feature : features) {
                        Map<String, Object> attributes = feature.getAttributes();
                        String objectID = attributes.get(Constant.LayerFields.OBJECTID).toString();
                        DanhSachDongHoKHAdapter.Item item = new DanhSachDongHoKHAdapter.Item(objectID);
                        Object idDongHo = attributes.get(Constant.DongHoKhachHangFields.ID);
                        if (idDongHo != null) {
                            item.setIdDongHo(idDongHo.toString());
                        }
                        Object ngayCapNhat = attributes.get(Constant.DongHoKhachHangFields.NGAY_CAP_NHAT);
                        if (ngayCapNhat != null) {
                            long endTime = Calendar.getInstance().getTimeInMillis();
                            long startTime = ((Calendar) ngayCapNhat).getTimeInMillis();
                            String time = TimeAgo.DateDifference(endTime - startTime);
                            item.setThoiGian(time);
                        }
                        Object tenKH = attributes.get(Constant.DongHoKhachHangFields.TEN_KH);
                        if (tenKH != null) {
                            item.setTenKhachHang(tenKH.toString());
                        }
                        Object maKH = attributes.get(Constant.DongHoKhachHangFields.CMND);
                        if (maKH != null) {
                            item.setCmnd(maKH.toString());
                        }
                        Object soDienThoai = attributes.get(Constant.DongHoKhachHangFields.SO_DIEN_THOAI);
                        if (soDienThoai != null) {
                            item.setSoDienThoai(soDienThoai.toString());
                        }
                        Object diaChi = attributes.get(Constant.DongHoKhachHangFields.DIA_CHI);
                        if (diaChi != null) {
                            item.setDiaChi(diaChi.toString());
                        }
                        items.add(item);
                    }

                    final DanhSachDongHoKHAdapter adapter = new DanhSachDongHoKHAdapter(this, items);
                    listView.setAdapter(adapter);
                    listView.setOnItemClickListener((parent, view, position, id) -> {

                        setBottomLayout(Integer.parseInt(adapter.getItems().get(position).getIdDongHo()));
                        mBottomSheetDialog.show();
                    });
                }
            }).execute(whereClause);


    }


}
