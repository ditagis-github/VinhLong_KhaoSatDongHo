package vinhlong.ditagis.com.capnhatdongho.Editing;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Domain;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.adapter.ChiTietMauKiemNghiemAdapter;
import vinhlong.ditagis.com.capnhatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.capnhatdongho.async.NotifyVatTuDongHoAdapterChangeAsync;
import vinhlong.ditagis.com.capnhatdongho.async.RefreshVatTuAsync;
import vinhlong.ditagis.com.capnhatdongho.libs.FeatureLayerDTG;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;

/**
 * Created by NGUYEN HONG on 5/7/2018.
 */

public class EditingVatTu implements RefreshVatTuAsync.AsyncResponse {
    private MainActivity mainActivity;
    private ServiceFeatureTable vatTuSFT;
    private FeatureLayerDTG vatTuDTG;
    private VatTuApdapter vatTuApdapter;
    private List<Feature> table_feature;
    private ArcGISFeature featureDHKH;
    private ServiceFeatureTable dongHoKHSFT;
    private ServiceFeatureTable dmVatTu;

    public EditingVatTu(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setVatTuDTG(FeatureLayerDTG vatTuDTG) {
        this.vatTuDTG = vatTuDTG;
        this.vatTuSFT = (ServiceFeatureTable) vatTuDTG.getFeatureLayer().getFeatureTable();
    }

    public void setDmVatTu(ServiceFeatureTable dmVatTu) {
        this.dmVatTu = dmVatTu;
    }

    public void setDongHoKHSFT(ServiceFeatureTable dongHoKHSFT) {
        this.dongHoKHSFT = dongHoKHSFT;
    }

    public void deleteDanhSachMauDanhGia(ArcGISFeature featureDHKH) {
        this.featureDHKH = featureDHKH;
        final Map<String, Object> attributes = featureDHKH.getAttributes();
        final String idDiemDanhGia = attributes.get(mainActivity.getString(R.string.IDDIEMDANHGIA)).toString();
        if (idDiemDanhGia != null) {
            List<VatTuApdapter.VatTu> vatTus = new ArrayList<>();
            vatTuApdapter = new VatTuApdapter(mainActivity, vatTus);
            getRefreshTableVatTuAsync();
            if (table_feature != null && table_feature.size() > 0) {
                for (Feature feature : table_feature) {
                    deleteFeature(feature);
                }
            }
        }
    }

    public void showDanhSachVatTu(ArcGISFeature featureDHKH) {
        this.featureDHKH = featureDHKH;
        final Map<String, Object> attributes = featureDHKH.getAttributes();
        final String dBDongHoNuoc = attributes.get(Constant.DongHoKhachHangFields.DBDongHoNuoc).toString();
        if (dBDongHoNuoc != null) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
            final View layout_table_vattu = mainActivity.getLayoutInflater().inflate(R.layout.layout_title_listview_button, null);
            ListView listView = layout_table_vattu.findViewById(R.id.listview);

            ((TextView) layout_table_vattu.findViewById(R.id.txtTitlePopup)).setText(mainActivity.getString(R.string.title_danhsachmaukiemnghiem));
            Button btnAdd = layout_table_vattu.findViewById(R.id.btnAdd);
            if (this.vatTuDTG.getAction().isCreate() == false) {
                btnAdd.setVisibility(View.INVISIBLE);
            }
            btnAdd.setText("Thêm dữ liệu");
            btnAdd.setOnClickListener(v -> addTableVatTu());
            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (vatTuDTG.getAction().isView()) {
                    final VatTuApdapter.VatTu itemAtPosition = vatTuApdapter.getVatTus().get(position);
                    String objectid = itemAtPosition.getOBJECTID();
                    QueryParameters queryParameters = new QueryParameters();
                    String queryClause = mainActivity.getString(R.string.OBJECTID) + " = " + objectid;
                    queryParameters.setWhereClause(queryClause);
                    final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = vatTuSFT.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
                    queryResultListenableFuture.addDoneListener(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                FeatureQueryResult result = queryResultListenableFuture.get();
                                Iterator iterator = result.iterator();

                                if (iterator.hasNext()) {
                                    Feature feature = (Feature) iterator.next();
                                    showInfosSelectedItem(feature);
                                }

                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            });
            List<VatTuApdapter.VatTu> vatTus = new ArrayList<>();
            vatTuApdapter = new VatTuApdapter(mainActivity, vatTus);
            listView.setAdapter(vatTuApdapter);
            getRefreshTableVatTuAsync();
            builder.setView(layout_table_vattu);
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        }
    }

    private void showInfosSelectedItem(Feature feature) {
        Map<String, Object> attributes = feature.getAttributes();
        View layout_chitiet_maudanhgia = mainActivity.getLayoutInflater().inflate(R.layout.layout_title_listview, null);
        ListView listview_chitiet_maudanhgia = (ListView) layout_chitiet_maudanhgia.findViewById(R.id.listview);
        if (attributes.get(Constant.VatTuFields.DBDongHo) != null) {
            ((TextView) layout_chitiet_maudanhgia.findViewById(R.id.txtTongItem)).setText(attributes.get(Constant.VatTuFields.DBDongHo).toString());
        }
        final List<ChiTietMauKiemNghiemAdapter.Item> items = new ArrayList<>();
        List<Field> fields = vatTuSFT.getFields();
        final String[] updateFields = vatTuDTG.getUpdateFields();
        String[] unedit_Fields = mainActivity.getResources().getStringArray(R.array.unedit_Fields);
        for (Field field : fields) {
            ChiTietMauKiemNghiemAdapter.Item item = new ChiTietMauKiemNghiemAdapter.Item();
            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
            item.setFieldType(field.getFieldType());
            Object value = attributes.get(field.getName());
            if (value != null) {
                if (field.getDomain() != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) field.getDomain()).getCodedValues();
                    String valueDomain = getValueDomain(codedValues, value.toString()).toString();
                    if (valueDomain != null) item.setValue(valueDomain);
                } else switch (field.getFieldType()) {
                    case DATE:
                        item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                    default:
                        if (attributes.get(field.getName()) != null)
                            item.setValue(attributes.get(field.getName()).toString());
                }
            }
            if (this.vatTuDTG.getAction().isEdit()) {
                if (updateFields.length > 0) {
                    if (updateFields[0].equals("*") || updateFields[0].equals("")) {
                        item.setEdit(true);
                    } else {
                        for (String updateField : updateFields) {
                            if (item.getFieldName().equals(updateField)) {
                                item.setEdit(true);
                                break;
                            }
                        }
                    }
                }
                for (String unedit_Field : unedit_Fields) {
                    if (unedit_Field.toUpperCase().equals(item.getFieldName().toUpperCase())) {
                        item.setEdit(false);
                        break;
                    }
                }
            }
            items.add(item);
        }
        ChiTietMauKiemNghiemAdapter chiTietMauKiemNghiemAdapter = new ChiTietMauKiemNghiemAdapter(mainActivity, items);
        if (items != null) listview_chitiet_maudanhgia.setAdapter(chiTietMauKiemNghiemAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        builder.setView(layout_chitiet_maudanhgia);
        if (this.vatTuDTG.getAction().isEdit()) {
            builder.setPositiveButton(mainActivity.getString(R.string.btn_Accept), null);
        }
        if (this.vatTuDTG.getAction().isDelete()) {
            builder.setNegativeButton(mainActivity.getString(R.string.btn_Delete), null);
        }
        builder.setNeutralButton(mainActivity.getString(R.string.btn_Esc), null);
        listview_chitiet_maudanhgia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (vatTuDTG.getAction().isEdit()) {
                    editValueAttribute(parent, view, position, id);
                }
            }
        });
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton(android.R.string.ok, null);
        dialog.show();
        // Chỉnh sửa
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Feature selectedFeature = getSelectedFeature(items.get(0).getValue());
                for (ChiTietMauKiemNghiemAdapter.Item item : items) {
                    Domain domain = vatTuSFT.getField(item.getFieldName()).getDomain();
                    Object codeDomain = null;
                    if (item.getFieldName().equals("NgayCapNhat")) {
                        Calendar currentTime = Calendar.getInstance();
                        item.setValue(Constant.DATE_FORMAT.format((currentTime).getTime()));
                    }
                    if (domain != null) {
                        List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                        codeDomain = getCodeDomain(codedValues, item.getValue());
                    }
                    switch (item.getFieldType()) {
                        case DATE:
                            if (item.getCalendar() != null)
                                selectedFeature.getAttributes().put(item.getFieldName(), item.getCalendar());
                            break;
                        case DOUBLE:
                            if (item.getValue() != null)
                                selectedFeature.getAttributes().put(item.getFieldName(), Double.parseDouble(item.getValue()));
                            break;
                        case SHORT:
                            if (codeDomain != null) {
                                selectedFeature.getAttributes().put(item.getFieldName(), Short.parseShort(codeDomain.toString()));
                            } else if (item.getValue() != null)
                                selectedFeature.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                            break;
                        case TEXT:
                            if (codeDomain != null) {
                                selectedFeature.getAttributes().put(item.getFieldName(), codeDomain.toString());
                            } else if (item.getValue() != null)
                                selectedFeature.getAttributes().put(item.getFieldName(), item.getValue());
                            break;
                    }
                }
                chiTietMauKiemNghiemAdapter.notifyDataSetChanged();
                Calendar currentTime = Calendar.getInstance();
                selectedFeature.getAttributes().put("NgayCapNhat", currentTime);
                updateFeature(selectedFeature);
            }
        });
        // Xóa
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            Feature selectedFeature = getSelectedFeature(items.get(0).getValue());
            deleteFeature(selectedFeature);
            dialog.dismiss();
        });
        // Thoát
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> dialog.dismiss());
    }

    private void getRefreshTableVatTuAsync() {
        final Map<String, Object> attributes = this.featureDHKH.getAttributes();
        final String dBDongHoNuoc = attributes.get(Constant.DongHoKhachHangFields.DBDongHoNuoc).toString();
        new RefreshVatTuAsync(mainActivity, vatTuSFT, vatTuApdapter, this.vatTuDTG.getAction(), features -> {
            table_feature = features;
        }).execute(dBDongHoNuoc);
    }

    private Feature getSelectedFeature(String OBJECTID) {
        Feature rt_feature = null;
        for (Feature feature : table_feature) {
            Object objectID = feature.getAttributes().get(mainActivity.getString(R.string.OBJECTID));
            if (objectID != null && objectID.toString().equals(OBJECTID)) {
                rt_feature = feature;
            }
        }
        return rt_feature;
    }

    private Object getValueDomain(List<CodedValue> codedValues, String code) {
        Object value = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getCode().toString().equals(code)) {
                value = codedValue.getName();
                break;
            }

        }
        return value;
    }

    private void addTableVatTu() {
        final Map<String, Object> attributes = this.featureDHKH.getAttributes();
        final String dBDongHo = attributes.get(Constant.DongHoKhachHangFields.DBDongHoNuoc).toString();
        final Feature vatTuFeature = vatTuSFT.createFeature();
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        View layout_add_vatu = mainActivity.getLayoutInflater().inflate(R.layout.layout_title_listview_button, null);
        ListView listView = layout_add_vatu.findViewById(R.id.listview);
        final List<ChiTietMauKiemNghiemAdapter.Item> items = new ArrayList<>();
        final ChiTietMauKiemNghiemAdapter chiTietMauKiemNghiemAdapter = new ChiTietMauKiemNghiemAdapter(mainActivity, items);
        if (items != null) listView.setAdapter(chiTietMauKiemNghiemAdapter);
        builder.setView(layout_add_vatu);
        final AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();
        listView.setOnItemClickListener((parent, view, position, id) -> editValueAttribute(parent, view, position, id));
        List<Field> fields = vatTuSFT.getFields();
        String[] updateFields = vatTuDTG.getUpdateFields();
        String[] unedit_Fields = mainActivity.getResources().getStringArray(R.array.unedit_Fields);
        for (Field field : fields) {
            if (!field.getName().equals(Constant.OBJECTID)) {
                ChiTietMauKiemNghiemAdapter.Item item = new ChiTietMauKiemNghiemAdapter.Item();
                item.setAlias(field.getAlias());
                item.setFieldName(field.getName());
                item.setFieldType(field.getFieldType());
                if (updateFields.length > 0) {
                    if (updateFields[0].equals("*") || updateFields[0].equals("")) {
                        item.setEdit(true);
                    } else {
                        for (String updateField : updateFields) {
                            if (item.getFieldName().equals(updateField)) {
                                item.setEdit(true);
                                break;
                            }
                        }
                    }
                }
                for (String unedit_Field : unedit_Fields) {
                    if (unedit_Field.toUpperCase().equals(item.getFieldName().toUpperCase())) {
                        item.setEdit(false);
                        break;
                    }
                }
                if (field.getName().equals(Constant.VatTuFields.DBDongHo)) {
                    item.setValue(dBDongHo);
                }
                if (field.getName().equals(Constant.VatTuFields.MaKhachHang)) {
                    item.setValue(dBDongHo);
                }
                items.add(item);
            }
        }

        vatTuFeature.getAttributes().put(Constant.VatTuFields.DBDongHo, dBDongHo);
        Button btnAdd = layout_add_vatu.findViewById(R.id.btnAdd);
        btnAdd.setText(mainActivity.getString(R.string.title_add));
        btnAdd.setOnClickListener(v -> {
            dialog.dismiss();
            for (ChiTietMauKiemNghiemAdapter.Item item : items) {
                Domain domain = vatTuSFT.getField(item.getFieldName()).getDomain();
                Object codeDomain = null;
                if (domain != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                    codeDomain = getCodeDomain(codedValues, item.getValue());
                    vatTuFeature.getAttributes().put(item.getFieldName(), item.getValue());
                }
                switch (item.getFieldType()) {
                    case DATE:
                        if (item.getCalendar() != null)
                            vatTuFeature.getAttributes().put(item.getFieldName(), item.getCalendar());
                        break;
                    case DOUBLE:
                        if (item.getValue() != null)
                            vatTuFeature.getAttributes().put(item.getFieldName(), Double.parseDouble(item.getValue()));
                        break;
                    case SHORT:
                        if (codeDomain != null) {
                            vatTuFeature.getAttributes().put(item.getFieldName(), Short.parseShort(codeDomain.toString()));
                        } else if (item.getValue() != null)
                            vatTuFeature.getAttributes().put(item.getFieldName(), Short.parseShort(item.getValue()));
                        break;
                    case TEXT:
                        if (codeDomain != null) {
                            vatTuFeature.getAttributes().put(item.getFieldName(), codeDomain.toString());
                        } else if (item.getValue() != null)
                            vatTuFeature.getAttributes().put(item.getFieldName(), item.getValue());
                        break;
                }
            }
            addFeature(vatTuFeature);
        });
    }

    private Object getCodeDomain(List<CodedValue> codedValues, String value) {
        Object code = null;
        for (CodedValue codedValue : codedValues) {
            if (codedValue.getName().equals(value)) {
                code = codedValue.getCode();
                break;
            }

        }
        return code;
    }


    private void addFeature(final Feature feature) {
        ListenableFuture<Void> mapViewResult = vatTuSFT.addFeatureAsync(feature);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = vatTuSFT.applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getString(R.string.DATA_SUCCESSFULLY_INSERTED), Toast.LENGTH_SHORT).show();
                        getRefreshTableVatTuAsync();
                    } else {
                        Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getString(R.string.FAILED_TO_INSERT_DATA), Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            });
        });
    }


    private void deleteFeature(Feature table_maudanhgiaFeature) {
        final ListenableFuture<Void> mapViewResult = vatTuSFT.deleteFeatureAsync(table_maudanhgiaFeature);
        mapViewResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = vatTuSFT.applyEditsAsync();
                listListenableEditAsync.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                            if (featureEditResults.size() > 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getString(R.string.DATA_SUCCESSFULLY_DELETED), Toast.LENGTH_SHORT).show();
                                getRefreshTableVatTuAsync();
                            } else {
                                Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getString(R.string.FAILED_TO_DELETE_DATA), Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void updateFeature(final Feature table_maudanhgiaFeature) {
        final ListenableFuture<Void> mapViewResult = vatTuSFT.updateFeatureAsync(table_maudanhgiaFeature);
        mapViewResult.addDoneListener(new Runnable() {
            @Override
            public void run() {
                final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = vatTuSFT.applyEditsAsync();
                listListenableEditAsync.addDoneListener(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                            if (featureEditResults.size() > 0) {
                                Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getString(R.string.DATA_SUCCESSFULLY_UPDATED), Toast.LENGTH_SHORT).show();
                                getRefreshTableVatTuAsync();
                            } else {
                                Toast.makeText(mainActivity.getApplicationContext(), mainActivity.getString(R.string.FAILED_TO_UPDATE_DATA), Toast.LENGTH_SHORT).show();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });
    }

    private void editValueAttribute(final AdapterView<?> parent, View view, int position, final long id) {
        final ChiTietMauKiemNghiemAdapter.Item item = (ChiTietMauKiemNghiemAdapter.Item) parent.getItemAtPosition(position);
        if (item.isEdit()) {
            final Calendar[] calendar = new Calendar[1];
            final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle("Cập nhật thuộc tính");
            builder.setMessage(item.getAlias());
            builder.setCancelable(false).setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            final LinearLayout layout = (LinearLayout) mainActivity.getLayoutInflater().
                    inflate(R.layout.layout_dialog_update_feature_listview, null);
            builder.setView(layout);
            final FrameLayout layoutTextView = layout.findViewById(R.id.layout_edit_viewmoreinfo_TextView);
            final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
            ImageView img_selectTime = (ImageView) layout.findViewById(R.id.img_selectTime);
            final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
            final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
            final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
            final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

            final Domain domain = vatTuSFT.getField(item.getFieldName()).getDomain();
            if (domain != null) {
                layoutSpin.setVisibility(View.VISIBLE);
                List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                if (codedValues != null) {
                    List<String> codes = new ArrayList<>();
                    for (CodedValue codedValue : codedValues)
                        codes.add(codedValue.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                    spin.setAdapter(adapter);
                    if (item.getValue() != null) spin.setSelection(codes.indexOf(item.getValue()));

                }
            } else switch (item.getFieldType()) {
                case DATE:
                    layoutTextView.setVisibility(View.VISIBLE);
                    textView.setText(item.getValue());
                    img_selectTime.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final View dialogView = View.inflate(mainActivity, R.layout.date_time_picker, null);
                            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(mainActivity).create();
                            dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                                    calendar[0] = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                    String date = String.format("%02d/%02d/%d", datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
                                    textView.setText(date);
                                    alertDialog.dismiss();
                                }
                            });
                            alertDialog.setView(dialogView);
                            alertDialog.show();
                        }
                    });
                    break;
                case TEXT:
                    layoutEditText.setVisibility(View.VISIBLE);
                    editText.setText(item.getValue());
                    break;
                case SHORT:
                    layoutEditText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    editText.setText(item.getValue());
                    break;
                case DOUBLE:
                    layoutEditText.setVisibility(View.VISIBLE);
                    editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                    editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
                    editText.setText(item.getValue());
                    break;
            }
            builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (domain != null) {
                        item.setValue(spin.getSelectedItem().toString());
                    } else {
                        switch (item.getFieldType()) {
                            case DATE:
                                item.setValue(textView.getText().toString());
                                item.setCalendar(calendar[0]);
                                break;
                            case DOUBLE:
                                try {
                                    double x = Double.parseDouble(editText.getText().toString());
                                    item.setValue(editText.getText().toString());
                                } catch (Exception e) {
                                    Toast.makeText(mainActivity, mainActivity.getString(R.string.INCORRECT_INPUT_FORMAT), Toast.LENGTH_LONG).show();
                                }
                                break;
                            case TEXT:
                                item.setValue(editText.getText().toString());
                                break;
                            case SHORT:
                                try {
                                    short x = Short.parseShort(editText.getText().toString());
                                    item.setValue(editText.getText().toString());
                                } catch (Exception e) {
                                    Toast.makeText(mainActivity, mainActivity.getString(R.string.INCORRECT_INPUT_FORMAT), Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                    ChiTietMauKiemNghiemAdapter adapter = (ChiTietMauKiemNghiemAdapter) parent.getAdapter();
                    new NotifyVatTuDongHoAdapterChangeAsync(mainActivity).execute(adapter);
//                    dialog.dismiss();
                }
            });
            builder.setView(layout);
            AlertDialog dialog = builder.create();
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();

        }
    }
    @Override
    public void processFinish(List<Feature> features) {

    }
}
