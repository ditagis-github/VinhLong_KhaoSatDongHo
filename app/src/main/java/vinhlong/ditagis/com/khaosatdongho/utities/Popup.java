package vinhlong.ditagis.com.khaosatdongho.utities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.CodedValue;
import com.esri.arcgisruntime.data.CodedValueDomain;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Envelope;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.mapping.view.Callout;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.R;
import vinhlong.ditagis.com.khaosatdongho.UpdateActivity;
import vinhlong.ditagis.com.khaosatdongho.VatTuActivity;
import vinhlong.ditagis.com.khaosatdongho.adapter.FeatureViewInfoAdapter;
import vinhlong.ditagis.com.khaosatdongho.async.EditAsync;
import vinhlong.ditagis.com.khaosatdongho.async.QueryHanhChinhAsync;
import vinhlong.ditagis.com.khaosatdongho.async.ViewAttachmentAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG;

public class Popup extends AppCompatActivity implements View.OnClickListener {
    private MainActivity mMainActivity;
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
        this.mMainActivity = mainActivity;
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
        new QueryHanhChinhAsync(this.mMainActivity, mSFTHanhChinh, output -> quanhuyen_features = output).execute();
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
        String[] hiddenFields = this.mMainActivity.getResources().getStringArray(R.array.hiddenFields);
        Map<String, Object> attributes = featureDHKH.getAttributes();
        ListView listView = linearLayout.findViewById(R.id.lstview_thongtinsuco);
        FeatureViewInfoAdapter featureViewInfoAdapter = new FeatureViewInfoAdapter(this.mMainActivity, new ArrayList<FeatureViewInfoAdapter.Item>());
        listView.setAdapter(featureViewInfoAdapter);
        boolean checkHiddenField;
        Object maXa = attributes.get(Constant.HanhChinhFields.MAXA);
        if (maXa != null) {
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
        LayoutInflater inflater = LayoutInflater.from(this.mMainActivity.getApplicationContext());
        linearLayout = (LinearLayout) inflater.inflate(R.layout.popup, null);
        linearLayout.findViewById(R.id.imgbtn_close_popup)
                .setOnClickListener(view -> {
                    if (mCallout != null && mCallout.isShowing()) mCallout.dismiss();
                });
        refressPopup();
        if (dongHoKHDTG.getAction().isEdit()) {
            LinearLayout imgBtn_ViewMoreInfo = linearLayout.findViewById(R.id.llayout_ViewMoreInfo);
            imgBtn_ViewMoreInfo.setVisibility(View.VISIBLE);
            imgBtn_ViewMoreInfo.setOnClickListener(v -> {
                dApplication.setSelectedFeature(Popup.this.featureDHKH);
                Intent intent = new Intent(mMainActivity, UpdateActivity.class);
                mMainActivity.startActivityForResult(intent, Constant.REQUEST.ID_UPDATE_ATTRIBUTE);

            });


            LinearLayout imgBtn_vatTu = linearLayout.findViewById(R.id.llayout_vattu);
            imgBtn_vatTu.setVisibility(View.VISIBLE);
            linearLayout.findViewById(R.id.llayout_vattu).setOnClickListener(v -> {
//                this.dApplication.getEditingVatTu().showDanhSachVatTu(featureDHKH);
                dApplication.setSelectedFeature(Popup.this.featureDHKH);
                Intent intent = new Intent(mMainActivity, VatTuActivity.class);
                mMainActivity.startActivityForResult(intent, Constant.REQUEST.ID_UPDATE_VAT_TU);
            });
        }
//        if (dongHoKHDTG.getAction().isDelete()) {
//            LinearLayout imgBtn_delete = linearLayout.findViewById(R.id.llayout_delete);
//            imgBtn_delete.setVisibility(View.VISIBLE);
//            imgBtn_delete.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    featureDHKH.getFeatureTable().getFeatureLayer().clearSelection();
//                    deleteFeature();
//                }
//            });
//        }
        if (dongHoKHDTG.getAction().isEdit() && featureDHKH.canEditAttachments()) {
            LinearLayout imgBtn_takePics = linearLayout.findViewById(R.id.llayout_takePics);
            imgBtn_takePics.setVisibility(View.VISIBLE);
            imgBtn_takePics.setOnClickListener(v -> updateAttachment(featureDHKH));
        }
        if (this.featureDHKH.canEditAttachments()) {
            LinearLayout imgBtn_view_attachment = linearLayout.findViewById(R.id.llayout_view_attachment);
            imgBtn_view_attachment.setVisibility(View.VISIBLE);
            imgBtn_view_attachment.setOnClickListener(v -> viewAttachment(featureDHKH));
        }
        if (this.featureDHKH.canUpdateGeometry()) {
            LinearLayout edit_location = linearLayout.findViewById(R.id.llayout_edit_location);
            edit_location.setVisibility(View.VISIBLE);
            edit_location.setOnClickListener(v -> editLocation(featureDHKH));
        }
        if (dongHoKHDTG.getAction().isEdit()
                && this.featureDHKH.getAttributes().get(Constant.DongHoKhachHangFields.TINH_TRANG).equals(Constant.TinhTrangDongHoKhachHang.DANG_KHAO_SAT)) {
            LinearLayout completeLayout = linearLayout.findViewById(R.id.llayout_complete);
            completeLayout.setVisibility(View.VISIBLE);
            completeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hoanTatKhaoSat();
                }
            });
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

    private void hoanTatKhaoSat() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mMainActivity, android.R.style.Theme_Material_Dialog_Alert);
        builder.setTitle("Xác nhận");
        builder.setMessage(String.format("Bạn có chắc muốn hoàn tất đồng hồ %s", Popup.this.featureDHKH.getAttributes().get(Constant.DongHoKhachHangFields.ID)));
        builder.setNegativeButton("Hoàn tất", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Popup.this.featureDHKH.getAttributes().put(Constant.DongHoKhachHangFields.TINH_TRANG, Constant.TinhTrangDongHoKhachHang.DANG_THIET_KE);
                new EditAsync(mMapView, mMainActivity, dongHoKHSFT, featureDHKH, new EditAsync.AsyncResponse() {
                    @Override
                    public void processFinish(Boolean isSuccess) {
                        if (isSuccess) {
                            Toast.makeText(mMainActivity, "Đã hoàn tất khảo sát", Toast.LENGTH_SHORT).show();
                            if (Popup.this.mCallout.isShowing())
                                Popup.this.mCallout.dismiss();
                        } else
                            Toast.makeText(mMainActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
                    }
                }).execute();
                dialog.dismiss();
            }
        }).setPositiveButton("Hủy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(false);

    }
    private void editLocation(ArcGISFeature feature) {
        mMainActivity.setChangingGeometry(true, feature);
        if (mCallout.isShowing())
            mCallout.dismiss();
    }

    public void updateAttachment(ArcGISFeature featureDHKH) {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        File photo = ImageFile.getFile(mMainActivity);
        Uri uri = Uri.fromFile(photo);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        dApplication.setSelectedFeature(featureDHKH);
        dApplication.setUri(uri);
        mMainActivity.startActivityForResult(cameraIntent, Constant.REQUEST.ID_UPDATE_ATTACHMENT);
    }

    private void viewAttachment(ArcGISFeature featureDHKH) {
        ViewAttachmentAsync viewAttachmentAsync = new ViewAttachmentAsync(mMainActivity, featureDHKH);
        viewAttachmentAsync.execute();
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


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txtAccept:
//            @Override
//
                break;
        }
    }
}
