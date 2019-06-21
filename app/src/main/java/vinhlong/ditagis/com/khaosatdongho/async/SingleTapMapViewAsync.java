package vinhlong.ditagis.com.khaosatdongho.async;

import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.GeoElement;
import com.esri.arcgisruntime.mapping.view.IdentifyLayerResult;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;

public class SingleTapMapViewAsync extends AsyncTask<android.graphics.Point, ArcGISFeature, Void> {
    private BottomSheetDialog mDialog;
    private MainActivity mActivity;
    private MapView mapView;
    private DApplication dApplication;
    private FeatureLayer dongHoKHFL;
    private SingleTapMapViewAsync.AsyncResponse delegate = null;
    public interface AsyncResponse {
        void processFinish(ArcGISFeature features);
    }
    public SingleTapMapViewAsync(MainActivity mainActivity, MapView mapView, SingleTapMapViewAsync.AsyncResponse asyncResponse) {
        this.mActivity = mainActivity;
        this.mapView = mapView;
        this.dApplication = (DApplication) mainActivity.getApplication();
        this.dongHoKHFL = this.dApplication.getDongHoKHDTG().getFeatureLayer();
        this.delegate = asyncResponse;
    }



    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang tìm kiếm thông tin...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected Void doInBackground(android.graphics.Point... params) {
        android.graphics.Point clickPoint = params[0];
        final ListenableFuture<IdentifyLayerResult> identifyFuture = mapView.identifyLayerAsync(dongHoKHFL, clickPoint, 5, false, 1);
        identifyFuture.addDoneListener(() -> {
            try {
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
                publishProgress();
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(ArcGISFeature... values) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        super.onProgressUpdate(values);
    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}