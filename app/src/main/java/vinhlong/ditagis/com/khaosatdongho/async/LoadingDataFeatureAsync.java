package vinhlong.ditagis.com.khaosatdongho.async;


import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Field;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;

public class LoadingDataFeatureAsync extends AsyncTask<Void, Void, Void> {
    private ArcGISFeature mArcGISFeature;

    public interface AsyncResponse {
        void processFinish(List<View> view);
    }

    private AsyncResponse mDelegate;
    private Activity mActivity;

    public LoadingDataFeatureAsync(Activity activity, ArcGISFeature arcGISFeature, AsyncResponse delegate) {
        mArcGISFeature = arcGISFeature;
        mDelegate = delegate;
        mActivity = activity;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        publishProgress();
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        mDelegate.processFinish(loadDataToAdd());
        super.onProgressUpdate(values);
    }

    private List<View> loadDataToAdd() {
        List<View> views = new ArrayList<View>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(mActivity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        for (Field field : mArcGISFeature.getFeatureTable().getFields()) {
            if (Constant.DongHoKhachHangFields.UpdateFields.contains(field.getName()))
                views.add(getView(field));

        }


        return views;
    }

    private View getView(Field field) {
        LinearLayout layoutView = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.item_add_feature, null, false);
        ArrayAdapter adapter = new ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, new ArrayList<String>());
        Spinner spinner = layoutView.findViewById(R.id.spinner_add_spinner_value);
        spinner.setAdapter(adapter);
        Object value = null;
        if (mArcGISFeature != null) {
            value = mArcGISFeature.getAttributes().get(field.getName());
        }
        if (field.getDomain() != null) {
            CodedValueDomain codedValueDomain = (CodedValueDomain) field.getDomain();
            ArrayList values = new ArrayList<String>();
            values.add(Constant.EMPTY);
            String selectedValue = null;
            for (CodedValue codedValue : codedValueDomain.getCodedValues()) {
                values.add(codedValue.getName());
                if (value != null && codedValue.getCode() == value)
                    selectedValue = codedValue.getName();
            }

            layoutView.findViewById(R.id.llayout_add_feature_number_decimal).setVisibility(View.GONE);
            layoutView.findViewById(R.id.llayout_add_feature_spinner).setVisibility(View.VISIBLE);
            layoutView.findViewById(R.id.llayout_add_feature_edittext).setVisibility(View.GONE);
            layoutView.findViewById(R.id.llayout_add_feature_number).setVisibility(View.GONE);

            ((TextView) layoutView.findViewById(R.id.txt_add_spiner_title)).setText(field.getAlias());
            adapter.addAll(values);
            adapter.notifyDataSetChanged();

            for (int i = 0; i < values.size(); i++) {
                if (selectedValue != null && values.get(i) == selectedValue) {
                    spinner.setSelection(i);
                    break;
                }
            }
        } else {
            NumberFormat nm = NumberFormat.getCurrencyInstance();
            switch (field.getFieldType()) {
                case INTEGER:
                case SHORT:
                    layoutView.findViewById(R.id.llayout_add_feature_number_decimal).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_spinner).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_edittext).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_number).setVisibility(View.VISIBLE);


                    ((TextView) layoutView.findViewById(R.id.txt_add_edit_number_title)).setText(field.getAlias());

                    if (value != null) {

                        try {
                            switch (field.getFieldType()) {
                                case INTEGER:
                                    ((TextView) layoutView.findViewById(R.id.etxt_add_edit_number_value)).setText(nm.format(Integer.parseInt(value.toString())));
                                    break;
                                case SHORT:
                                    ((TextView) layoutView.findViewById(R.id.etxt_add_edit_number_value)).setText(nm.format(Short.parseShort(value.toString())));
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "Có lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show();
                        }


                    }
                    break;
                case DOUBLE:
                case FLOAT:
                    layoutView.findViewById(R.id.llayout_add_feature_number_decimal).setVisibility(View.VISIBLE);
                    layoutView.findViewById(R.id.llayout_add_feature_spinner).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_edittext).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_number).setVisibility(View.GONE);


                    ((TextView) layoutView.findViewById(R.id.txt_add_edit_number_decimal_title)).setText(field.getAlias());

                    if (value != null) {
                        try {
                            switch (field.getFieldType()) {
                                case DOUBLE:
                                    ((TextView) layoutView.findViewById(R.id.etxt_add_edit_number_decimal_value)).setText(nm.format(Double.parseDouble(value.toString())));
                                    break;
                                case FLOAT:
                                    ((TextView) layoutView.findViewById(R.id.etxt_add_edit_number_decimal_value)).setText(nm.format(Float.parseFloat(value.toString())));
                                    break;
                                default:
                                    break;
                            }
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "Có lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show();
                        }


                    }
                case TEXT:
                    layoutView.findViewById(R.id.llayout_add_feature_number_decimal).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_spinner).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_edittext).setVisibility(View.VISIBLE);
                    layoutView.findViewById(R.id.llayout_add_feature_number).setVisibility(View.GONE);
                    ((TextView) layoutView.findViewById(R.id.txt_add_edit_text_title)).setText(field.getAlias());
                    if (value != null) {
                        try {
                            ((TextView) layoutView.findViewById(R.id.edit_add_edittext_value)).setText(value.toString());
                        } catch (Exception e) {
                            Toast.makeText(mActivity, "Có lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show();
                        }

                    }
                    break;

                default:
                    layoutView.findViewById(R.id.llayout_add_feature_number_decimal).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_spinner).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_edittext).setVisibility(View.GONE);
                    layoutView.findViewById(R.id.llayout_add_feature_number).setVisibility(View.GONE);
                    break;
            }
        }
        return layoutView;


    }
}
