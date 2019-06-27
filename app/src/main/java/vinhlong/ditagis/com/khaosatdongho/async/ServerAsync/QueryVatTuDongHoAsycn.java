package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;


public class QueryVatTuDongHoAsycn extends AsyncTask<Long, ArrayList<VatTuApdapter.VatTu>, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(ArrayList<VatTuApdapter.VatTu> output);
    }

    public QueryVatTuDongHoAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang lấy danh sách vật tư...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected Void doInBackground(Long... params) {
        try {
            if (params != null && params.length > 0) {
                long maKhachHang = params[0];
                String urlAPI = String.format(Constant.API_URL.VATTUS_DONGHO, maKhachHang);
                URL url = new URL(urlAPI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    conn.setDoOutput(false);
                    conn.setRequestMethod(Constant.METHOD.GET);
                    conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mActivity.getString(R.string.preference_login_api)));
                    conn.connect();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    ArrayList<VatTuApdapter.VatTu> vatTus = getVatTus(buffer.toString());
                    publishProgress(vatTus);
                } catch (Exception e) {
                    Log.e("error", e.toString());
                    publishProgress();
                } finally {
                    conn.disconnect();

                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        return null;
    }

    private ArrayList<VatTuApdapter.VatTu> getVatTus(String data) throws JSONException {
        ArrayList<VatTuApdapter.VatTu> vatTus = new ArrayList<>();
        if (data != null) {
            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonArray = jsonData.getJSONArray("value");
            for (int i = 0; i < jsonArray.length(); i++) {
                VatTuApdapter.VatTu vatTu = new VatTuApdapter.VatTu(i + 1);
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Object maVatTu = jsonObject.get(Constant.VatTuFields.MaVatTu);
                if (maVatTu != null) {
                    vatTu.setMaVatTu(maVatTu.toString());
                }
                Object soLuong = jsonObject.get(Constant.VatTuFields.SoLuong);
                if (soLuong != null) {
                    vatTu.setSoLuongVatTu((Double) soLuong);
                }
                Object dDKhachHang = jsonObject.get(Constant.VatTuFields.ID);
                if (dDKhachHang != null) {
                    vatTu.setiDKhachHang(((Integer) dDKhachHang).longValue());
                }
                Object giaNC = jsonObject.get(Constant.VatTuFields.GIA_NC);
                if (giaNC != null) {
                    vatTu.setGiaNC(giaNC.toString());
                }
                vatTus.add(vatTu);
            }
        }
        return vatTus;
    }

    @Override
    protected void onProgressUpdate(ArrayList<VatTuApdapter.VatTu>... values) {
        super.onProgressUpdate(values);
        this.mDialog.dismiss();
        if (values == null || values.length == 0)
            this.mDelegate.processFinish(null);
        else
            this.mDelegate.processFinish(values[0]);

    }

    @Override
    protected void onPostExecute(Void value) {
    }

}
