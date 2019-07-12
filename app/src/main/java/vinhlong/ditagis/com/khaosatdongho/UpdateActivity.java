package vinhlong.ditagis.com.khaosatdongho;

import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Field;

import java.util.HashMap;
import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.async.EditAsync;
import vinhlong.ditagis.com.khaosatdongho.async.LoadingDataFeatureAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;

public class UpdateActivity extends AppCompatActivity {
    private DApplication mApplication;


    private SwipeRefreshLayout mmSwipe;
    private ArcGISFeature mArcGISFeature;
    private LinearLayout mLLayoutField;
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
                mApplication.getSelectedFeature(), feature -> {
                    UpdateActivity.this.findViewById(R.id.llayout_update_feature_progress).setVisibility(View.GONE);
                    ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_main)).setVisibility(View.VISIBLE);
                    if (feature != null) {
                        Toast.makeText(UpdateActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(UpdateActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                }).execute(getAttributes());

    }

    private HashMap<String, Object> getAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();
        if (mLLayoutField == null) return attributes;
        String currentAlias = "";
        for (int i = 0; i < mLLayoutField.getChildCount(); i++) {
            LinearLayout itemAddFeature = (LinearLayout) mLLayoutField.getChildAt(i);
            for (int j = 0; j < itemAddFeature.getChildCount(); j++) {
                TextInputLayout typeInputItemAddFeature = (TextInputLayout) itemAddFeature.getChildAt(j);
                if (typeInputItemAddFeature.getVisibility() == View.VISIBLE) {
                    currentAlias = typeInputItemAddFeature.getHint().toString();
                    View view = ((FrameLayout) typeInputItemAddFeature.getChildAt(0)).getChildAt(0);

                    if (view instanceof TextInputEditText && !currentAlias.isEmpty()) {
                        for (Field fieldEdittext : mApplication.getDongHoKHSFT().getFields()) {
                            if (fieldEdittext.getAlias().equals(currentAlias)) {
                                if (fieldEdittext.getDomain() != null) {
                                    List<CodedValue> codedValues = ((CodedValueDomain) fieldEdittext.getDomain()).getCodedValues();


                                    Object valueDomain = getCodeDomain(codedValues, ((TextInputEditText) view).getText().toString());
                                    if (valueDomain != null)
                                        attributes.put(currentAlias, valueDomain.toString());
                                } else {
                                    attributes.put(currentAlias, ((TextInputEditText) view).getText().toString());
                                }
                                break;
                            }
                        }
                    } else if (view instanceof Spinner && !currentAlias.isEmpty()) {
                        for (Field fieldSpinner : mApplication.getDongHoKHSFT().getFields()) {
                            if (fieldSpinner.getAlias().equals(currentAlias)) {
                                if (fieldSpinner.getDomain() != null) {
                                    List<CodedValue> codedValues = ((CodedValueDomain) fieldSpinner.getDomain()).getCodedValues();

                                    Object valueDomain = getCodeDomain(codedValues, ((Spinner) view).getSelectedItem().toString());
                                    if (valueDomain != null)
                                        attributes.put(currentAlias, valueDomain.toString());
                                } else {
                                }
                                break;
                            }
                        }
                    }

                }
            }
        }
        return attributes;
    }
    private Object getCodeDomain(List<CodedValue> codedValues, String value) {
        Object code = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getName().equals(value)) {
                code = codedValue.getCode();
                break;
            }
        }
        return code;
    }
    private void initViews() {
        mLLayoutField = findViewById(R.id.llayout_update_feature_field);

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
       mLLayoutField.removeAllViews();
        findViewById(R.id.llayout_update_feature_progress).setVisibility(View.VISIBLE);
        findViewById(R.id.llayout_update_feature_main).setVisibility(View.GONE);

        new LoadingDataFeatureAsync(UpdateActivity.this, mApplication.getSelectedFeature(), new LoadingDataFeatureAsync.AsyncResponse() {

            @Override
            public void processFinish(List<View> views) {
                if (views != null) {
                    for (View view : views) {
                      mLLayoutField.addView(view);
                    }
                    ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_progress)).setVisibility(View.GONE);
                    ((LinearLayout) UpdateActivity.this.findViewById(R.id.llayout_update_feature_main)).setVisibility(View.VISIBLE);
                }


            }
        }).execute();


    }
}
