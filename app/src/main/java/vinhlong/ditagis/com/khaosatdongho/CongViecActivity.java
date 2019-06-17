package vinhlong.ditagis.com.khaosatdongho;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.khaosatdongho.adapter.ThongKeAdapter;
import vinhlong.ditagis.com.khaosatdongho.async.QueryDongHoKhachHangAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.ListObjectDB;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.TimePeriodReport;

public class CongViecActivity extends AppCompatActivity {
    private TextView txtTongItem;
    private ServiceFeatureTable serviceFeatureTable;
    private ThongKeAdapter thongKeAdapter;
    private DApplication dApplication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_congviec);
        List<ThongKeAdapter.Item> items = new ArrayList<>();
        dApplication = (DApplication) getApplication();
        serviceFeatureTable = (ServiceFeatureTable) dApplication.getDongHoKHDTG().getFeatureLayer().getFeatureTable();
        String whereClause = "1 = 1";
        getQueryDiemDanhGiaAsync(whereClause);
    }

    private void getQueryDiemDanhGiaAsync(String whereClause) {
        ListView listView = findViewById(R.id.listview);
        final List<DanhSachDongHoKHAdapter.Item> items = new ArrayList<>();
        final DanhSachDongHoKHAdapter adapter = new DanhSachDongHoKHAdapter(this, items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(getString(R.string.ket_qua_objectid), adapter.getItems().get(position).getObjectID());
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        });
        if (serviceFeatureTable != null)
            new QueryDongHoKhachHangAsync(this, serviceFeatureTable, txtTongItem, adapter, features -> {

            }).execute(whereClause);
    }



}
