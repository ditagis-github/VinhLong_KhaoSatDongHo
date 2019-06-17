package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.ProgressDialog;
import android.content.Context;
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
import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.ListObjectDB;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;


public class QueryTenMauAsycn extends AsyncTask<Void, String, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public QueryTenMauAsycn(MainActivity mainActivity, AsyncResponse delegate) {
        this.mainActivity = mainActivity;
        this.mDelegate = delegate;
        DApplication dApplication = (DApplication) mainActivity.getApplication();

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        this.mDialog = new ProgressDialog(this.mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mDialog.setMessage(mainActivity.getString(R.string.async_dang_lay_danh_sach_ten_mau));
        this.mDialog.setCancelable(false);
        this.mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(Constant.API_URL.TENMAU_LIST);
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
                publishProgress(buffer.toString());
                this.mDialog.dismiss();
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.toString());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        this.mDelegate.processFinish(values[0]);

    }

    @Override
    protected void onPostExecute(Void value) {
    }
}
