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
import java.util.Map;
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
    private ArrayList<Feature> dmVatTuFeatures;

    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate;

    public RefreshVatTuAsync(Context context, ServiceFeatureTable vatTuTable, ArrayList<Feature> features, VatTuApdapter vatTuApdapter, Action action, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = context;
        this.vatTuTable = vatTuTable;
        this.vatTuApdapter = vatTuApdapter;
        dialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
        this.action = action;
        this.dmVatTuFeatures = features;
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
                    VatTuApdapter.VatTu vatTu = new VatTuApdapter.VatTu();
                    Object maVatTu = feature.getAttributes().get(Constant.VatTuFields.MaVatTu);
                    Object soLuong = feature.getAttributes().get(Constant.VatTuFields.SoLuong);
                    Object objectID = feature.getAttributes().get(Constant.LayerFields.OBJECTID);
                    vatTu.setObjectID(objectID.toString());
                    if (soLuong != null) {
                        vatTu.setSoLuongVatTu(soLuong.toString());
                    }
                    if (maVatTu != null) {
                        Feature loaiVatTu = getLoaiVatTu(maVatTu.toString());
                        if (loaiVatTu != null) {
                            Object donViTinh = loaiVatTu.getAttributes().get(Constant.LoaiVatTuFields.DonViTinh);
                            Object giaVatTu = loaiVatTu.getAttributes().get(Constant.LoaiVatTuFields.GiaVatTu);
                            Object tenVatTu = loaiVatTu.getAttributes().get(Constant.LoaiVatTuFields.TenVatTu);
                            if (donViTinh != null)
                                vatTu.setDonViTinh(donViTinh.toString());
                            if (giaVatTu != null)
                                vatTu.setDonGiaVatTu(giaVatTu.toString());
                            if (tenVatTu != null)
                                vatTu.setTenVatTu(tenVatTu.toString());
                        }
                    }
                    vatTu.setView(action.isView());
                    vatTus.add(vatTu);
                    features.add(feature);
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

    private Feature getLoaiVatTu(String maVatTu) {
        if (maVatTu != null) {
            for (Feature feature : this.dmVatTuFeatures) {
                Map<String, Object> attributes = feature.getAttributes();
                Object maVT = attributes.get(Constant.VatTuFields.MaVatTu);
                if (maVT != null && maVT.toString().equals(maVatTu)) {
                    return feature;
                }
            }
        }
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

