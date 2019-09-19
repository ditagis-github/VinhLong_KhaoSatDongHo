package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.graphics.Bitmap
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.esri.arcgisruntime.data.ArcGISFeature
import org.apache.commons.io.IOUtils
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewMoreInfoAttachmentsAdapter
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class ViewAttachmentAsync(private val mActivity: Activity, private val mRootView: ViewGroup, selectedArcGISFeature: ArcGISFeature, private val mDelegate: AsyncResponse) :
        AsyncTask<Void, FeatureViewMoreInfoAttachmentsAdapter.Item, Void>() {
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private var builder: AlertDialog.Builder? = null
    private var layout: View? = null
    private val mApplication: DApplication = mActivity.application as DApplication
    private val mAttachments = arrayListOf<FeatureViewMoreInfoAttachmentsAdapter.Item>()
    private var mSize = 0
    init {
        mSelectedArcGISFeature = selectedArcGISFeature
    }

    interface AsyncResponse {
        fun processFinish(item: FeatureViewMoreInfoAttachmentsAdapter.Item)
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mApplication.progressDialog.show(mActivity, mRootView, "Đang tải hình ảnh...")
    }

    @SuppressLint("WrongThread")
    override fun doInBackground(vararg params: Void): Void? {

        val attachmentResults = mSelectedArcGISFeature!!.fetchAttachmentsAsync()
        attachmentResults.addDoneListener {
            try {

                mSize = mAttachments.size
                val attachments = attachmentResults.get()
                // if selected feature has attachments, display them in a list fashion
                if (!attachments.isEmpty()) {
                    //
                    for (attachment in attachments) {
                        if (attachment.contentType.contains(
                                        Bitmap.CompressFormat.JPEG.toString().toLowerCase())
                                || attachment.contentType.contains(Bitmap.CompressFormat.PNG.toString().toLowerCase())) {
                            val item = FeatureViewMoreInfoAttachmentsAdapter.Item()
                            item.name = attachment.name
                            val inputStreamListenableFuture = attachment.fetchDataAsync()
                            inputStreamListenableFuture.addDoneListener {
                                try {
                                    val inputStream = inputStreamListenableFuture.get()
                                    item.img = IOUtils.toByteArray(inputStream)
                                    mAttachments.add(item)
                                    //Kiểm tra nếu adapter có phần tử và attachment là phần tử cuối cùng thì show dialog

                                    mSize--
                                    publishProgress(item)


                                } catch (e: InterruptedException) {
                                    publishProgress()
                                } catch (e: ExecutionException) {
                                    publishProgress()
                                } catch (e: IOException) {
                                    publishProgress()
                                }
                            }

                        } else {

                        }
                    }

                } else {
                    publishProgress()
                    //                        MySnackBar.make(mCallout, "Không có file hình ảnh đính kèm", true);
                }

            } catch (e: Exception) {
                publishProgress()
                Log.e("ERROR", e.message)
            }
        }
        return null
    }


    override fun onProgressUpdate(vararg values: FeatureViewMoreInfoAttachmentsAdapter.Item) {
        super.onProgressUpdate(*values)
        if (mSize == 0) mApplication.progressDialog.dismiss()
        if (values.isNotEmpty()) mDelegate.processFinish(values[0])


    }

}

