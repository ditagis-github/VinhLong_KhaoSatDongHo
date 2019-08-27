package vinhlong.ditagis.com.khaosatdongho.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import vinhlong.ditagis.com.khaosatdongho.R

/**
 * Created by ThanLe on 04/10/2017.
 */

class FeatureViewMoreInfoAttachmentsAdapter(private val mContext: Context, private val items: MutableList<Item>) : ArrayAdapter<FeatureViewMoreInfoAttachmentsAdapter.Item>(mContext, 0, items) {

    fun getItems(): List<Item> {
        return items
    }

    override fun clear() {
        items.clear()
    }

    override fun getCount(): Int {
        return items.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_viewmoreinfo_attachment,null)
        }
        val item = items[position]

        val txtValue = convertView!!.findViewById<View>(R.id.txt_viewmoreinfo_attachment_name) as TextView
        //todo
        txtValue.text = item.name

        val imageView = convertView.findViewById<ImageView>(R.id.img_viewmoreinfo_attachment)
        val bmp = BitmapFactory.decodeByteArray(item.img, 0, item.img!!.size)

        imageView.setImageBitmap(Bitmap.createScaledBitmap(bmp, bmp.width,
                bmp.height, false))
        return convertView
    }


    class Item {
        var name: String? = null
        var img: ByteArray? = null
    }
}
