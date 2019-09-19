package vinhlong.ditagis.com.khaosatdongho

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_view_attachment.*
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewMoreInfoAttachmentsAdapter
import vinhlong.ditagis.com.khaosatdongho.async.ViewAttachmentAsync
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication

class ViewAttachmentActivity : AppCompatActivity() {
    private lateinit var mApplication: DApplication
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_attachment)
        mApplication = application as DApplication
        val lstViewAttachment = lstView_alertdialog_attachments

        val attachmentsAdapter = FeatureViewMoreInfoAttachmentsAdapter(this, mutableListOf())
        lstViewAttachment.adapter = attachmentsAdapter

        val viewAttachmentAsync = ViewAttachmentAsync(this, container_attachment, mApplication.selectedFeature!!, object : ViewAttachmentAsync.AsyncResponse {
            override fun processFinish(item: FeatureViewMoreInfoAttachmentsAdapter.Item) {
                attachmentsAdapter.add(item)
                attachmentsAdapter.notifyDataSetChanged()
            }

        })
        viewAttachmentAsync.execute()
    }
}
