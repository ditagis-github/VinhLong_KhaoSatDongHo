package vinhlong.ditagis.com.khaosatdongho.async;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Point;

import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.R;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class EditGeometryAsync extends AsyncTask<Point, Boolean, Void> {
    private ProgressDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Context mContext;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature;

    private AsyncResponse mDelegate;

    public EditGeometryAsync(Context context, ServiceFeatureTable serviceFeatureTable,
                             ArcGISFeature selectedArcGISFeature, AsyncResponse delegate) {
        mContext = context;
        this.mDelegate = delegate;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
        mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        mDialog.setCancelable(false);

        mDialog.show();

    }

    @Override
    protected Void doInBackground(Point... params) {
        if (params != null && params.length > 0) {
            mSelectedArcGISFeature.setGeometry(params[0]);
            final ListenableFuture<Void> updateFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
            updateFuture.addDoneListener(() -> {
                try {
                    // track the update
                    updateFuture.get();
                    // apply edits once the update has completed
                    if (updateFuture.isDone()) {
                        applyEditsToServer();
                    } else {
                        publishProgress();
                    }
                } catch (InterruptedException | ExecutionException e1) {
                    publishProgress();
                }
            });
        } else publishProgress();
        return null;
    }

    private void applyEditsToServer() {
        final ListenableFuture<List<FeatureEditResult>> applyEditsFuture = ((ServiceFeatureTable) mSelectedArcGISFeature
                .getFeatureTable()).applyEditsAsync();
        applyEditsFuture.addDoneListener(() -> {
            try {
                // get results of edit
                List<FeatureEditResult> featureEditResultsList = applyEditsFuture.get();
                if (!featureEditResultsList.get(0).hasCompletedWithErrors()) {
                    publishProgress(true);
                } else {
                    publishProgress();
                }
            } catch (InterruptedException | ExecutionException e) {
                publishProgress();
            }
        });
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();

        }
        if (values != null && values.length > 0)
            mDelegate.processFinish(values[0]);
        else mDelegate.processFinish(null);
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

    public interface AsyncResponse {
        void processFinish(Boolean feature);
    }

}

