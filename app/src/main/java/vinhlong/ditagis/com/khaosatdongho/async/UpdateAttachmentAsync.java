package vinhlong.ditagis.com.khaosatdongho.async;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.BottomSheetDialog;
import android.widget.LinearLayout;
import android.widget.TextView;

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

public class UpdateAttachmentAsync extends AsyncTask<Void, Boolean, Void> {
    private BottomSheetDialog mDialog;
    private MainActivity mActivity;
    private ServiceFeatureTable mServiceFeatureTable;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private byte[] mImage;
    private DApplication mApplication;
    private AsyncResponse mDelegate;

    public interface AsyncResponse {
        void processFinish(Boolean success);
    }

    public UpdateAttachmentAsync(MainActivity mainActivity, ArcGISFeature selectedArcGISFeature, byte[] image, AsyncResponse delegate) {
        this.mDelegate = delegate;
        this.mActivity = mainActivity;
        mServiceFeatureTable = (ServiceFeatureTable) selectedArcGISFeature.getFeatureTable();
        mSelectedArcGISFeature = selectedArcGISFeature;
        this.mImage = image;
        this.mApplication = (DApplication) mainActivity.getApplication();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog = new BottomSheetDialog(this.mActivity);
        LinearLayout view = (LinearLayout) mActivity.getLayoutInflater().inflate(R.layout.layout_progress_dialog, null, false);
        ((TextView) view.findViewById(R.id.txt_progress_dialog_title)).setText("Đang cập nhật thông tin...");
        mDialog.setContentView(view);
        mDialog.setCancelable(false);

        mDialog.show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        final String attachmentName = String.format(Constant.AttachmentName.UPDATE,
                mApplication.getUser().getUserName(), System.currentTimeMillis());
        final ListenableFuture<Attachment> addResult = mSelectedArcGISFeature.addAttachmentAsync(mImage, Bitmap.CompressFormat.PNG.toString(), attachmentName);
        addResult.addDoneListener(() -> {
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
                                        publishProgress(true);
                                    } else {
                                        publishProgress();
                                    }
                                } else {
                                    publishProgress();
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                publishProgress();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                                publishProgress();
                            }


                        });


                    });
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
                publishProgress();
            } catch (ExecutionException e) {
                e.printStackTrace();
                publishProgress();
            }
        });
        return null;
    }

    @Override
    protected void onProgressUpdate(Boolean... values) {
        super.onProgressUpdate(values);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
        if (values != null && values.length > 0 && values[0]) {
            mDelegate.processFinish(true);
        } else mDelegate.processFinish(false);

    }


}

