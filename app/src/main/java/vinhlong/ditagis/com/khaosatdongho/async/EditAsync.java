package vinhlong.ditagis.com.khaosatdongho.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewMoreInfoAdapter;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.MySnackBar;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditAsync extends AsyncTask<HashMap<String, Object>, Boolean, Void> {
    private BottomSheetDialog mDialog;
    private Activity mainActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private View mapView;
    private DApplication dApplication;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Boolean feature);
    }

    public EditAsync(View mapView, Activity mainActivity, ServiceFeatureTable serviceFeatureTable
            , ArcGISFeature selectedArcGISFeature, AsyncResponse delegate) {
        this.mainActivity = mainActivity;
        this.mDelegate = delegate;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
        this.mapView = mapView;
        this.dApplication = (DApplication) mainActivity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mainActivity);
        LinearLayout view = (LinearLayout) mainActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang cập nhật thông tin...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();

    }

    @Override
    protected Void doInBackground(HashMap<String, Object>... params) {
        if (params != null && params.length > 0) {
            HashMap<String, Object> attributes = params[0];
            for (String alias : attributes.keySet()) {
                for (Field field : mServiceFeatureTable.getFields()) {
                    if (field.getAlias().equals(alias)) {
                        try {
                            Object value = attributes.get(alias);
                            if (value == null)
                                mSelectedArcGISFeature.getAttributes().put(field.getName(), null);
                            else {
                                String valueString = value.toString().trim();

                                switch (field.getFieldType()) {
                                    case TEXT:
                                        mSelectedArcGISFeature.getAttributes().put(field.getName(), valueString);
                                        break;
                                    case DOUBLE:
                                        mSelectedArcGISFeature.getAttributes().put(field.getName(), java.lang.Double.parseDouble(valueString));
                                    case FLOAT:
                                        mSelectedArcGISFeature.getAttributes().put(field.getName(), java.lang.Float.parseFloat(valueString));
                                    case INTEGER:
                                        mSelectedArcGISFeature.getAttributes().put(field.getName(), Integer.parseInt(valueString));
                                    case SHORT:
                                        mSelectedArcGISFeature.getAttributes().put(field.getName(), java.lang.Short.parseShort(valueString));
                                        break;
                                    default:
                                        break;
                                }
                            }

                        } catch (Exception e) {
                            mSelectedArcGISFeature.getAttributes().put(field.getName(), null);
                            Log.e("Lỗi thêm điểm", e.toString());
                        }

                        break;
                    }
                }
            }
        }
        Calendar currentTime = Calendar.getInstance();
        mSelectedArcGISFeature.getAttributes().put(Constant.DongHoKhachHangFields.NGAY_CAP_NHAT, currentTime);
        mSelectedArcGISFeature.getAttributes().put(Constant.DongHoKhachHangFields.NGUOI_CAP_NHAT, this.dApplication.getUser().getUserName());
        ListenableFuture<Void> voidListenableFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
        voidListenableFuture.addDoneListener(() -> {
            try {
                voidListenableFuture.get();
                ListenableFuture<List<FeatureEditResult>> listListenableFuture = mServiceFeatureTable.applyEditsAsync();
                listListenableFuture.addDoneListener(() -> {
                    try {
                        List<FeatureEditResult> featureEditResults = listListenableFuture.get();
                        if (featureEditResults.size() > 0) {
                            if (!featureEditResults.get(0).hasCompletedWithErrors()) {
                                publishProgress(true);
                            } else {
                                publishProgress();
                            }
                        } else {
                            publishProgress();
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        publishProgress();
                        e.printStackTrace();
                    }

                });
            } catch (InterruptedException | ExecutionException e) {
                publishProgress();
                e.printStackTrace();
            }
        });
        return null;
    }
    private void notifyError() {
        publishProgress();
        MySnackBar.make(mapView, "Đã xảy ra lỗi", false);
    }
    private Object getIdFeatureTypes(List<FeatureType> featureTypes, String value) {
        Object code = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getName().equals(value)) {
                code = featureType.getId();
                break;
            }
        }
        return code;
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

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (values != null && values[0]) {
            mDelegate.processFinish(true);
        } else {
            notifyError();
            mDelegate.processFinish(false);
        }
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

