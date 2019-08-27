package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import com.esri.arcgisruntime.data.ArcGISFeature
import org.apache.commons.io.IOUtils
import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewMoreInfoAttachmentsAdapter
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication
import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class ViewAttachmentAsync(private val mActivity: MainActivity, private val mRootView: ViewGroup, selectedArcGISFeature: ArcGISFeature) : AsyncTask<Void, Int, Void>() {
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private var builder: AlertDialog.Builder? = null
    private var layout: View? = null
    private val mApplication: DApplication = mActivity.application as DApplication

    init {
        mSelectedArcGISFeature = selectedArcGISFeature
    }

    override fun onPreExecute() {
        super.onPreExecute()
        mApplication.progressDialog.show(mActivity, mRootView, "Đang tải hình ảnh...")
    }
    override fun doInBackground(vararg params: Void): Void? {
        builder = AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        val layoutInflater = LayoutInflater.from(mActivity)
        layout = layoutInflater.inflate(R.layout.layout_viewmoreinfo_feature_attachment, null)
        val lstViewAttachment = layout!!.findViewById<ListView>(R.id.lstView_alertdialog_attachments)

        val attachmentsAdapter = FeatureViewMoreInfoAttachmentsAdapter(mActivity, mutableListOf())
        lstViewAttachment.adapter = attachmentsAdapter
        val attachmentResults = mSelectedArcGISFeature!!.fetchAttachmentsAsync()
        attachmentResults.addDoneListener {
            try {

                val attachments = attachmentResults.get()
                val size = intArrayOf(attachments.size)
                // if selected feature has attachments, display them in a list fashion
                if (!attachments.isEmpty()) {
                    //
                    for (attachment in attachments) {
                        if (attachment.contentType.toLowerCase().trim { it <= ' ' }.contains("jpeg") || attachment.contentType.toLowerCase().trim { it <= ' ' }.contains("png")) {
                            val item = FeatureViewMoreInfoAttachmentsAdapter.Item()
                            item.name = attachment.name
                            val inputStreamListenableFuture = attachment.fetchDataAsync()
                            inputStreamListenableFuture.addDoneListener {
                                try {
                                    val inputStream = inputStreamListenableFuture.get()
                                    item.img = IOUtils.toByteArray(inputStream)
                                    attachmentsAdapter.add(item)
                                    attachmentsAdapter.notifyDataSetChanged()
                                    size[0]--
                                    //Kiểm tra nếu adapter có phần tử và attachment là phần tử cuối cùng thì show dialog


                                    publishProgress(size[0])


                                } catch (e: InterruptedException) {
                                    e.printStackTrace()
                                } catch (e: ExecutionException) {
                                    e.printStackTrace()
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }
                            }

                        }
                    }

                } else {
                    publishProgress(0)
                    //                        MySnackBar.make(mCallout, "Không có file hình ảnh đính kèm", true);
                }

            } catch (e: Exception) {
                Log.e("ERROR", e.message)
            }
        }
        return null
    }


    override fun onProgressUpdate(vararg values: Int?) {
        mApplication.progressDialog.dismiss()
        if (values[0] == 0) {

        }
        super.onProgressUpdate(*values)

    }

}

