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


public class QueryDanhSachVatTuAsycn extends AsyncTask<Void, ArrayList<QueryDanhSachVatTuAsycn.VatTu>, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(ArrayList<VatTu> output);
    }

    public QueryDanhSachVatTuAsycn(Activity activity, AsyncResponse delegate) {
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
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(Constant.API_URL.VATTU_LIST);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod(Constant.METHOD.GET);
//                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
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
                publishProgress();
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
                Object tenMau = jsonObject.get("TenVatTu");
                if (tenMau != null) {
                    vatTu.setTenVatTu(tenMau.toString());
                }
                Object donViTinh = jsonObject.get("DonViTinh");
                if (donViTinh != null) {
                    vatTu.setDonViTinh(donViTinh.toString());
                }
                Object vt = jsonObject.get("VT");
                if (vt != null) {
                    vatTu.setvT(vt.toString());
                }
                Object nc = jsonObject.get("NC");
                if (nc != null) {
                    vatTu.setnC(nc.toString());
                }
                Object mtc = jsonObject.get("MTC");
                if (mtc != null) {
                    vatTu.setmTC(mtc.toString());
                }
                Object maHSDG = jsonObject.get("MaHSDG");
                if (maHSDG != null) {
                    vatTu.setMaHSDG(maHSDG.toString());
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
        else
            this.mDelegate.processFinish(values[0]);

    }

    @Override
    protected void onPostExecute(Void value) {
    }

    public static class VatTu {
        private String maVatTu;
        private String tenVatTu;
        private String donViTinh;
        private String vT;
        private String nC;
        private String mTC;
        private String maHSDG;

        public VatTu() {
        }

        public VatTu(String maVatTu, String tenVatTu, String donViTinh, String vT, String nC, String mTC, String maHSDG) {
            this.donViTinh = donViTinh;
            this.maVatTu = maVatTu;
            this.tenVatTu = tenVatTu;
            this.vT = vT;
            this.nC = nC;
            this.mTC = mTC;
            this.maHSDG = maHSDG;
        }

        public String getMaVatTu() {
            return maVatTu;
        }

        public void setMaVatTu(String maVatTu) {
            this.maVatTu = maVatTu;
        }

        public String getTenVatTu() {
            return tenVatTu;
        }

        public void setTenVatTu(String tenVatTu) {
            this.tenVatTu = tenVatTu;
        }

        public String getDonViTinh() {
            return donViTinh;
        }

        public void setDonViTinh(String donViTinh) {
            this.donViTinh = donViTinh;
        }

        public String getvT() {
            return vT;
        }

        public void setvT(String vT) {
            this.vT = vT;
        }

        public String getnC() {
            return nC;
        }

        public void setnC(String nC) {
            this.nC = nC;
        }

        public String getmTC() {
            return mTC;
        }

        public void setmTC(String mTC) {
            this.mTC = mTC;
        }

        public String getMaHSDG() {
            return maHSDG;
        }

        public void setMaHSDG(String maHSDG) {
            this.maHSDG = maHSDG;
        }
    }
}
