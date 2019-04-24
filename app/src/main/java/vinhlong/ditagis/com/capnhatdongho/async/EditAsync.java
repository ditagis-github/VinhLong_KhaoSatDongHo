package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.adapter.FeatureViewMoreInfoAdapter;
import vinhlong.ditagis.com.capnhatdongho.entities.DApplication;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;
import vinhlong.ditagis.com.capnhatdongho.utities.MySnackBar;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditAsync extends AsyncTask<FeatureViewMoreInfoAdapter, Void, Void> {
    private ProgressDialog dialog;
    private MainActivity mainActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private MapView mapView;
    private DApplication dApplication;

    public EditAsync(MapView mapView, MainActivity mainActivity, ServiceFeatureTable serviceFeatureTable, ArcGISFeature selectedArcGISFeature) {
        this.mainActivity = mainActivity;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
        dialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mapView = mapView;
        this.dApplication = (DApplication) mainActivity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(mainActivity.getString(R.string.async_dang_xu_ly));
        dialog.setCancelable(false);

        dialog.show();

    }

    @Override
    protected Void doInBackground(FeatureViewMoreInfoAdapter... params) {
        FeatureViewMoreInfoAdapter adapter = params[0];
        for (FeatureViewMoreInfoAdapter.Item item : adapter.getItems()) {
            if (item.getValue() == null) continue;
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
        Calendar currentTime = Calendar.getInstance();
        mSelectedArcGISFeature.getAttributes().put(Constant.DongHoKhachHangFields.NgayCapNhat, currentTime);
        mSelectedArcGISFeature.getAttributes().put(Constant.DongHoKhachHangFields.NguoiCapNhat, this.dApplication.getUser().getUserName());
        ListenableFuture<Void> voidListenableFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
        voidListenableFuture.addDoneListener(() -> {
            try {
                voidListenableFuture.get();
                ListenableFuture<List<FeatureEditResult>> listListenableFuture = mServiceFeatureTable.applyEditsAsync();
                listListenableFuture.addDoneListener(() -> {
                    try {
                        listListenableFuture.get();
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
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
        MySnackBar.make(mapView, "Đã xảy ra lỗi", false);
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

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
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

