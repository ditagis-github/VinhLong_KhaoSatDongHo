package vinhlong.ditagis.com.khaosatdongho.async


import android.app.Activity
import android.os.AsyncTask
import android.support.design.widget.TextInputLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.widget.*
import com.esri.arcgisruntime.data.ArcGISFeature
import com.esri.arcgisruntime.data.CodedValueDomain
import com.esri.arcgisruntime.data.Field
import kotlinx.android.synthetic.main.item_add_feature.view.*
import vinhlong.ditagis.com.khaosatdongho.R
import vinhlong.ditagis.com.khaosatdongho.utities.Constant
import java.text.NumberFormat
import java.util.*

class LoadingDataFeatureAsync(private val mActivity: Activity, private val mArcGISFeature: ArcGISFeature?, private val mDelegate: AsyncResponse) : AsyncTask<Void, Void, Void>() {

    interface AsyncResponse {
        fun processFinish(views: List<View>?)
    }

    override fun doInBackground(vararg voids: Void): Void? {
        publishProgress()
        return null
    }

    override fun onProgressUpdate(vararg values: Void) {
        mDelegate.processFinish(loadDataToAdd())
        super.onProgressUpdate(*values)
    }

    private fun loadDataToAdd(): List<View> {
        val views = ArrayList<View>()
        val layoutManager = LinearLayoutManager(mActivity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        for (field in mArcGISFeature!!.featureTable.fields) {
            if (Constant.DongHoKhachHangFields.UpdateFields.contains(field.name))
                views.add(getView(field))

        }


        return views
    }

    private fun getView(field: Field): View {


        val layoutView = mActivity.layoutInflater.inflate(R.layout.item_add_feature, null, false) as LinearLayout

        var value: Any? = null
        if (mArcGISFeature != null) {
            value = mArcGISFeature.attributes[field.name]
        }
        val spinLayout = layoutView.findViewById<TextInputLayout>(R.id.llayout_add_feature_spinner)
        if (field.domain != null) {
            val codedValueDomain = field.domain as CodedValueDomain
            val adapter = ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, ArrayList<String>())
            layoutView.spinner_add_spinner_value.adapter = adapter
            val values = ArrayList<String>()
            values.add(Constant.EMPTY)
            var selectedValue: String? = null
            for (codedValue in codedValueDomain.codedValues) {
                values.add(codedValue.name)
                if (value != null && codedValue.code == value)
                    selectedValue = codedValue.name
            }

            layoutView.llayout_add_feature_number_decimal.visibility = View.GONE
            spinLayout.visibility = View.VISIBLE
            layoutView.llayout_add_feature_edittext.visibility = View.GONE
            layoutView.llayout_add_feature_number.visibility = View.GONE


            spinLayout.hint = field.alias
            spinLayout.tag = field.name
            layoutView.txt_spin_title.setText(field.alias)
            adapter.addAll(values)
            adapter.notifyDataSetChanged()

            for (i in values.indices) {
                if (selectedValue != null && values[i] == selectedValue) {
                    layoutView.spinner_add_spinner_value.setSelection(i)
                    break
                }
            }
        } else {
            val nm = NumberFormat.getCurrencyInstance()
            when (field.fieldType) {
                Field.Type.UNKNOWN -> {
                }
                Field.Type.INTEGER, Field.Type.SHORT -> {
                    layoutView.llayout_add_feature_number_decimal.visibility = View.GONE
                    spinLayout.visibility = View.GONE
                    layoutView.llayout_add_feature_edittext.visibility = View.GONE
                    layoutView.llayout_add_feature_number.visibility = View.VISIBLE


                    layoutView.llayout_add_feature_number.hint = field.alias
                    layoutView.llayout_add_feature_number.tag = field.name
                    if (value != null) {

                        try {
                            when (field.fieldType) {
                                Field.Type.INTEGER -> layoutView.etxt_add_edit_number_value.setText(nm.format(Integer.parseInt(value.toString()).toLong()))
                                Field.Type.SHORT -> layoutView.etxt_add_edit_number_value.setText(nm.format(java.lang.Short.parseShort(value.toString()).toLong()))
                                else -> {
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(mActivity, "Có lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show()
                        }


                    }
                }
                Field.Type.GUID -> {
                }
                Field.Type.DOUBLE, Field.Type.FLOAT -> {
                    layoutView.llayout_add_feature_number_decimal.visibility = View.VISIBLE
                    spinLayout.visibility = View.GONE
                    layoutView.llayout_add_feature_edittext.visibility = View.GONE
                    layoutView.llayout_add_feature_number.visibility = View.GONE


                    layoutView.llayout_add_feature_number_decimal.hint = field.alias
                    layoutView.llayout_add_feature_number_decimal.tag = field.name
                    if (value != null) {
                        try {
                            when (field.fieldType) {
                                Field.Type.DOUBLE -> layoutView.etxt_add_edit_number_decimal_value.setText(nm.format(java.lang.Double.parseDouble(value.toString())))
                                Field.Type.FLOAT -> layoutView.etxt_add_edit_number_decimal_value.setText(nm.format(java.lang.Float.parseFloat(value.toString()).toDouble()))
                                else -> {
                                }
                            }
                        } catch (e: Exception) {
                            Toast.makeText(mActivity, "Có lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show()
                        }


                    }
                }
                Field.Type.DATE -> {
                }
                Field.Type.TEXT -> {
                    layoutView.llayout_add_feature_number_decimal.visibility = View.GONE
                    spinLayout.visibility = View.GONE
                    layoutView.llayout_add_feature_edittext.visibility = View.VISIBLE
                    layoutView.llayout_add_feature_number.visibility = View.GONE

                    layoutView.llayout_add_feature_edittext.hint = field.alias
                    layoutView.llayout_add_feature_edittext.tag = field.name

                    if (value != null) {
                        try {
                            layoutView.edit_add_edittext_value.setText( value.toString())
                        } catch (e: Exception) {
                            Toast.makeText(mActivity, "Có lỗi khi load dữ liệu", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                Field.Type.OID -> {
                }
                Field.Type.GLOBALID -> {
                }
                Field.Type.BLOB -> {
                }
                Field.Type.GEOMETRY -> {
                }
                Field.Type.RASTER -> {
                }
                Field.Type.XML -> {
                }
                else -> {
                    layoutView.llayout_add_feature_number_decimal.visibility = View.GONE
                    spinLayout.visibility = View.GONE
                    layoutView.llayout_add_feature_edittext.visibility = View.GONE
                    layoutView.llayout_add_feature_number.visibility = View.GONE
                }
            }
        }

        return layoutView


    }


}
