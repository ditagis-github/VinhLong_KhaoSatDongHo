package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.Attachment
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*

import org.apache.commons.io.IOUtils

import java.io.IOException
import java.io.InputStream
import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vinhlong.ditagis.com.khaosatdongho.MainActivity
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.adapter.ChiTietVatTuAdapter
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewMoreInfoAttachmentsAdapter

/**
 * Created by ThanLe on 4/16/2018.
 */
@SuppressLint("StaticFieldLeak")
class ViewAttachmentAsync( private val mActivity: MainActivity, selectedArcGISFeature: ArcGISFeature) : AsyncTask<Void, Int, Void>() {
    private var mDialog: BottomSheetDialog? = null
    private var mSelectedArcGISFeature: ArcGISFeature? = null
    private var builder: AlertDialog.Builder? = null
    private var layout: View? = null

    init {
        mSelectedArcGISFeature = selectedArcGISFeature
    }

    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()


        mDialog = BottomSheetDialog(this.mActivity)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang tải hình ảnh đính kèm..."
        val layoutCancel = view.findViewById<LinearLayout>(R.id.layout_progress_dialog_cancel)
        layoutCancel.visibility = View.VISIBLE
        layoutCancel.setOnClickListener { publishProgress(0) }
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()
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


    protected override fun onProgressUpdate(vararg values: Int?) {
        if (values[0] == 0) {
            //            if (mDialog != null && mDialog.isShowing()) {
            //                mDialog.dismiss();
            //            }
            //        } else if (values[0] == -1) {
            if (mDialog != null && mDialog!!.isShowing) {
                mDialog!!.dismiss()

                builder!!.setView(layout)
                builder!!.setCancelable(false)
                builder!!.setPositiveButton("Thoát") { dialog, which -> dialog.dismiss() }
                val dialog = builder!!.create()
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)

                dialog.show()
            }
        }
        super.onProgressUpdate(*values)

    }

}

