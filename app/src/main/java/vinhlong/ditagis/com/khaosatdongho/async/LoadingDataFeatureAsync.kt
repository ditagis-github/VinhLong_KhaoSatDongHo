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
        if (field.domain != null) {
            val codedValueDomain = field.domain as CodedValueDomain
            val adapter = ArrayAdapter(mActivity, android.R.layout.simple_list_item_1, ArrayList<String>())
            val spinner = layoutView.findViewById<Spinner>(R.id.spinner_add_spinner_value)
            spinner.adapter = adapter
            val values = ArrayList<String>()
            values.add(Constant.EMPTY)
            var selectedValue: String? = null
            for (codedValue in codedValueDomain.codedValues) {
                values.add(codedValue.name)
                if (value != null && codedValue.code === value)
                    selectedValue = codedValue.name
            }

            layoutView.findViewById<View>(R.id.llayout_add_feature_number_decimal).visibility = View.GONE
            layoutView.findViewById<View>(R.id.llayout_add_feature_spinner).visibility = View.VISIBLE
            layoutView.findViewById<View>(R.id.llayout_add_feature_edittext).visibility = View.GONE
            layoutView.findViewById<View>(R.id.llayout_add_feature_number).visibility = View.GONE

            val spinLayout = layoutView.findViewById<TextInputLayout>(R.id.llayout_add_feature_spinner)
            layoutView.txt_spin_title.text = field.alias
            spinLayout.hint = field.alias

            adapter.addAll(values)
            adapter.notifyDataSetChanged()

            for (i in values.indices) {
                if (selectedValue != null && values[i] === selectedValue) {
                    spinner.setSelection(i)
                    break
                }
            }
        } else {
            val nm = NumberFormat.getCurrencyInstance()
            when (field.fieldType) {
                Field.Type.UNKNOWN -> {
                }
                Field.Type.INTEGER, Field.Type.SHORT -> {
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number_decimal).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_spinner).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_edittext).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number).visibility = View.VISIBLE


                    val edit_number_layout = layoutView.findViewById<TextInputLayout>(R.id.llayout_add_feature_number)
                    edit_number_layout.hint = field.alias
                    if (value != null) {

                        try {
                            when (field.fieldType) {
                                Field.Type.INTEGER -> (layoutView.findViewById<View>(R.id.etxt_add_edit_number_value) as TextView).text = nm.format(Integer.parseInt(value.toString()).toLong())
                                Field.Type.SHORT -> (layoutView.findViewById<View>(R.id.etxt_add_edit_number_value) as TextView).text = nm.format(java.lang.Short.parseShort(value.toString()).toLong())
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
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number_decimal).visibility = View.VISIBLE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_spinner).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_edittext).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number).visibility = View.GONE


                    val edit_number_decimal_layout = layoutView.findViewById<TextInputLayout>(R.id.llayout_add_feature_number_decimal)
                    edit_number_decimal_layout.hint = field.alias
                    if (value != null) {
                        try {
                            when (field.fieldType) {
                                Field.Type.DOUBLE -> (layoutView.findViewById<View>(R.id.etxt_add_edit_number_decimal_value) as TextView).text = nm.format(java.lang.Double.parseDouble(value.toString()))
                                Field.Type.FLOAT -> (layoutView.findViewById<View>(R.id.etxt_add_edit_number_decimal_value) as TextView).text = nm.format(java.lang.Float.parseFloat(value.toString()).toDouble())
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
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number_decimal).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_spinner).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_edittext).visibility = View.VISIBLE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number).visibility = View.GONE

                    val textLayout = layoutView.findViewById<TextInputLayout>(R.id.llayout_add_feature_edittext)
                    textLayout.hint = field.alias

                    if (value != null) {
                        try {
                            (layoutView.findViewById<View>(R.id.edit_add_edittext_value) as TextView).text = value.toString()
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
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number_decimal).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_spinner).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_edittext).visibility = View.GONE
                    layoutView.findViewById<View>(R.id.llayout_add_feature_number).visibility = View.GONE
                }
            }
        }

        return layoutView


    }


}
