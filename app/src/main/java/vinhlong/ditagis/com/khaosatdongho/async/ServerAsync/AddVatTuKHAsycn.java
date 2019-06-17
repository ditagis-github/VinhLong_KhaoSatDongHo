package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;

public class AddVatTuKHAsycn extends AsyncTask<ArrayList<VatTuApdapter.VatTu>, Void, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public AddVatTuKHAsycn(MainActivity activity) {
        this.mainActivity = activity;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mainActivity.getString(R.string.connect_message));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    private JSONObject getCred(VatTuApdapter.VatTu vatTu) {
        JSONObject cred = new JSONObject();
        try {
            if (vatTu.getMaVatTu() != null) {
                cred.put("MaVatTu", vatTu.getMaVatTu());
            }
            if (vatTu.getSoLuongVatTu() != null) {
                cred.put("SoLuong", vatTu.getSoLuong());
            }
            if (vatTu.getiDKhachHang() != null) {
                cred.put("IDKhachHang", vatTu.getiDKhachHang());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return cred;
    }

    @Override
    protected Void doInBackground(ArrayList<VatTuApdapter.VatTu>... params) {
        ArrayList<VatTuApdapter.VatTu> vatTus = params[0];
        if (vatTus != null && vatTus.size()> 0) {
            try {
                URL url = new URL(Constant.API_URL.INSERT_VATTU);
                for(VatTuApdapter.VatTu vatTu:vatTus) {
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    try {
                        conn.setRequestMethod(Constant.METHOD.POST);
                        conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mainActivity.getString(R.string.preference_login_api)));
                        conn.setRequestProperty("Content-Type", "application/json");
                        OutputStream outputStream = conn.getOutputStream();
                        OutputStreamWriter wr = new OutputStreamWriter(outputStream);
                        JSONObject cred = getCred(vatTu);
                        wr.write(cred.toString());
                        wr.flush();
                        conn.connect();
                        conn.getInputStream();
                        wr.close();
                        outputStream.close();
                    } finally {
                        conn.disconnect();
                    }
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(Void user) {
        mDialog.dismiss();
    }
}