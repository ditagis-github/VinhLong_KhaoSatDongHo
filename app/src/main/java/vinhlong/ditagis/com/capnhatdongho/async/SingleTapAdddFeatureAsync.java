package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.tasks.geocode.GeocodeResult;
import com.esri.arcgisruntime.tasks.geocode.LocatorTask;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.entities.DApplication;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;
import vinhlong.ditagis.com.capnhatdongho.utities.MySnackBar;

public class SingleTapAdddFeatureAsync extends AsyncTask<Point, ArcGISFeature, Void> {
    private ProgressDialog mDialog;
    private ServiceFeatureTable dongHoKHSFT;
    private MapView mapView;
    private LocatorTask locatorTask = new LocatorTask("http://geocode.arcgis.com/arcgis/rest/services/World/GeocodeServer");
    private DApplication dApplication;
    private SingleTapAdddFeatureAsync.AsyncResponse delegate;
    private MainActivity mainActivity;

    public interface AsyncResponse {
        void processFinish(ArcGISFeature features);
    }

    public SingleTapAdddFeatureAsync(MainActivity mainActivity, MapView mapView, ServiceFeatureTable hongHoKHSFT, SingleTapAdddFeatureAsync.AsyncResponse asyncResponse) {
        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.dongHoKHSFT = hongHoKHSFT;
        this.mapView = mapView;
        this.dApplication = (DApplication) mainActivity.getApplication();
        this.delegate = asyncResponse;
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mainActivity.getString(R.string.PROCESSING));
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
        publishProgress();
        MySnackBar.make(mapView,mainActivity.getString(R.string.ERROR_OCCURRED) , false);
    }

    private void addFeatureAsync(Feature featureAdd) {
        Calendar currentTime = Calendar.getInstance();
        featureAdd.getAttributes().put(Constant.DongHoKhachHangFields.NgayCapNhat, currentTime);
        featureAdd.getAttributes().put(Constant.DongHoKhachHangFields.NguoiCapNhat, this.dApplication.getUser().getUserName());

        ListenableFuture<Void> voidListenableFuture = this.dongHoKHSFT.addFeatureAsync(featureAdd);
        voidListenableFuture.addDoneListener(() -> {
            try {
                voidListenableFuture.get();
                ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = this.dongHoKHSFT.applyEditsAsync();
                listListenableEditAsync.addDoneListener(() -> {
                    try {
                        List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                        if (featureEditResults.size() > 0) {
                            long objectId = featureEditResults.get(0).getObjectId();
                            final QueryParameters queryParameters = new QueryParameters();
                            final String query = "OBJECTID = " + objectId;
                            queryParameters.setWhereClause(query);
                            final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = this.dongHoKHSFT.queryFeaturesAsync(queryParameters);
                            queryResultListenableFuture.addDoneListener(() ->{
                                try {
                                    FeatureQueryResult features = queryResultListenableFuture.get();
                                    Iterator<Feature> iterator = features.iterator();
                                    if (iterator.hasNext()) {
                                        Feature feature = iterator.next();
                                        publishProgress((ArcGISFeature) feature);
                                        delegate.processFinish((ArcGISFeature) feature);
                                    }
                                } catch (InterruptedException | ExecutionException e) {
                                    notifyError();
                                    e.printStackTrace();
                                }
                            });
                        } else publishProgress();
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
    protected void onProgressUpdate(ArcGISFeature... values) {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        mainActivity.addFeatureClose();
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}
