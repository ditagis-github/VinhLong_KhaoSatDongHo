package vinhlong.ditagis.com.khaosatdongho;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ArcGISFeature;

import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.async.EditAsync;
import vinhlong.ditagis.com.khaosatdongho.async.LoadingDataFeatureAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;

public class UpdateActivity extends AppCompatActivity {
    private DApplication mApplication;


    private SwipeRefreshLayout mmSwipe;
    private ArcGISFeature mArcGISFeature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);
        mApplication = (DApplication) getApplication();
        initViews();
    }

    private void update() {
        ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_progress)).setVisibility(View.VISIBLE);
        ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_main)).setVisibility(View.GONE);
        ((TextView) UpdateActivity.this.findViewById(R.id.txt_update_feature_progress)).setText("Đang lưu...");
        new EditAsync((TextView) UpdateActivity.this.findViewById(R.id.txt_update_feature_progress), UpdateActivity.this, mApplication.getDongHoKHSFT(),
                mApplication.getSelectedFeature(), new EditAsync.AsyncResponse() {
            @Override
            public void processFinish(Boolean feature) {
                ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_progress)).setVisibility(View.GONE);
                ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_main)).setVisibility(View.VISIBLE);
                if (feature != null) {
                    Toast.makeText(UpdateActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(UpdateActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
            }
        }).execute();

    }

    private void initViews() {
        findViewById(R.id.btn_update_feature_update).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update();
            }
        });
        mmSwipe = findViewById(R.id.swipe_udpate_feature);

        ((TextView) findViewById(R.id.txt_update_feature_progress)).setText("Đang khởi tạo thuộc tính...");
        findViewById(R.id.llayout_update_feature_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.llayout_update_feature_main).setVisibility(View.GONE);
        mArcGISFeature = mApplication.getSelectedFeature();

        mmSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
                mmSwipe.setRefreshing(false);
            }
        });

        loadData();
    }

    private void loadData() {
        ((LinearLayout) findViewById(R.id.llayout_update_feature_field)).removeAllViews();
        findViewById(R.id.llayout_update_feature_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.llayout_update_feature_main).setVisibility(View.GONE);

        new LoadingDataFeatureAsync(UpdateActivity.this, mApplication.getSelectedFeature(), new LoadingDataFeatureAsync.AsyncResponse() {

            @Override
            public void processFinish(List<View> views) {
                if (views != null) {
                    for (View view : views) {
                        ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_field)).addView(view);
                    }
                    ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_progress)).setVisibility(View.GONE);
                    ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_main)).setVisibility(View.VISIBLE);
                }


            }
        }).execute();


    }
}
