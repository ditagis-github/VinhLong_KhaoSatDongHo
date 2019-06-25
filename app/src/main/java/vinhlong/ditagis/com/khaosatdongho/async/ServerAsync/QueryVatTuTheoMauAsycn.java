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
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;


public class QueryVatTuTheoMauAsycn extends AsyncTask<String, ArrayList<QueryVatTuTheoMauAsycn.VatTu>, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(ArrayList<VatTu> output);
    }

    public QueryVatTuTheoMauAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang lấy danh sách tên mẫu...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected Void doInBackground(String... params) {
        try {
            if (params != null && params.length > 0) {
                String tenThietLapMau = params[0];
                String urlAPI = String.format(Constant.API_URL.VATTU_THEOMAU, tenThietLapMau);
                URL url = new URL(urlAPI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    conn.setDoOutput(false);
                    conn.setRequestMethod(Constant.METHOD.GET);
                    conn.connect();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer buffer = new StringBuffer();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        buffer.append(line);
                    }
                    ArrayList<VatTu> vatTus = getVatTus(buffer.toString());
                    publishProgress(vatTus);
                    this.mDialog.dismiss();
                } catch (Exception e) {
                    Log.e("error", e.toString());
                } finally {
                    conn.disconnect();
                }
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        return null;
    }
    private ArrayList<VatTu> getVatTus(String data) throws JSONException {
        ArrayList<VatTu> vatTus = new ArrayList<>();
        if (data != null) {
            JSONObject jsonData = new JSONObject(data);
            JSONArray jsonArray = jsonData.getJSONArray("value");
            for (int i = 0; i < jsonArray.length(); i++) {
                VatTu vatTu = new VatTu();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Object maVatTu = jsonObject.get("MaVatTu");
                if (maVatTu != null) {
                    vatTu.setMaVatTu(maVatTu.toString());
                }
                Object tenMau = jsonObject.get("TENMAU");
                if (tenMau != null) {
                    vatTu.setTenMau(tenMau.toString());
                }
                Object soLuong = jsonObject.get("SoLuong");
                if (soLuong != null) {
                    vatTu.setSoLuong(soLuong.toString());
                }
                Object id = jsonObject.get("ID");
                if (id != null) {
                    vatTu.setId(id.toString());
                }
                vatTus.add(vatTu);
            }
        }
        return vatTus;
    }
    @Override
    protected void onProgressUpdate(ArrayList<VatTu>... values) {
        super.onProgressUpdate(values);
        if (values == null || values.length == 0)
            this.mDelegate.processFinish(null);
        this.mDelegate.processFinish(values[0]);

    }

    @Override
    protected void onPostExecute(Void value) {
    }
    public class VatTu{
        private String maVatTu;
        private String tenMau;
        private String soLuong;
        private String id;

        public VatTu() {
        }

        public VatTu(String maVatTu, String tenMau, String soLuong, String id) {
            this.maVatTu = maVatTu;
            this.tenMau = tenMau;
            this.soLuong = soLuong;
            this.id = id;
        }

        public String getMaVatTu() {
            return maVatTu;
        }

        public void setMaVatTu(String maVatTu) {
            this.maVatTu = maVatTu;
        }

        public String getTenMau() {
            return tenMau;
        }

        public void setTenMau(String tenMau) {
            this.tenMau = tenMau;
        }

        public String getSoLuong() {
            return soLuong;
        }

        public void setSoLuong(String soLuong) {
            this.soLuong = soLuong;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
