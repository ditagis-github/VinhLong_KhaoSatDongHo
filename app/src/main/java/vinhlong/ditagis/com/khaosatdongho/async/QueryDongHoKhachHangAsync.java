package vinhlong.ditagis.com.khaosatdongho.async;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.R;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryDongHoKhachHangAsync extends AsyncTask<String, List<Feature>, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private ServiceFeatureTable serviceFeatureTable;
    private TextView txtTongItem;


    public QueryDongHoKhachHangAsync(Activity congViecActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, AsyncResponse asyncResponse) {
        this.mDelegate = asyncResponse;
        mActivity = congViecActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.txtTongItem = txtTongItem;
    }

    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse mDelegate = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang lấy danh sách công việc...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();


    }

    @Override
    protected Void doInBackground(String... params) {


        final List<Feature> features = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = params[0];
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = queryResultListenableFuture.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature feature = (Feature) iterator.next();

                    features.add(feature);
                }
                publishProgress(features);

            } catch (InterruptedException e) {
                e.printStackTrace();
                publishProgress();
            } catch (ExecutionException e) {
                e.printStackTrace();
                publishProgress();
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(List<Feature>... values) {


        if (txtTongItem != null)
            txtTongItem.setText(mActivity.getString(R.string.nav_thong_ke_tong_diem) + values[0].size());
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        if (values == null || values.length == 0)
            mDelegate.processFinish(null);
        else
            mDelegate.processFinish(values[0]);
        super.onProgressUpdate(values);

    }


}

