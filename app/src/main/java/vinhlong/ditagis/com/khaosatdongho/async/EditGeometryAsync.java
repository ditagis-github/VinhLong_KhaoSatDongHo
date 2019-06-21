package vinhlong.ditagis.com.khaosatdongho.async;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private BottomSheetDialog mDialog;
    @SuppressLint("StaticFieldLeak")
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature;

    private AsyncResponse mDelegate;

    public EditGeometryAsync(Activity activity, ServiceFeatureTable serviceFeatureTable,
                             ArcGISFeature selectedArcGISFeature, AsyncResponse delegate) {
        mActivity = activity;
        this.mDelegate = delegate;
        mServiceFeatureTable = serviceFeatureTable;
        mSelectedArcGISFeature = selectedArcGISFeature;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang cập nhật vị trí...");
        mDialog.setContentView(view);
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

