package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;


public class QueryTenMauAsycn extends AsyncTask<Void, String, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(String output);
    }

    public QueryTenMauAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
        DApplication dApplication = (DApplication) activity.getApplication();

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
        if (values == null || values.length == 0)
            this.mDelegate.processFinish(null);
        else
            this.mDelegate.processFinish(values[0]);

    }

    @Override
    protected void onPostExecute(Void value) {
    }
}
