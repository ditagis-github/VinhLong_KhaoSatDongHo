package vinhlong.ditagis.com.khaosatdongho.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View

import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

import com.esri.arcgisruntime.data.Field

import java.util.Calendar

import vinhlong.ditagis.com.khaosatdongho.R


class ChiTietVatTuAdapter(private val mContext: Context, private var items: MutableList<Item>?) : ArrayAdapter<ChiTietVatTuAdapter.Item>(mContext, 0, items) {

    fun getItems(): List<Item>? {
        return items
    }

    fun setItems(items: MutableList<Item>) {
        this.items = items
    }

    override fun clear() {
        items!!.clear()
    }

    override fun getCount(): Int {
        return items!!.size
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    @SuppressLint("ResourceAsColor")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            convertView = inflater.inflate(R.layout.item_text_text_image,null)
        }
        val item = items!![position]
        val textViewItem1 = convertView!!.findViewById<View>(R.id.txtStt) as TextView
        val textViewItem2 = convertView.findViewById<View>(R.id.txtTenVatTu) as TextView
        val imageView = convertView.findViewById<View>(R.id.img_Item) as ImageView
        textViewItem1.text = item.alias
        textViewItem2.text = item.value
        if (item.isEdit) {
            imageView.visibility = View.VISIBLE
        } else
            imageView.visibility = View.GONE

        return convertView
    }


    class Item {
        var alias: String? = null
        var value: String? = null
        var isEdit: Boolean = false
        var fieldName: String? = null
        var fieldType: Field.Type? = null
        var calendar: Calendar? = null


        constructor(alias: String, value: String) {
            this.alias = alias
            this.value = value
            this.isEdit = false
        }

        constructor()
    }
}
