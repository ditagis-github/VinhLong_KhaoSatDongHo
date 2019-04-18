package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.capnhatdongho.libs.Action;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class RefreshVatTuAsync extends AsyncTask<String, List<VatTuApdapter.VatTu>, Void> {
    private ProgressDialog dialog;
    private Context mContext;
    private ServiceFeatureTable vatTuTable;
    private VatTuApdapter vatTuApdapter;
    private Action action;

    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate;

    public RefreshVatTuAsync(Context context, ServiceFeatureTable vatTuTable, VatTuApdapter vatTuApdapter, Action action, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = context;
        this.vatTuTable = vatTuTable;
        this.vatTuApdapter = vatTuApdapter;
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        this.action = action;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<Feature> features = new ArrayList<>();
        final List<VatTuApdapter.VatTu> vatTus = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = Constant.VatTuFields.DBDongHo + " = '" + params[0] + "'";
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = vatTuTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(() -> {
            try {
                FeatureQueryResult result = queryResultListenableFuture.get();
                Iterator iterator = result.iterator();

                while (iterator.hasNext()) {
                    Feature feature = (Feature) iterator.next();
                    features.add(feature);
                    feature.getAttributes().get("OBJECTID");
                    VatTuApdapter.VatTu vatTu = new VatTuApdapter.VatTu();
                    vatTu.setOBJECTID(feature.getAttributes().get("OBJECTID").toString());
                    vatTu.setdBDongHoNuoc(getValueAttributes(feature, Constant.VatTuFields.DBDongHo));
                    vatTu.setTenMau(getValueAttributes(feature, Constant.VatTuFields.MaKhachHang));
                    vatTu.setView(action.isView());
                    vatTus.add(vatTu);
                }
                delegate.processFinish(features);
                publishProgress(vatTus);

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    private String getValueAttributes(Feature feature, String fieldName) {
        if (feature.getAttributes().get(fieldName) != null)
            return feature.getAttributes().get(fieldName).toString();
        return null;
    }


    @Override
    protected void onProgressUpdate(List<VatTuApdapter.VatTu>... values) {
        vatTuApdapter.clear();
        vatTuApdapter.setVatTus(values[0]);
        vatTuApdapter.notifyDataSetChanged();
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

