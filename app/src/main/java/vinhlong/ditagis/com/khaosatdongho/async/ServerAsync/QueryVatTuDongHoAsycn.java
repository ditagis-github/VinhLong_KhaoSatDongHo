package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;


public class QueryVatTuDongHoAsycn extends AsyncTask<String, ArrayList<VatTuApdapter.VatTu>, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(ArrayList<VatTuApdapter.VatTu> output);
    }

    public QueryVatTuDongHoAsycn(MainActivity mainActivity, AsyncResponse delegate) {
        this.mainActivity = mainActivity;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mainActivity.getString(R.string.async_dang_lay_danh_sach_vat_tu));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            if (params != null && params.length > 0) {
                String maKhachHang = params[0];
                String urlAPI = String.format(Constant.API_URL.VATTUS_DONGHO, maKhachHang);
                URL url = new URL(urlAPI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    conn.setDoOutput(false);
                    conn.setRequestMethod(Constant.METHOD.GET);
                    conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mainActivity.getString(R.string.preference_login_api)));
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
                } finally {
                    conn.disconnect();
                    this.mDialog.dismiss();
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
                VatTuApdapter.VatTu vatTu = new VatTuApdapter.VatTu(i);
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Object maVatTu = jsonObject.get("MaVatTu");
                if (maVatTu != null) {
                    vatTu.setMaVatTu(maVatTu.toString());
                }
                Object soLuong = jsonObject.get("SoLuong");
                if (soLuong != null) {
                    vatTu.setSoLuongVatTu(soLuong.toString());
                }
                Object dDKhachHang = jsonObject.get("IDKhachHang");
                if (dDKhachHang != null) {
                    vatTu.setiDKhachHang(dDKhachHang.toString());
                }
                Object giaNC = jsonObject.get("GiaNC");
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
        this.mDelegate.processFinish(values[0]);

    }

    @Override
    protected void onPostExecute(Void value) {
    }

}
