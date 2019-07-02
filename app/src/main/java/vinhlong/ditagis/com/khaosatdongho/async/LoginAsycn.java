package vinhlong.ditagis.com.khaosatdongho.async;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.UserDangNhap;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.Preference;

public class LoginAsycn extends AsyncTask<String, Void, Object> {
    private Exception exception;
    private BottomSheetDialog mDialog;
    private Context mContext;
    private AsyncResponse mDelegate;
    private DApplication mApplication;
    private Activity mActivity;
    public interface AsyncResponse {
        void processFinish(Object output);
    }

    public LoginAsycn(Activity activity, AsyncResponse delegate) {
        this.mApplication = (DApplication) activity.getApplication();
        this.mContext = activity;
        this.mDelegate = delegate;
        this.mActivity = activity;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mContext);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang đăng nhập...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);
        mDialog.show();

    }

    @Override
    protected Object doInBackground(String... params) {
        String userName = params[0];
        String passWord = params[1];
//        String passEncoded = (new EncodeMD5()).encode(pin + "_DITAGIS");
        // Do some validation here
        String urlParameters = String.format("Username=%s&Password=%s", userName, passWord);
        String urlWithParam = String.format("%s?%s", Constant.API_URL.LOGIN, urlParameters);
        try {
            URL url = new URL(urlWithParam);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setRequestMethod(Constant.METHOD.POST);
                JSONObject cred = new JSONObject();
                cred.put("Username", userName);
                cred.put("Password", passWord);

                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setRequestProperty("Accept", "application/json");
                conn.setUseCaches(false);
                OutputStream outputStream = conn.getOutputStream();
                OutputStreamWriter wr = new OutputStreamWriter(outputStream);
                wr.write(cred.toString());
                wr.flush();
                wr.close();
                outputStream.close();
                conn.connect();
                InputStream inputStream = conn.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                    break;
                }
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
                String token = stringBuilder.toString().replace("\"", "");
                Preference.getInstance().savePreferences(mContext.getString(R.string.preference_login_api),token);
                if (checkAccess()) {
                    User user = new User();
                    user.setDisplayName(getDisplayName());
                    user.setUserName(userName);
                    user.setPassWord(passWord);
                    conn.disconnect();
                    return user;
                } else {
                    conn.disconnect();
                    return "Không có quyền truy cập ứng dụng này";
                }
            } catch (Exception e) {
                Log.e("ERROR", e.getMessage(), e);
                return e.toString();
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("ERROR", e.getMessage(), e);
            return e.toString();
        }

    }


    @Override
    protected void onPostExecute(Object o) {
//        if (user != null) {
        mDialog.dismiss();
        this.mDelegate.processFinish(o);
//        }
    }

    private Boolean checkAccess() {
        boolean isAccess = false;
        try {
            URL url = new URL(Constant.API_URL.IS_ACCESS);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod(Constant.METHOD.GET);
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = bufferedReader.readLine();
                if (line.equals("true"))
                    isAccess = true;

            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        } finally {
            return isAccess;
        }
    }

    private String getDisplayName() {
        String displayName = "";
        try {
            URL url = new URL(Constant.API_URL.DISPLAY_NAME);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            try {
                conn.setDoOutput(false);
                conn.setRequestMethod(Constant.METHOD.GET);
                conn.setRequestProperty("Authorization", Preference.getInstance().loadPreference(mContext.getString(R.string.preference_login_api)));
                conn.connect();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    pajsonRouteeJSon(line);
                    break;
                }

            } catch (Exception e) {
                Log.e("error", e.toString());
            } finally {
                conn.disconnect();
            }
        } catch (Exception e) {
            Log.e("error", e.toString());
        } finally {
            return displayName;
        }
    }

    private void pajsonRouteeJSon(String data) throws JSONException {
        if (data != null) {
            String myData = "{ \"account\": [".concat(data).concat("]}");
            JSONObject jsonData = new JSONObject(myData);
            JSONArray jsonRoutes = jsonData.getJSONArray("account");
//        jsonData.getJSONArray("account");
            for (int i = 0; i < jsonRoutes.length(); i++) {
                JSONObject jsonRoute = jsonRoutes.getJSONObject(i);
                String displayName = jsonRoute.getString(mContext.getString(R.string.sql_coloumn_login_displayname));
                String username = jsonRoute.getString(mContext.getString(R.string.sql_coloumn_login_username));
                UserDangNhap.getInstance().setUser(new User());
                UserDangNhap.getInstance().getUser().setDisplayName(displayName);
                UserDangNhap.getInstance().getUser().setUserName(username);
            }
        }
    }
}