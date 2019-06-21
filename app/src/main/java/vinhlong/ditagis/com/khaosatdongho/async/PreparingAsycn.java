package vinhlong.ditagis.com.khaosatdongho.async;

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
import java.util.List;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.LayerInfoDTG;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.ListObjectDB;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;


public class PreparingAsycn extends AsyncTask<Void, Void, Void> {
    private BottomSheetDialog mDialog;
    private Activity mActivity;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Void output);
    }

    public PreparingAsycn(Activity activity, AsyncResponse delegate) {
        this.mActivity = activity;
        this.mDelegate = delegate;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang khởi tạo bản đồ...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            URL url = new URL(Constant.API_URL.LAYER_INFO);
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
                pajsonRouteeJSon(buffer.toString());
            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
//            ListFeatureLayerDTGDB listFeatureLayerDTGDB = new ListFeatureLayerDTGDB(mActivity);
//            ListObjectDB.getInstance().setLstFeatureLayerDTG(listFeatureLayerDTGDB.find(Preference.getInstance().loadPreference(
//                    mActivity.getString(R.string.preference_username)
//            )));
        } catch (Exception e) {
            Log.e("Lỗi lấy danh sách DMA", e.toString());
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);


    }

    @Override
    protected void onPostExecute(Void value) {
//        if (khachHang != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(value);
//        }
    }

    private void pajsonRouteeJSon(String data) throws JSONException {
        if (data == null)
            return;
        String myData = "{ \"layerInfo\": ".concat(data).concat("}");
        JSONObject jsonData = new JSONObject(myData);
        JSONArray jsonRoutes = jsonData.getJSONArray("layerInfo");
        List<LayerInfoDTG> layerDTGS = new ArrayList<>();
        for (int i = 0; i < jsonRoutes.length(); i++) {
            JSONObject jsonRoute = jsonRoutes.getJSONObject(i);


//           LayerInfoDTG layerInfoDTG = new LayerInfoDTG();
            layerDTGS.add(new LayerInfoDTG(jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_id)),
                    jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_title)),
                    jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_url)),
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_iscreate)), jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isdelete)),
                    jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isedit)), jsonRoute.getBoolean(mActivity.getString(R.string.sql_coloumn_sys_isview)),
                    jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_outfield)), jsonRoute.getString(mActivity.getString(R.string.sql_coloumn_sys_definition))));


        }
        ListObjectDB.getInstance().setLstFeatureLayerDTG(layerDTGS);

    }

}
