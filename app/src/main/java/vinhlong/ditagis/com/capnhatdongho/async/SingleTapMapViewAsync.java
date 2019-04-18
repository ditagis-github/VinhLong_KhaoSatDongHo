package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.entities.DApplication;
import vinhlong.ditagis.com.capnhatdongho.utities.Popup;

public class SingleTapMapViewAsync extends AsyncTask<android.graphics.Point, ArcGISFeature, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private MapView mapView;
    private DApplication dApplication;
    private FeatureLayer dongHoKHFL;
    private Popup popup;
    private SingleTapMapViewAsync.AsyncResponse delegate = null;
    public interface AsyncResponse {
        void processFinish(ArcGISFeature features);
    }
    public SingleTapMapViewAsync(MainActivity mainActivity, MapView mapView, Popup popup,SingleTapMapViewAsync.AsyncResponse asyncResponse) {
        this.mainActivity = mainActivity;
        this.mapView = mapView;
        this.mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.dApplication = (DApplication) mainActivity.getApplication();
        this.dongHoKHFL = this.dApplication.getDongHoKHDTG().getFeatureLayer();
        this.popup = popup;
        this.delegate = asyncResponse;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage("Đang xử lý...");
        mDialog.setCancelable(false);
        mDialog.show();
    }

    @Override
    protected Void doInBackground(android.graphics.Point... params) {
        android.graphics.Point clickPoint = params[0];
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(dongHoKHFL, clickPoint, 5, false, 1);
        identifyFuture.addDoneListener(() -> {
            try {
                if (mDialog != null && mDialog.isShowing()) {
                    mDialog.dismiss();
                }
                IdentifyLayerResult layerResult = identifyFuture.get();
                List<GeoElement> resultGeoElements = layerResult.getElements();
                if (resultGeoElements.size() > 0) {
                    if (resultGeoElements.get(0) instanceof ArcGISFeature) {
                        ArcGISFeature arcGISFeature = (ArcGISFeature) resultGeoElements.get(0);
                        delegate.processFinish(arcGISFeature);
                        publishProgress(arcGISFeature);
                    }
                } else {
                    publishProgress();
                }
            } catch (Exception e) {
                Log.e(mainActivity.getResources().getString(R.string.app_name), "Select feature failed: " + e.getMessage());
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(ArcGISFeature... values) {
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}