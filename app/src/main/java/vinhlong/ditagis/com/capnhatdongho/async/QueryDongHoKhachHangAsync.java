package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.ThongKeActivity;
import vinhlong.ditagis.com.capnhatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class QueryDongHoKhachHangAsync extends AsyncTask<String, List<DanhSachDongHoKHAdapter.Item>, Void> {
    private ProgressDialog dialog;
    private Context mContext;
    private ServiceFeatureTable serviceFeatureTable;
    private DanhSachDongHoKHAdapter danhSachDongHoKHAdapter;
    private TextView txtTongItem;

    public QueryDongHoKhachHangAsync(ThongKeActivity thongKeActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, DanhSachDongHoKHAdapter adapter, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = thongKeActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.danhSachDongHoKHAdapter = adapter;
        this.txtTongItem = txtTongItem;
        dialog = new ProgressDialog(thongKeActivity, android.R.style.Theme_Material_Dialog_Alert);
    }

    public QueryDongHoKhachHangAsync(MainActivity mainActivity, ServiceFeatureTable serviceFeatureTable, TextView txtTongItem, DanhSachDongHoKHAdapter adapter, AsyncResponse asyncResponse) {
        this.delegate = asyncResponse;
        mContext = mainActivity;
        this.serviceFeatureTable = serviceFeatureTable;
        this.danhSachDongHoKHAdapter = adapter;
        this.txtTongItem = txtTongItem;
        dialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
    }

    public interface AsyncResponse {
        void processFinish(List<Feature> features);
    }

    private AsyncResponse delegate = null;


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.setMessage(mContext.getString(R.string.async_dang_xu_ly));
        dialog.setCancelable(false);
        dialog.show();

    }

    @Override
    protected Void doInBackground(String... params) {
        final List<DanhSachDongHoKHAdapter.Item> items = new ArrayList<>();
        final List<Feature> features = new ArrayList<>();
        QueryParameters queryParameters = new QueryParameters();
        String queryClause = params[0];
        queryParameters.setWhereClause(queryClause);
        final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        queryResultListenableFuture.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {
                    FeatureQueryResult result = queryResultListenableFuture.get();
                    Iterator iterator = result.iterator();

                    while (iterator.hasNext()) {
                        Feature feature = (Feature) iterator.next();
                        DanhSachDongHoKHAdapter.Item item = new DanhSachDongHoKHAdapter.Item();
                        Map<String, Object> attributes = feature.getAttributes();
                        item.setObjectID(attributes.get(mContext.getString(R.string.OBJECTID)).toString());
                        item.setDbDongHoNuoc(attributes.get(mContext.getString(R.string.IDDIEMDANHGIA)).toString());
                        String format_date = Constant.DATE_FORMAT.format(((Calendar) attributes.get(Constant.NGAY_CAP_NHAT)).getTime());
                        item.setMaKhachHang(format_date);
                        item.setTenThueBao(attributes.get(mContext.getString(R.string.DIACHI)).toString());
                        items.add(item);
                        features.add(feature);
                    }
                    delegate.processFinish(features);
                    publishProgress(items);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(List<DanhSachDongHoKHAdapter.Item>... values) {
        danhSachDongHoKHAdapter.clear();
        danhSachDongHoKHAdapter.setItems(values[0]);
        danhSachDongHoKHAdapter.notifyDataSetChanged();
        if (txtTongItem != null)
            txtTongItem.setText(mContext.getString(R.string.nav_thong_ke_tong_diem) + values[0].size());
        if (dialog != null && dialog.isShowing()) dialog.dismiss();
        super.onProgressUpdate(values);

    }
    @Override
    protected void onPostExecute(Void result) {
        if (dialog != null || dialog.isShowing()) dialog.dismiss();
        super.onPostExecute(result);

    }

}

