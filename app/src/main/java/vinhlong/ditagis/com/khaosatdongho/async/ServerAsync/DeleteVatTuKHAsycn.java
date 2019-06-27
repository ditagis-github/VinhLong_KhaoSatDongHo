package vinhlong.ditagis.com.khaosatdongho.async.ServerAsync;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.net.HttpURLConnection;
import java.net.URL;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;

public class DeleteVatTuKHAsycn extends AsyncTask<Long, Void, Boolean> {
    private BottomSheetDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;
    private Activity mActivity;
    public interface AsyncResponse {
        void processFinish(Boolean output);
    }

    public DeleteVatTuKHAsycn(Activity activity,AsyncResponse delegate) {
        this.mContext = activity;
        this.mActivity = activity;
        this.mDelegate = delegate;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang cập nhật vật tư...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Long... params) {
        if (params != null && params.length > 0) {
            try {
                long idKhachHang = params[0];
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