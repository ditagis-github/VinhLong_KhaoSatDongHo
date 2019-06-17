package vinhlong.ditagis.com.khaosatdongho.async;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class UpdateAttachmentAsync extends AsyncTask<Void, Void, Void> {
    private ProgressDialog mDialog;
    private MainActivity mainActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private byte[] mImage;
    private DApplication dApplication;

    public UpdateAttachmentAsync(MainActivity mainActivity, ArcGISFeature selectedArcGISFeature, byte[] image) {
        this.mainActivity = mainActivity;
        mServiceFeatureTable = (ServiceFeatureTable) selectedArcGISFeature.getFeatureTable();
        mSelectedArcGISFeature = selectedArcGISFeature;
        mDialog = new ProgressDialog(mainActivity, android.R.style.Theme_Material_Dialog_Alert);
        this.mImage = image;
        this.dApplication = (DApplication) mainActivity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mainActivity.getString(R.string.async_dang_xu_ly));
        mDialog.setCancelable(false);

        mDialog.show();

    }

    @Override
    protected Void doInBackground(Void... params) {
        final String attachmentName = String.format(Constant.AttachmentName.UPDATE,
                dApplication.getUser().getUserName(), System.currentTimeMillis());
        final ListenableFuture<Attachment> addResult = mSelectedArcGISFeature.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName);
        addResult.addDoneListener(() -> {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();
            }
            try {
                Attachment attachment = addResult.get();
                if (attachment.getSize() > 0) {
                    final ListenableFuture<Void> voidListenableFuture = mServiceFeatureTable.updateFeatureAsync(mSelectedArcGISFeature);
                    voidListenableFuture.addDoneListener(() -> {
                        final ListenableFuture<List<FeatureEditResult>> applyEditsAsync = mServiceFeatureTable.applyEditsAsync();
                        applyEditsAsync.addDoneListener(() -> {
                            try {
                                List<FeatureEditResult> featureEditResults = applyEditsAsync.get();
                                if (featureEditResults.size() > 0) {
                                    if (!featureEditResults.get(0).hasCompletedWithErrors()) {
                                        //attachmentList.add(fileName);
                                        String s = mSelectedArcGISFeature.getAttributes().get("objectid").toString();
                                        // update the attachment list view/ on the control panel
                                    } else {
                                    }
                                } else {
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                            if (mDialog != null && mDialog.isShowing()) {
                                mDialog.dismiss();
                            }

                        });


                    });
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

