package vinhlong.ditagis.com.capnhatdongho.async;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Attachment;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.adapter.FeatureViewMoreInfoAttachmentsAdapter;

/**
 * Created by ThanLe on 4/16/2018.
 */

public class ViewAttachmentAsync extends AsyncTask<Void, Integer, Void> {
    private ProgressDialog mDialog;
    private MainActivity mMainActivity;
    private ArcGISFeature mSelectedArcGISFeature = null;
    private AlertDialog.Builder builder;
    private View layout;

    public ViewAttachmentAsync(MainActivity context, ArcGISFeature selectedArcGISFeature) {
        mMainActivity = context;
        mSelectedArcGISFeature = selectedArcGISFeature;
        mDialog = new ProgressDialog(context, android.R.style.Theme_Material_Dialog_Alert);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mDialog.setMessage(mMainActivity.getString(R.string.async_dang_lay_hinh_anh_dinh_kem));
        mDialog.setCancelable(false);
        mDialog.setButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                publishProgress(0);
            }
        });
        mDialog.show();

    }

    @Override
    protected Void doInBackground(Void... params) {
        builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        LayoutInflater layoutInflater = LayoutInflater.from(mMainActivity);
        layout = layoutInflater.inflate(R.layout.layout_viewmoreinfo_feature_attachment, null);
        ListView lstViewAttachment = layout.findViewById(R.id.lstView_alertdialog_attachments);

        final FeatureViewMoreInfoAttachmentsAdapter attachmentsAdapter = new FeatureViewMoreInfoAttachmentsAdapter(mMainActivity, new ArrayList<>());
        lstViewAttachment.setAdapter(attachmentsAdapter);
        final ListenableFuture<List<Attachment>> attachmentResults = mSelectedArcGISFeature.fetchAttachmentsAsync();
        attachmentResults.addDoneListener(new Runnable() {
            @Override
            public void run() {
                try {

                    final List<Attachment> attachments = attachmentResults.get();
                    final int[] size = {attachments.size()};
                    // if selected feature has attachments, display them in a list fashion
                    if (!attachments.isEmpty()) {
                        //
                        for (final Attachment attachment : attachments) {
                            if (attachment.getContentType().toLowerCase().trim().contains("jpeg") || attachment.getContentType().toLowerCase().trim().contains("png")) {
                                final FeatureViewMoreInfoAttachmentsAdapter.Item item = new FeatureViewMoreInfoAttachmentsAdapter.Item();
                                item.setName(attachment.getName());
                                final ListenableFuture<InputStream> inputStreamListenableFuture = attachment.fetchDataAsync();
                                inputStreamListenableFuture.addDoneListener(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            InputStream inputStream = inputStreamListenableFuture.get();
                                            item.setImg(IOUtils.toByteArray(inputStream));
                                            attachmentsAdapter.add(item);
                                            attachmentsAdapter.notifyDataSetChanged();
                                            size[0]--;
                                            //Kiểm tra nếu adapter có phần tử và attachment là phần tử cuối cùng thì show dialog


                                            publishProgress(size[0]);


                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        } catch (ExecutionException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });

                            }
                        }

                    } else {
                        publishProgress(0);
//                        MySnackBar.make(mCallout, "Không có file hình ảnh đính kèm", true);
                    }

                } catch (Exception e) {
                    Log.e("ERROR", e.getMessage());
                }
            }
        });
        return null;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        if (values[0] == 0) {
//            if (mDialog != null && mDialog.isShowing()) {
//                mDialog.dismiss();
//            }
//        } else if (values[0] == -1) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.dismiss();

                builder.setView(layout);
                builder.setCancelable(false);
                builder.setPositiveButton("Thoát", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                dialog.show();
            }
        }
        super.onProgressUpdate(values);

    }


    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }

}

