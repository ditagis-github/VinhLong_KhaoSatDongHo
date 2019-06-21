package vinhlong.ditagis.com.khaosatdongho.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.adapter.ChiTietVatTuAdapter;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class NotifyVatTuDongHoAdapterChangeAsync extends AsyncTask<ChiTietVatTuAdapter, Void, Void> {
    private BottomSheetDialog mDialog;
    private Context mContext;
    private Activity mActivity;

    public NotifyVatTuDongHoAdapterChangeAsync(Activity activity) {
        mActivity =activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mContext);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang cập nhật giao diện...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();

    }

    @Override
    protected Void doInBackground(ChiTietVatTuAdapter... params) {
        final ChiTietVatTuAdapter adapter = params[0];
        try {
            Thread.sleep(500);
            mActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    adapter.notifyDataSetChanged();

                }
            });
        } catch (InterruptedException e) {

        }


        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        if (mDialog != null || mDialog.isShowing())
            mDialog.dismiss();
        super.onPostExecute(result);

    }

}
