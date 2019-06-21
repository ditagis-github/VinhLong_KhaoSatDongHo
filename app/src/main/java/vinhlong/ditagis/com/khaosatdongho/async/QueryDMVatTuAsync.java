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
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.R;


/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryDMVatTuAsync extends AsyncTask<Void, ArrayList<Feature>, Void> {
    public interface AsyncResponse {
        void processFinish(ArrayList<Feature> output);
    }

    public AsyncResponse delegate = null;

    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    public  ArrayList<Feature> features;

    public QueryDMVatTuAsync(Activity activity, ServiceFeatureTable serviceFeatureTable, AsyncResponse delegate) {
        mActivity = activity;
        mServiceFeatureTable = serviceFeatureTable;
        this.delegate = delegate;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "1=1";
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> featureQueryResultListenableFuture = mServiceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        featureQueryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = featureQueryResultListenableFuture.get();
                Iterator<Feature> iterator = result.iterator();
                features = new ArrayList<>();
                while (iterator.hasNext()) {
                    Feature feature = iterator.next();
                    features.add(feature);
                }
                mDialog.dismiss();
                publishProgress(features);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang tải dữ liệu...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }


    @Override
    protected void onProgressUpdate(ArrayList... values) {
        super.onProgressUpdate(values);
        delegate.processFinish(values[0]);

    }
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}


