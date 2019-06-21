package vinhlong.ditagis.com.khaosatdongho.async;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
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
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
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

public class EditAsync extends AsyncTask<FeatureViewMoreInfoAdapter, Boolean, Void> {
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
    protected Void doInBackground(FeatureViewMoreInfoAdapter... params) {
        if (params != null && params.length > 0) {
            FeatureViewMoreInfoAdapter adapter = params[0];
            for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
                if (item.getValue() == null || !item.isEdit()) continue;
                Domain domain = mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain();
                Object codeDomain = null;
                if (domain != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) this.mSelectedArcGISFeature.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                    codeDomain = getCodeDomain(codedValues, item.getValue());
                }
                if (item.getFieldName().equals(mSelectedArcGISFeature.getFeatureTable().getTypeIdField())) {
                    List<FeatureType> featureTypes = mSelectedArcGISFeature.getFeatureTable().getFeatureTypes();
                    Object idFeatureTypes = getIdFeatureTypes(featureTypes, item.getValue());
                    mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(idFeatureTypes.toString()));

                } else switch (item.getFieldType()) {
                    case DATE:
                        Date date = null;
                        try {
                            date = Constant.DATE_FORMAT.parse(item.getValue());
                            Calendar c = Calendar.getInstance();
                            c.setTime(date);
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), c);
                        } catch (ParseException e) {
                        }
                        break;

                    case TEXT:
                        if (codeDomain != null) {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), codeDomain.toString());
                        } else
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), item.getValue());
                        break;
                    case SHORT:
                        if (codeDomain != null) {
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(codeDomain.toString()));
                        } else
                            mSelectedArcGISFeature.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                        break;
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
                        listListenableFuture.get();
                        publishProgress(true);
                    } catch (InterruptedException | ExecutionException e) {
                        notifyError();
                        e.printStackTrace();
                    }

                });
            } catch (InterruptedException | ExecutionException e) {
                notifyError();
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
        } else mDelegate.processFinish(false);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

