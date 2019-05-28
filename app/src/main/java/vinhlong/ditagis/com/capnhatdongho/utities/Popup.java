package vinhlong.ditagis.com.capnhatdongho.utities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
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
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.loadable.LoadStatus;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.R;
import vinhlong.ditagis.com.capnhatdongho.adapter.FeatureViewInfoAdapter;
import vinhlong.ditagis.com.capnhatdongho.adapter.FeatureViewMoreInfoAdapter;
import vinhlong.ditagis.com.capnhatdongho.async.EditAsync;
import vinhlong.ditagis.com.capnhatdongho.async.NotifyDataSetChangeAsync;
import vinhlong.ditagis.com.capnhatdongho.async.QueryHanhChinhAsync;
import vinhlong.ditagis.com.capnhatdongho.async.ViewAttachmentAsync;
import vinhlong.ditagis.com.capnhatdongho.entities.DApplication;
import vinhlong.ditagis.com.capnhatdongho.libs.FeatureLayerDTG;

public class Popup extends AppCompatActivity implements View.OnClickListener {
    private MainActivity mainActivity;
    private ArcGISFeature featureDHKH = null;
    private ServiceFeatureTable dongHoKHSFT;
    private Callout mCallout;
    private FeatureLayerDTG dongHoKHDTG;
    private List<String> lstFeatureType;
    private LinearLayout linearLayout;
    private MapView mMapView;
    private DApplication dApplication;
    private ArrayList<Feature> quanhuyen_features;
    private Feature quanhuyen_feature;

    public Popup(MainActivity mainActivity, MapView mMapView, Callout callout) {
        this.mainActivity = mainActivity;
        this.dApplication = (DApplication) mainActivity.getApplication();
        this.mCallout = callout;
        this.mMapView = mMapView;
    }

    public void setDongHoKHDTG(FeatureLayerDTG dongHoKHDTG) {
        this.dongHoKHDTG = dongHoKHDTG;
        this.dongHoKHSFT = (ServiceFeatureTable) dongHoKHDTG.getFeatureLayer().getFeatureTable();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void setmSFTHanhChinh(ServiceFeatureTable mSFTHanhChinh) {
        new QueryHanhChinhAsync(this.mainActivity, mSFTHanhChinh, output -> quanhuyen_features = output).execute();
    }

    private void getHanhChinhFeature(String IDHanhChinh) {
        quanhuyen_feature = null;
        if (quanhuyen_features != null) {
            for (Feature feature : quanhuyen_features) {
                Object idHanhChinh = feature.getAttributes().get("IDHanhChinh");
                if (idHanhChinh != null && idHanhChinh.equals(IDHanhChinh)) {
                    quanhuyen_feature = feature;
                }
            }
        }
    }

    public void refressPopup() {
        String[] hiddenFields = this.mainActivity.getResources().getStringArray(R.array.hiddenFields);
        Map<String, Object> attributes = featureDHKH.getAttributes();
        ListView listView = linearLayout.findViewById(R.id.lstview_thongtinsuco);
        FeatureViewInfoAdapter featureViewInfoAdapter = new FeatureViewInfoAdapter(this.mainActivity, new ArrayList<FeatureViewInfoAdapter.Item>());
        listView.setAdapter(featureViewInfoAdapter);
        boolean checkHiddenField;
        Object maXa = attributes.get(Constant.HanhChinhFields.MAXA);
        if(maXa != null){
            getHanhChinhFeature(maXa.toString());
        }
        for (Field field : this.featureDHKH.getFeatureTable().getFields()) {
            checkHiddenField = false;
            for (String hiddenField : hiddenFields) {
                if (hiddenField.equals(field.getName())) {
                    checkHiddenField = true;
                    break;
                }
            }
            Object value = attributes.get(field.getName());
            if (value != null && !checkHiddenField) {
                FeatureViewInfoAdapter.Item item = new FeatureViewInfoAdapter.Item();
                item.setAlias(field.getAlias());
                item.setFieldName(field.getName());
                if (item.getFieldName().toUpperCase().equals(Constant.HanhChinhFields.MAXA)) {
                    if (quanhuyen_feature != null)
                        item.setValue(quanhuyen_feature.getAttributes().get(Constant.HanhChinhFields.TENHANHCHINH).toString());
                } else if (item.getFieldName().toUpperCase().equals(Constant.HanhChinhFields.MAHUYEN)) {
                    if (quanhuyen_feature != null)
                        item.setValue(quanhuyen_feature.getAttributes().get(Constant.HanhChinhFields.TENHUYEN).toString());
                } else if (field.getDomain() != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) this.featureDHKH.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                    Object valueDomainObject = getValueDomain(codedValues, value.toString());
                    if (valueDomainObject != null) item.setValue(valueDomainObject.toString());
                } else switch (field.getFieldType()) {
                    case DATE:
                        item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                    default:
                        item.setValue(value.toString());
                }

                featureViewInfoAdapter.add(item);
                featureViewInfoAdapter.notifyDataSetChanged();
            }
        }
    }

    public void dimissCallout() {
        FeatureLayer featureLayer = dongHoKHDTG.getFeatureLayer();
        featureLayer.clearSelection();
        if (mCallout != null && mCallout.isShowing()) {
            mCallout.dismiss();
        }
    }

    public LinearLayout showPopup(ArcGISFeature featureDHKH) {
        dimissCallout();
        this.featureDHKH = featureDHKH;
        FeatureLayer featureLayer = dongHoKHDTG.getFeatureLayer();
        featureLayer.selectFeature(featureDHKH);
        lstFeatureType = new ArrayList<>();
        for (int i = 0; i < featureDHKH.getFeatureTable().getFeatureTypes().size(); i++) {
            lstFeatureType.add(featureDHKH.getFeatureTable().getFeatureTypes().get(i).getName());
        }
        LayoutInflater inflater = LayoutInflater.from(this.mainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.popup, null);
        linearLayout.findViewById(R.id.imgbtn_close_popup)
                .setOnClickListener(view -> {
                    if (mCallout != null && mCallout.isShowing()) mCallout.dismiss();
                });
        refressPopup();
        if (dongHoKHDTG.getAction().isEdit()) {
            ImageButton imgBtn_ViewMoreInfo = (ImageButton) linearLayout.findViewById(R.id.imgBtn_ViewMoreInfo);
            imgBtn_ViewMoreInfo.setVisibility(View.VISIBLE);
            imgBtn_ViewMoreInfo.setOnClickListener(v -> viewMoreInfo());

            ImageButton imgBtn_viewtablethoigian = (ImageButton) linearLayout.findViewById(R.id.imgBtn_viewtablethoigian);
            imgBtn_viewtablethoigian.setVisibility(View.VISIBLE);
            linearLayout.findViewById(R.id.imgBtn_viewtablethoigian).setOnClickListener(v -> {
                this.dApplication.getEditingVatTu().showDanhSachVatTu(featureDHKH);
            });
        }
        if (dongHoKHDTG.getAction().isDelete()) {
            ImageButton imgBtn_delete = (ImageButton) linearLayout.findViewById(R.id.imgBtn_delete);
            imgBtn_delete.setVisibility(View.VISIBLE);
            imgBtn_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    featureDHKH.getFeatureTable().getFeatureLayer().clearSelection();
                    deleteFeature();
                }
            });
        }
        if (dongHoKHDTG.getAction().isEdit() && featureDHKH.canEditAttachments()) {
            ImageButton imgBtn_takePics = (ImageButton) linearLayout.findViewById(R.id.imgBtn_takePics);
            imgBtn_takePics.setVisibility(View.VISIBLE);
            imgBtn_takePics.setOnClickListener(v -> updateAttachment(featureDHKH));
        }
        if (this.featureDHKH.canEditAttachments()) {
            ImageButton imgBtn_view_attachment = (ImageButton) linearLayout.findViewById(R.id.imgBtn_view_attachment);
            imgBtn_view_attachment.setVisibility(View.VISIBLE);
            imgBtn_view_attachment.setOnClickListener(v -> viewAttachment(featureDHKH));
        }
        linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Envelope envelope = featureDHKH.getGeometry().getExtent();
        double scale = mMapView.getMapScale();
        double minScale = dongHoKHDTG.getFeatureLayer().getMinScale();
        if (scale > minScale) scale = minScale;
        mMapView.setViewpointGeometryAsync(envelope, 0);
//        mMapView.setViewpointScaleAsync(scale);
        mCallout.setLocation(envelope.getCenter());
        mCallout.setContent(linearLayout);
        this.runOnUiThread(() -> {
            mCallout.refresh();
            mCallout.show();
        });
        return linearLayout;
    }

    public void updateAttachment(ArcGISFeature featureDHKH) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = ImageFile.getFile(mainActivity);
        Uri uri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        dApplication.setSelectedFeature(featureDHKH);
        dApplication.setUri(uri);
        mainActivity.startActivityForResult(cameraIntent, Constant.REQUEST.ID_UPDATE_ATTACHMENT);
    }

    private void viewAttachment(ArcGISFeature featureDHKH) {
        ViewAttachmentAsync viewAttachmentAsync = new ViewAttachmentAsync(mainActivity, featureDHKH);
        viewAttachmentAsync.execute();
    }

    private void viewMoreInfo() {
        Map<String, Object> attr = featureDHKH.getAttributes();
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
        View layout = mainActivity.getLayoutInflater().inflate(R.layout.layout_viewmoreinfo_feature, null);
        final FeatureViewMoreInfoAdapter adapter = new FeatureViewMoreInfoAdapter(mainActivity, new ArrayList<FeatureViewMoreInfoAdapter.Item>());
        final ListView lstView = layout.findViewById(R.id.lstView_alertdialog_info);
        lstView.setAdapter(adapter);
        lstView.setOnItemClickListener((parent, view, position, id) -> edit(parent, view, position, id));
        String[] updateFields = dongHoKHDTG.getUpdateFields();
        String[] unedit_Fields = mainActivity.getResources().getStringArray(R.array.unedit_DHKH_Fields);
        String typeIdField = featureDHKH.getFeatureTable().getTypeIdField();
        Object maXa = attr.get(Constant.HanhChinhFields.MAXA);
        if(maXa != null){
            getHanhChinhFeature(maXa.toString());
        }
        for (Field field : this.featureDHKH.getFeatureTable().getFields()) {
            Object value = attr.get(field.getName());
            FeatureViewMoreInfoAdapter.Item item = new FeatureViewMoreInfoAdapter.Item();
            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
            if (value != null) {
                if (item.getFieldName().toUpperCase().equals(Constant.HanhChinhFields.MAXA)) {
                    if (quanhuyen_feature != null)
                        item.setValue(quanhuyen_feature.getAttributes().get(Constant.HanhChinhFields.TENHANHCHINH).toString());
                } else if (item.getFieldName().toUpperCase().equals(Constant.HanhChinhFields.MAHUYEN)) {
                    if (quanhuyen_feature != null)
                        item.setValue(quanhuyen_feature.getAttributes().get(Constant.HanhChinhFields.TENHUYEN).toString());
                } else if (item.getFieldName().equals(typeIdField)) {
                    List<FeatureType> featureTypes = featureDHKH.getFeatureTable().getFeatureTypes();
                    String valueFeatureType = getValueFeatureType(featureTypes, value.toString()).toString();
                    if (valueFeatureType != null) item.setValue(valueFeatureType);
                } else if (field.getDomain() != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) this.featureDHKH.getFeatureTable().getField(item.getFieldName()).getDomain()).getCodedValues();
                    Object valueDomain = getValueDomain(codedValues, value.toString());
                    if (valueDomain != null) item.setValue(valueDomain.toString());
                } else switch (field.getFieldType()) {
                    case DATE:
                        item.setValue(Constant.DATE_FORMAT.format(((Calendar) value).getTime()));
                        break;
                    case OID:
                    case TEXT:
                        item.setValue(value.toString());
                        break;
                    case SHORT:
                        item.setValue(value.toString());
                        break;
                }
            }
            item.setEdit(false);
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
            item.setFieldType(field.getFieldType());
            adapter.add(item);
            adapter.notifyDataSetChanged();
        }
        builder.setView(layout);
        builder.setCancelable(false);
        builder.setPositiveButton(mainActivity.getString(R.string.btn_Accept), null);
        builder.setNeutralButton(mainActivity.getString(R.string.btn_Esc), null);
        final AlertDialog dialog = builder.create();

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setPositiveButton(android.R.string.ok, null);
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> dialog.dismiss());
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            EditAsync editAsync = new EditAsync(mMapView, mainActivity, dongHoKHSFT, featureDHKH);
            try {
                editAsync.execute(adapter).get();
                refressPopup();
                dialog.dismiss();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

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

    private Object getValueFeatureType(List<FeatureType> featureTypes, String code) {
        Object value = null;
        for (FeatureType featureType : featureTypes) {
            if (featureType.getId().toString().equals(code)) {
                value = featureType.getName();
                break;
            }
        }
        return value;
    }

    private void edit(final AdapterView<?> parent, View view, int position, long id) {
        if (parent.getItemAtPosition(position) instanceof FeatureViewMoreInfoAdapter.Item) {
            final FeatureViewMoreInfoAdapter.Item item = (FeatureViewMoreInfoAdapter.Item) parent.getItemAtPosition(position);
            if (item.isEdit()) {
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

                final FrameLayout layoutTextView = layout.findViewById(R.id.layout_edit_viewmoreinfo_TextView);
                final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
                ImageView img_selectTime = (ImageView) layout.findViewById(R.id.img_selectTime);
                final LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
                final EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
                final LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
                final Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);

                final Domain domain = featureDHKH.getFeatureTable().getField(item.getFieldName()).getDomain();
                if (item.getFieldName().equals(featureDHKH.getFeatureTable().getTypeIdField())) {
                    layoutSpin.setVisibility(View.VISIBLE);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, lstFeatureType);
                    spin.setAdapter(adapter);
                    if (item.getValue() != null)
                        spin.setSelection(lstFeatureType.indexOf(item.getValue()));
                } else if (domain != null) {
                    layoutSpin.setVisibility(View.VISIBLE);
                    List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                    if (codedValues != null) {
                        List<String> codes = new ArrayList<>();
                        for (CodedValue codedValue : codedValues)
                            codes.add(codedValue.getName());
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                        spin.setAdapter(adapter);
                        if (item.getValue() != null)
                            spin.setSelection(codes.indexOf(item.getValue()));

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
                                        Calendar calendar = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                                        String s = String.format("%02d/%02d/%d", datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());

                                        textView.setText(s);
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
                        editText.setText(item.getValue());


                        break;
                    case DOUBLE:
                        layoutEditText.setVisibility(View.VISIBLE);
                        editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                        editText.setText(item.getValue());
                        break;
                }
                builder.setPositiveButton("Cập nhật", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (item.getFieldName().equals(featureDHKH.getFeatureTable().getTypeIdField()) || (domain != null)) {
                            item.setValue(spin.getSelectedItem().toString());
                        } else {
                            switch (item.getFieldType()) {
                                case DATE:
                                    item.setValue(textView.getText().toString());
                                    break;
                                case DOUBLE:
                                    try {
                                        double x = Double.parseDouble(editText.getText().toString());
                                        item.setValue(editText.getText().toString());
                                    } catch (Exception e) {
                                        Toast.makeText(mainActivity, R.string.input_format_incorrect, Toast.LENGTH_LONG).show();
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
                                        Toast.makeText(mainActivity, R.string.input_format_incorrect, Toast.LENGTH_LONG).show();
                                    }
                                    break;
                            }
                        }
                        dialog.dismiss();
                        FeatureViewMoreInfoAdapter adapter = (FeatureViewMoreInfoAdapter) parent.getAdapter();
                        new NotifyDataSetChangeAsync(mainActivity).execute(adapter);
                    }
                });
                builder.setView(layout);
                AlertDialog dialog = builder.create();
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.show();

            }
        }

    }

    private void deleteFeature() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity, android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("Xác nhận");
        builder.setMessage(R.string.question_delete_point);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                featureDHKH.loadAsync();

                // update the selected feature
                featureDHKH.addDoneLoadingListener(new Runnable() {
                    @Override
                    public void run() {
                        if (featureDHKH.getLoadStatus() == LoadStatus.FAILED_TO_LOAD) {
                            Log.d(getResources().getString(R.string.app_name), "Error while loading feature");
                        }
                        try {
                            // update feature in the feature table
                            ListenableFuture<Void> mapViewResult = dongHoKHSFT.deleteFeatureAsync(featureDHKH);
                            mapViewResult.addDoneListener(new Runnable() {
                                @Override
                                public void run() {
                                    // apply change to the server
                                    final ListenableFuture<List<FeatureEditResult>> serverResult = dongHoKHSFT.applyEditsAsync();
                                    serverResult.addDoneListener(new Runnable() {
                                        @Override
                                        public void run() {
                                            List<FeatureEditResult> edits = null;
                                            try {
                                                edits = serverResult.get();
                                                if (edits.size() > 0) {
                                                    dApplication.getEditingVatTu().deleteDanhSachMauDanhGia(featureDHKH);
                                                    if (!edits.get(0).hasCompletedWithErrors()) {
                                                        Log.e("", "Feature successfully updated");
                                                    }
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

                        } catch (Exception e) {
                            Log.e(getResources().getString(R.string.app_name), "deteting feature in the feature table failed: " + e.getMessage());
                        }
                    }
                });
                if (mCallout != null) mCallout.dismiss();
            }
        }).setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.show();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAdd:
//            @Override
//
                break;
        }
    }
}
