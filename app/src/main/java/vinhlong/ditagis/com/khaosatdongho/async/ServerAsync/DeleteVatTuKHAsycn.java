package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;

public class DeleteVatTuKHAsycn extends AsyncTask<String, Void, Boolean> {
    private ProgressDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    public DeleteVatTuKHAsycn(Activity activity,AsyncResponse delegate) {
        this.mContext = activity;
        this.mDelegate = delegate;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mContext, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mContext.getString(R.string.connect_message));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected Boolean doInBackground(String... params) {
        if (params != null && params.length > 0) {
            try {
                String idKhachHang = params[0];
                String urlAPI = String.format(Constant.API_URL.DELETE_VATTUS, idKhachHang);
                URL url = new URL(urlAPI);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                try {
                    conn.setDoOutput(true);
                    conn.setRequestMethod(Constant.METHOD.DELETE);
                    conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                    conn.connect();
                    conn.getInputStream();
                } finally {
                    conn.disconnect();
                    mDialog.dismiss();
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
            }
        }
        return true;
    }


    @Override
    protected void onPostExecute(Boolean rs) {
        this.mDelegate.processFinish(rs);
        mDialog.dismiss();
    }

}