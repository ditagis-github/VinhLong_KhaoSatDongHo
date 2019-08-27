package vinhlong.ditagis.com.khaosatdongho.async

import android.annotation.SuppressLint
import android.app.Activity
import android.os.AsyncTask
import android.support.design.widget.BottomSheetDialog
import android.widget.LinearLayout
import android.widget.TextView

import com.esri.arcgisruntime.concurrent.ListenableFuture
import com.esri.arcgisruntime.data.Feature
import com.esri.arcgisruntime.data.FeatureQueryResult
import com.esri.arcgisruntime.data.QueryParameters
import com.esri.arcgisruntime.data.ServiceFeatureTable
import kotlinx.android.synthetic.main.layout_progress_dialog.view.*

import java.util.ArrayList
import java.util.concurrent.ExecutionException

import vinhlong.ditagis.com.khaosatdongho.R

/**
 * Created by ThanLe on 4/16/2018.
 */

class QueryDongHoKhachHangAsync(private val mActivity: Activity, private val serviceFeatureTable: ServiceFeatureTable, private val txtTongItem: TextView?, asyncResponse: AsyncResponse) : AsyncTask<String, List<Feature>, Void>() {
    private var mDialog: BottomSheetDialog? = null

    private var mDelegate: AsyncResponse? = null


    init {
        this.mDelegate = asyncResponse
    }

    interface AsyncResponse {
        fun processFinish(features: List<Feature>?)
    }


    @SuppressLint("SetTextI18n")
    override fun onPreExecute() {
        super.onPreExecute()
        mDialog = BottomSheetDialog(this.mActivity)
        val view = mActivity.layoutInflater.inflate(R.layout.layout_progress_dialog, null, false) as LinearLayout
        view.txt_progress_dialog_title.text = "Đang lấy danh sách công việc..."
        mDialog!!.setContentView(view)
        mDialog!!.setCancelable(false)

        mDialog!!.show()


    }

    override fun doInBackground(vararg params: String): Void? {


        val features = ArrayList<Feature>()
        val queryParameters = QueryParameters()
        val queryClause = params[0]
        queryParameters.whereClause = queryClause
        val queryResultListenableFuture = serviceFeatureTable.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL)
        queryResultListenableFuture.addDoneListener {
            try {
                val result = queryResultListenableFuture.get()
                val iterator = result.iterator()
                while (iterator.hasNext()) {
                    val feature = iterator.next() as Feature

                    features.add(feature)
                }
                publishProgress(features)

            } catch (e: InterruptedException) {
                e.printStackTrace()
                publishProgress()
            } catch (e: ExecutionException) {
                e.printStackTrace()
                publishProgress()
            }
        }
        return null
    }

    override fun onProgressUpdate(vararg values: List<Feature>) {


        if (txtTongItem != null)
            txtTongItem.text = mActivity.getString(R.string.nav_thong_ke_tong_diem) + values[0].size
        if (mDialog != null && mDialog!!.isShowing) mDialog!!.dismiss()
        if (values == null || values.size == 0)
            mDelegate!!.processFinish(null)
        else
            mDelegate!!.processFinish(values[0])
        super.onProgressUpdate(*values)

    }


}

