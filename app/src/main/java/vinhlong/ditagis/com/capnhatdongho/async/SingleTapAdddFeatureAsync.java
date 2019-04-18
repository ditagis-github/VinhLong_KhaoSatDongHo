package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Build;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;
import vinhlong.ditagis.com.capnhatdongho.utities.MySnackBar;

public class SingleTapAdddFeatureAsync extends AsyncTask<Point, Void, Void> {
    private ProgressDialog mDialog;
    private ServiceFeatureTable dongHoKHSFT;
    private MapView mapView;
    private LocatorTask locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");


    public SingleTapAdddFeatureAsync(MainActivity mainActivity, MapView mapView, ServiceFeatureTable hongHoKHSFT) {
        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.dongHoKHSFT = hongHoKHSFT;
        this.mapView = mapView;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(Point... params) {
        final Point clickPoint = params[0];
        Feature feature = this.dongHoKHSFT.createFeature();
        feature.setGeometry(clickPoint);
        final ListenableFuture<List<GeocodeResult>> listListenableFuture = locatorTask.reverseGeocodeAsync(clickPoint);
        listListenableFuture.addDoneListener(() -> {
            try {
                List<GeocodeResult> geocodeResults = listListenableFuture.get();
                if (geocodeResults.size() > 0) {
                    GeocodeResult geocodeResult = geocodeResults.get(0);
                    Map<String, Object> attrs = new HashMap<>();
                    for (String key : geocodeResult.getAttributes().keySet()) {
                        attrs.put(key, geocodeResult.getAttributes().get(key));
                    }
                    String address = geocodeResult.getAttributes().get("LongLabel").toString();
                    feature.getAttributes().put(Constant.DongHoKhachHangFields.GhiChu, address);
                }
                addFeatureAsync(feature);
            } catch (InterruptedException | ExecutionException e) {
                notifyError();
                e.printStackTrace();
            }
        });
        return null;
    }

    private void notifyError() {
        MySnackBar.make(mapView, "Đã xảy ra lỗi", false);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }

    }

    private void addFeatureAsync(Feature feature) {
        ListenableFuture<Void> voidListenableFuture = this.dongHoKHSFT.addFeatureAsync(feature);
        voidListenableFuture.addDoneListener(() -> {
            try {
                voidListenableFuture.get();
                ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = this.dongHoKHSFT.applyEditsAsync();
                listListenableEditAsync.addDoneListener(() -> {
                    try {
                        List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                        if (featureEditResults.size() > 0) {
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        notifyError();
                        e.printStackTrace();
                    }

                });
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

        });
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
