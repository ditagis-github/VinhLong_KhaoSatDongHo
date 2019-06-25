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
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.khaosatdongho.libs.Action;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class RefreshVatTuAsync extends AsyncTask<String, List<VatTuApdapter.VatTu>, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private ServiceFeatureTable vatTuTable;
    private VatTuApdapter vatTuApdapter;
    private Action action;
    private ArrayList<Feature> dmVatTuFeatures;

    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate;

    public RefreshVatTuAsync(Activity activity, ServiceFeatureTable vatTuTable, ArrayList<Feature> features, VatTuApdapter vatTuApdapter, Action action, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mActivity = activity;
        this.vatTuTable = vatTuTable;
        this.vatTuApdapter = vatTuApdapter;
        this.action = action;
        this.dmVatTuFeatures = features;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang xử lý...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<Feature> features = new ArrayList<>();
        final List<VatTuApdapter.VatTu> vatTus = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = Constant.VatTuFields.MaKhachHang + " = '" + params[0] + "'";
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
                    vatTu.setMaVatTu(objectID.toString());
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
                                vatTu.setGiaNC(giaVatTu.toString());
                            if (tenVatTu != null)
                                vatTu.setTenVatTu(tenVatTu.toString());
                        }
                    }
                    vatTus.add(vatTu);
                    features.add(feature);
                }
                publishProgress(vatTus);
                delegate.processFinish(features);
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

    @Override
    protected void onProgressUpdate(List<VatTuApdapter.VatTu>... values) {
        vatTuApdapter.clear();
//        vatTuApdapter.setVatTus(values[0]);
        vatTuApdapter.notifyDataSetChanged();
        if (mDialog != null && mDialog.isShowing()) mDialog.dismiss();
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

