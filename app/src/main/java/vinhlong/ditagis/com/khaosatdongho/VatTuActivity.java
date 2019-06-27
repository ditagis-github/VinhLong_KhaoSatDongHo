package vinhlong.ditagis.com.khaosatdongho;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.mapping.view.MapView;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.adapter.ChiTietVatTuAdapter;
import vinhlong.ditagis.com.khaosatdongho.adapter.VatTuApdapter;
import vinhlong.ditagis.com.khaosatdongho.async.NotifyVatTuDongHoAdapterChangeAsync;
import vinhlong.ditagis.com.khaosatdongho.async.QueryDMVatTuAsync;
import vinhlong.ditagis.com.khaosatdongho.async.RefreshVatTuAsync;
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.AddVatTuKHAsycn;
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.DeleteVatTuKHAsycn;
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.QueryDanhSachVatTuAsycn;
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.QueryTenMauAsycn;
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.QueryVatTuDongHoAsycn;
import vinhlong.ditagis.com.khaosatdongho.async.ServerAsync.QueryVatTuTheoMauAsycn;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;
import vinhlong.ditagis.com.khaosatdongho.utities.Constant;
import vinhlong.ditagis.com.khaosatdongho.utities.MySnackBar;

public class VatTuActivity extends AppCompatActivity {
    private VatTuApdapter vatTuApdapter;
    private List<Feature> table_feature;
    private ArcGISFeature feature;
    private ArrayList<Feature> dmVatTuFeatures;
    private ArrayAdapter mAdapter;
    private MapView mapView;
    private TextView titlePopup;
    List<String> tenMaus;
    ArrayList<QueryDanhSachVatTuAsycn.VatTu> danhSachVatTu;
    private DApplication mApplication;
    public NumberFormat formatter = new DecimalFormat("###,###,###");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vat_tu);
        mApplication = (DApplication) getApplication();
        new QueryDMVatTuAsync(VatTuActivity.this, mApplication.getDmVatTuKHSFT(), features -> {
            this.dmVatTuFeatures = features;
        }).execute();
        this.getMauThietLaps();
        this.getDanhSachVatTu();


    }

    private void getMauThietLaps() {
        tenMaus = new ArrayList<>();
        new QueryTenMauAsycn(VatTuActivity.this, danhsachtenmau -> {
            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(danhsachtenmau);
                tenMaus.add(Constant.TENMAU.SELECT);
                for (int i = 0; i < jsonArray.length(); i++) {
                    tenMaus.add(jsonArray.get(i).toString());
                }
                showDanhSachVatTu(mApplication.getSelectedFeature());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }).execute();
    }

    private void getVatTu(long maKhachHang) {
        new QueryVatTuDongHoAsycn(VatTuActivity.this, vatTus -> {
            if (vatTus != null) {
                for (VatTuApdapter.VatTu vatTu : vatTus) {
                    for (QueryDanhSachVatTuAsycn.VatTu vatTu1 : danhSachVatTu) {
                        if (vatTu.getMaVatTu().equals(vatTu1.getMaVatTu())) {
                            vatTu.setTenVatTu(vatTu1.getTenVatTu());
                            vatTu.setDonViTinh(vatTu1.getDonViTinh());
                            break;
                        }
                    }
                }
                vatTuApdapter.setVatTus(vatTus);
                vatTuApdapter.notifyDataSetChanged();
            }
        }).execute(maKhachHang);
    }

    private void getVatTuTheoMau(String tenThietLapMau, Long idDongHo) {
        ArrayList<VatTuApdapter.VatTu> vatTusAdapter = new ArrayList<>();
        new QueryVatTuTheoMauAsycn(VatTuActivity.this, vatTuTheoMau -> {
            if (vatTuTheoMau != null) {
                for (int i = 0; i < vatTuTheoMau.size(); i++) {
                    VatTuApdapter.VatTu vatTuAdapter = new VatTuApdapter.VatTu(i + 1);
                    QueryVatTuTheoMauAsycn.VatTu vatTu = vatTuTheoMau.get(i);
                    String maVatTu = vatTu.getMaVatTu();
                    if (maVatTu != null) {
                        QueryDanhSachVatTuAsycn.VatTu loaiVatTu = getLoaiVatTu(maVatTu);
                        vatTuAdapter.setMaVatTu(maVatTu);
                        vatTuAdapter.setTenVatTu(loaiVatTu.getTenVatTu());
                        vatTuAdapter.setSoLuongVatTu(vatTu.getSoLuong());
                        vatTuAdapter.setGiaNC(loaiVatTu.getnC());
                        vatTuAdapter.setGiaVT(loaiVatTu.getvT());
                        vatTuAdapter.setDonViTinh(loaiVatTu.getDonViTinh());
                        vatTuAdapter.setiDKhachHang(idDongHo);
                        vatTusAdapter.add(vatTuAdapter);
                    }
                }
                vatTuApdapter.setVatTus(vatTusAdapter);
                vatTuApdapter.notifyDataSetChanged();
            }
        }).execute(tenThietLapMau);
    }

    private QueryDanhSachVatTuAsycn.VatTu getLoaiVatTu(String maVatTu) {
        if (maVatTu != null) {
            for (QueryDanhSachVatTuAsycn.VatTu vatTu : this.danhSachVatTu) {
                if (vatTu.getMaVatTu().equals(maVatTu)) {
                    return vatTu;
                }
            }
        }
        return null;
    }

    private void getDanhSachVatTu() {
        new QueryDanhSachVatTuAsycn(VatTuActivity.this, danhSachVatTu -> {
            if (danhSachVatTu != null) {
                this.danhSachVatTu = danhSachVatTu;
            }
        }).execute();
    }


    public void showDanhSachVatTu(ArcGISFeature featureDHKH) {
        this.feature = featureDHKH;
        final Map<String, Object> attributes = featureDHKH.getAttributes();
        Object idDongHo = attributes.get(Constant.DongHoKhachHangFields.ID);
//        idDongHo = 12345;
        if (idDongHo != null) {

            Spinner spin_thietlapmau = findViewById(R.id.spin_thietlapmau);
            mAdapter = new ArrayAdapter<>(VatTuActivity.this, android.R.layout.simple_spinner_dropdown_item, tenMaus);
            spin_thietlapmau.setAdapter(mAdapter);
            TextView txtAdd = findViewById(R.id.txtAccept);
            spin_thietlapmau.setSelection(0);
            Object finalIdDongHo = idDongHo;
            spin_thietlapmau.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    Object itemAtPosition = spin_thietlapmau.getItemAtPosition(position);
                    if (itemAtPosition != null) {
                        String tenThietLapMau = itemAtPosition.toString();
                        if (!tenThietLapMau.equals(Constant.TENMAU.SELECT)) {
                            getVatTuTheoMau(tenThietLapMau, ((Integer) finalIdDongHo).longValue());
                        } else {
                            vatTuApdapter.clear();
                            vatTuApdapter.notifyDataSetChanged();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    // your code here
                }

            });
            ListView listView = findViewById(R.id.listview);

            if (!mApplication.getVatTuDHDTG().getAction().isCreate()) {
                txtAdd.setVisibility(View.INVISIBLE);
            }
            txtAdd.setOnClickListener(v -> addTableVatTu(v, (String) spin_thietlapmau.getSelectedItem()));
            listView.setOnItemClickListener((parent, view, position, id) -> {
                if (mApplication.getVatTuDHDTG().getAction().isView()) {
//                    final VatTuApdapter.VatTu itemAtPosition = vatTuApdapter.getVatTus().get(position);
//                    String objectid = itemAtPosition.getMaVatTu();
//                    QueryParameters queryParameters = new QueryParameters();
//                    String queryClause = Constant.LayerFields.OBJECTID + " = " + objectid;
//                    queryParameters.setWhereClause(queryClause);
//                    final ListenableFuture<FeatureQueryResult> queryResultListenableFuture = mApplication.getVatTuKHSFT().queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
//                    queryResultListenableFuture.addDoneListener(new Runnable() {
//                        @Override
//                        public void run() {
//                            try {
//                                FeatureQueryResult result = queryResultListenableFuture.get();
//                                Iterator iterator = result.iterator();
//
//                                if (iterator.hasNext()) {
//                                    Feature feature = (Feature) iterator.next();
//                                    showInfosSelectedItem(feature);
//                                }
//
//                            } catch (InterruptedException e) {
//                                e.printStackTrace();
//                            } catch (ExecutionException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    });
                }
            });
            ArrayList<VatTuApdapter.VatTu> vatTus = new ArrayList<>();
            vatTuApdapter = new VatTuApdapter(VatTuActivity.this, vatTus);
            listView.setAdapter(vatTuApdapter);
//            getRefreshTableVatTuAsync();
            getVatTu(((Integer) idDongHo).longValue());

        } else {
            MySnackBar.make(mapView, VatTuActivity.this.getString(R.string.DATA_NOT_FOUND), true);
            return;
        }
    }

    private Feature getLoaiVatTu_Ma(String maVatTu) {
        if (maVatTu != null) {
            for (Feature feature : this.dmVatTuFeatures) {
                Map<String, Object> attributes = feature.getAttributes();
                Object maVT = attributes.get(Constant.LoaiVatTuFields.MaVatTu);
                if (maVT != null && maVT.toString().equals(maVatTu)) {
                    return feature;
                }
            }
        }
        return null;
    }

    private Feature getLoaiVatTu_Ten(String tenVatTu) {
        if (tenVatTu != null) {
            for (Feature feature : this.dmVatTuFeatures) {
                Map<String, Object> attributes = feature.getAttributes();
                Object tenVT = attributes.get(Constant.LoaiVatTuFields.TenVatTu);
                if (tenVT != null && tenVT.toString().equals(tenVatTu)) {
                    return feature;
                }
            }
        }
        return null;
    }

    private void showInfosSelectedItem(Feature selectedFeature) {
        Map<String, Object> attributes = selectedFeature.getAttributes();
        View layout_chitiet_vattudongho = VatTuActivity.this.getLayoutInflater().inflate(R.layout.layout_title_listview, null);
        ListView listview_chitiet_maudanhgia = layout_chitiet_vattudongho.findViewById(R.id.listview);
        if (attributes.get(Constant.VatTuFields.DBDongHo) != null) {
            ((TextView) layout_chitiet_vattudongho.findViewById(R.id.txtTitle)).setText(attributes.get(Constant.VatTuFields.DBDongHo).toString());
        } else {
            ((TextView) layout_chitiet_vattudongho.findViewById(R.id.txtTitle)).setText(VatTuActivity.this.getString(R.string.title_chitietvattu));
        }
        final List<ChiTietVatTuAdapter.Item> items = new ArrayList<>();
        List<Field> fields = mApplication.getVatTuKHSFT().getFields();
        final String[] updateFields = mApplication.getVatTuDHDTG().getUpdateFields();
        String[] unedit_Fields = VatTuActivity.this.getResources().getStringArray(R.array.unedit_VT_Fields);
        for (Field field : fields) {
            ChiTietVatTuAdapter.Item item = new ChiTietVatTuAdapter.Item();
            item.setAlias(field.getAlias());
            item.setFieldName(field.getName());
            item.setFieldType(field.getFieldType());
            if (field.getName().equals(Constant.VatTuFields.MaVatTu)) {
                item.setAlias(Constant.VatTuAlias.LoaiVatTu);
            }
            Object value = attributes.get(field.getName());
            if (value != null) {
                if (field.getName().equals(Constant.VatTuFields.MaVatTu)) {
                    Feature loaiVatTu = getLoaiVatTu_Ma(value.toString());
                    if (loaiVatTu != null) {
                        item.setValue(loaiVatTu.getAttributes().get(Constant.LoaiVatTuFields.TenVatTu).toString());
                    }
                } else if (field.getDomain() != null) {
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
            if (this.mApplication.getVatTuDHDTG().getAction().isEdit()) {
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
        ChiTietVatTuAdapter chiTietVatTuAdapter = new ChiTietVatTuAdapter(VatTuActivity.this, items);
        if (items != null) listview_chitiet_maudanhgia.setAdapter(chiTietVatTuAdapter);
        AlertDialog.Builder builder = new AlertDialog.Builder(VatTuActivity.this, android.R.style.Theme_Holo_Light_NoActionBar_Fullscreen);
        builder.setView(layout_chitiet_vattudongho);
        if (this.mApplication.getVatTuDHDTG().getAction().isEdit()) {
            builder.setPositiveButton(VatTuActivity.this.getString(R.string.btn_Accept), null);
        }
        if (this.mApplication.getVatTuDHDTG().getAction().isDelete()) {
            builder.setNegativeButton(VatTuActivity.this.getString(R.string.btn_Delete), null);
        }
        builder.setNeutralButton(VatTuActivity.this.getString(R.string.btn_Esc), null);
        listview_chitiet_maudanhgia.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (mApplication.getVatTuDHDTG().getAction().isEdit()) {
                    editValueAttribute(parent, view, position, id);
                }
            }
        });
        final AlertDialog dialog = builder.create();
        builder.setPositiveButton(android.R.string.ok, null);
        dialog.show();
        // Chỉnh sửa
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            for (ChiTietVatTuAdapter.Item item : items) {
                Domain domain = mApplication.getVatTuKHSFT().getField(item.getFieldName()).getDomain();
                Object codeDomain = null;
                if (item.getFieldName().equals(Constant.VatTuFields.NgayCapNhat)) {
                    Calendar currentTime = Calendar.getInstance();
                    item.setValue(Constant.DATE_FORMAT.format((currentTime).getTime()));
                } else if (domain != null) {
                    List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                    codeDomain = getCodeDomain(codedValues, item.getValue());
                }
                if (item.getFieldName().equals(Constant.VatTuFields.MaVatTu)) {
                    Feature loaiVatTu_ten = getLoaiVatTu_Ten(item.getValue());
                    if (loaiVatTu_ten != null)
                        selectedFeature.getAttributes().put(item.getFieldName(), loaiVatTu_ten.getAttributes().get(Constant.LoaiVatTuFields.MaVatTu).toString());
                } else {
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
            }
            chiTietVatTuAdapter.notifyDataSetChanged();
            Calendar currentTime = Calendar.getInstance();
            selectedFeature.getAttributes().put(Constant.VatTuFields.NgayCapNhat, currentTime);
            updateFeature(selectedFeature);
        });
        // Xóa
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(v -> {
            deleteFeature(selectedFeature);
            dialog.dismiss();
        });
        // Thoát
        dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> dialog.dismiss());

        capNhatDanhBo(selectedFeature, dialog);
    }

    private void getRefreshTableVatTuAsync() {
        final Map<String, Object> attributes = this.feature.getAttributes();
        long idDongHo = (Integer) attributes.get(Constant.DongHoKhachHangFields.ID);
        new RefreshVatTuAsync(VatTuActivity.this, mApplication.getVatTuKHSFT(), this.dmVatTuFeatures, vatTuApdapter, this.mApplication.getVatTuDHDTG().getAction(), features -> {
            table_feature = features;
            if (this.titlePopup != null && features != null) {
                double sum = 0;
                for (Feature feature : features) {
                    Object maVatTu = feature.getAttributes().get(Constant.VatTuFields.MaVatTu);
                    Object soLuong = feature.getAttributes().get(Constant.VatTuFields.SoLuong);
                    if (maVatTu != null && soLuong != null) {
                        Feature loaiVatTu = getLoaiVatTu12(maVatTu.toString());
                        if (loaiVatTu != null) {
                            Object giaVatTu = loaiVatTu.getAttributes().get(Constant.LoaiVatTuFields.GiaVatTu);
                            if (giaVatTu != null) {
                                try {
                                    double giaVT = Double.parseDouble(giaVatTu.toString());
                                    int soLuongVT = Integer.parseInt(soLuong.toString());
                                    sum += soLuongVT * giaVT;
                                } catch (Exception e) {
                                }
                            }
                        }
                    }
                }
                titlePopup.setText(formatter.format(sum) + Constant.DefineST.DonViTien);
            }
        }).execute(idDongHo);
    }

    private Feature getLoaiVatTu12(String maVatTu) {
        if (maVatTu != null) {
            for (Feature feature : this.dmVatTuFeatures) {
                Map<String, Object> attributes = feature.getAttributes();
                Object maVT = attributes.get(Constant.VatTuFields.MaVatTu);
                if (maVT != null && maVT.toString().equals(maVatTu)) {
                    return feature;
                }
            }
        }
        return null;
    }

    private void capNhatDanhBo(Feature selectedFeature, AlertDialog alertDialog) {
        final Map<String, Object> attributes = feature.getAttributes();
        Object dBDongHoNuoc = attributes.get(Constant.DongHoKhachHangFields.ID);
        Object dbDongHoVatTu = selectedFeature.getAttributes().get(Constant.VatTuFields.DBDongHo);
        if (dBDongHoNuoc != null && dbDongHoVatTu != null && !dbDongHoVatTu.equals(dBDongHoNuoc)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(VatTuActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle("Danh bộ của đồng hồ nước khác với danh bộ vật tư");
            builder.setMessage(R.string.question_capnhatdanhbo);
            builder.setPositiveButton("Có", (dialog, which) -> {
                Calendar currentTime = Calendar.getInstance();
                selectedFeature.getAttributes().put(Constant.VatTuFields.NgayCapNhat, currentTime);
                selectedFeature.getAttributes().put(Constant.VatTuFields.DBDongHo, dBDongHoNuoc.toString());
                updateFeature(selectedFeature);
                dialog.dismiss();
                alertDialog.dismiss();
                showInfosSelectedItem(selectedFeature);
            }).setNegativeButton("Không", (dialog, which) -> dialog.dismiss()).setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.show();
        }
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

    private void addTableVatTu(View v, String item) {
        if (item.equals(Constant.TENMAU.SELECT)) {
            Snackbar.make(v, "Vui lòng chọn mẫu!", 2000).show();
        } else {
            new DeleteVatTuKHAsycn(VatTuActivity.this, output -> {
                if (output) {
                    new AddVatTuKHAsycn(VatTuActivity.this, new AddVatTuKHAsycn.AsyncResponse() {
                        @Override
                        public void processFinish(Boolean success) {
                            if (success) {
                                Toast.makeText(VatTuActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                            } else Snackbar.make(v, "Cật nhật thất bại!", 2000).show();
                        }
                    }).execute(vatTuApdapter.getVatTus());
                }
            }).execute(vatTuApdapter.getVatTus().get(0).getiDKhachHang());

        }

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
        ListenableFuture<Void> mapViewResult = mApplication.getVatTuKHSFT().addFeatureAsync(feature);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mApplication.getVatTuKHSFT().applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        Toast.makeText(VatTuActivity.this.getApplicationContext(), VatTuActivity.this.getString(R.string.DATA_SUCCESSFULLY_INSERTED), Toast.LENGTH_SHORT).show();
                        getRefreshTableVatTuAsync();
                    } else {
                        Toast.makeText(VatTuActivity.this.getApplicationContext(), VatTuActivity.this.getString(R.string.FAILED_TO_INSERT_DATA), Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            });
        });
    }


    private void deleteFeature(Feature feature) {
        final ListenableFuture<Void> mapViewResult = mApplication.getVatTuKHSFT().deleteFeatureAsync(feature);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mApplication.getVatTuKHSFT().applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        Toast.makeText(VatTuActivity.this.getApplicationContext(), VatTuActivity.this.getString(R.string.DATA_SUCCESSFULLY_DELETED), Toast.LENGTH_SHORT).show();
                        getRefreshTableVatTuAsync();
                    } else {
                        Toast.makeText(VatTuActivity.this.getApplicationContext(), VatTuActivity.this.getString(R.string.FAILED_TO_DELETE_DATA), Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            });
        });
    }

    private void updateFeature(final Feature feature) {
        final ListenableFuture<Void> mapViewResult = mApplication.getVatTuKHSFT().updateFeatureAsync(feature);
        mapViewResult.addDoneListener(() -> {
            final ListenableFuture<List<FeatureEditResult>> listListenableEditAsync = mApplication.getVatTuKHSFT().applyEditsAsync();
            listListenableEditAsync.addDoneListener(() -> {
                try {
                    List<FeatureEditResult> featureEditResults = listListenableEditAsync.get();
                    if (featureEditResults.size() > 0) {
                        Toast.makeText(VatTuActivity.this.getApplicationContext(), VatTuActivity.this.getString(R.string.DATA_SUCCESSFULLY_UPDATED), Toast.LENGTH_SHORT).show();
                        getRefreshTableVatTuAsync();
                    } else {
                        Toast.makeText(VatTuActivity.this.getApplicationContext(), VatTuActivity.this.getString(R.string.FAILED_TO_UPDATE_DATA), Toast.LENGTH_SHORT).show();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            });
        });
    }

    private void editValueAttribute(final AdapterView<?> parent, View view, int position, final long id) {
        ChiTietVatTuAdapter.Item item = (ChiTietVatTuAdapter.Item) parent.getItemAtPosition(position);
        if (item.isEdit()) {
            final Calendar[] calendar = new Calendar[1];
            final AlertDialog.Builder builder = new AlertDialog.Builder(VatTuActivity.this, android.R.style.Theme_Material_Light_Dialog_Alert);
            builder.setTitle("Cập nhật thuộc tính");
            builder.setMessage(item.getAlias());
            builder.setCancelable(false).setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            LinearLayout layout = (LinearLayout) VatTuActivity.this.getLayoutInflater().
                    inflate(R.layout.layout_dialog_update_feature_listview, null);
            builder.setView(layout);
            final FrameLayout layoutTextView = layout.findViewById(R.id.layout_edit_viewmoreinfo_TextView);
            final TextView textView = layout.findViewById(R.id.txt_edit_viewmoreinfo);
            ImageView img_selectTime = layout.findViewById(R.id.img_selectTime);
            LinearLayout layoutEditText = layout.findViewById(R.id.layout_edit_viewmoreinfo_Editext);
            EditText editText = layout.findViewById(R.id.etxt_edit_viewmoreinfo);
            LinearLayout layoutSpin = layout.findViewById(R.id.layout_edit_viewmoreinfo_Spinner);
            Spinner spin = layout.findViewById(R.id.spin_edit_viewmoreinfo);
            AutoCompleteTextView autoCompleteTextView = layout.findViewById(R.id.autoCompleteTextView);
            Field field = mApplication.getVatTuKHSFT().getField(item.getFieldName());
            Domain domain = field.getDomain();
            if (field.getName().equals(Constant.VatTuFields.MaVatTu)) {
                layout.findViewById(R.id.layout_edit_viewmoreinfo_AutoComplete).setVisibility(View.VISIBLE);
                if (this.dmVatTuFeatures != null) {
                    List<String> loaiVatTu = new ArrayList<>();
                    for (Feature feature : this.dmVatTuFeatures)
                        loaiVatTu.add(feature.getAttributes().get(Constant.LoaiVatTuFields.TenVatTu).toString());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, loaiVatTu);
                    autoCompleteTextView.setThreshold(1);
                    autoCompleteTextView.setAdapter(adapter);
                    if (item.getValue() != null)
                        autoCompleteTextView.setText(item.getValue());
                }
            } else if (domain != null) {
                layoutSpin.setVisibility(View.VISIBLE);
                List<CodedValue> codedValues = ((CodedValueDomain) domain).getCodedValues();
                if (codedValues != null) {
                    List<String> codes = new ArrayList<>();
                    for (CodedValue codedValue : codedValues)
                        codes.add(codedValue.getName());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(layout.getContext(), android.R.layout.simple_list_item_1, codes);
                    spin.setAdapter(adapter);
                    if (item.getValue() != null) spin.setSelection(codes.indexOf(item.getValue()));
                }
            } else switch (item.getFieldType()) {
                case DATE:
                    layoutTextView.setVisibility(View.VISIBLE);
                    textView.setText(item.getValue());
                    img_selectTime.setOnClickListener(v -> {
                        final View dialogView = View.inflate(VatTuActivity.this, R.layout.date_time_picker, null);
                        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(VatTuActivity.this).create();
                        dialogView.findViewById(R.id.date_time_set).setOnClickListener(view1 -> {
                            DatePicker datePicker = dialogView.findViewById(R.id.date_picker);
                            calendar[0] = new GregorianCalendar(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            String date = String.format("%02d/%02d/%d", datePicker.getDayOfMonth(), datePicker.getMonth() + 1, datePicker.getYear());
                            textView.setText(date);
                            alertDialog.dismiss();
                        });
                        alertDialog.setView(dialogView);
                        alertDialog.show();
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
                    if (field.getName().equals(Constant.VatTuFields.MaVatTu)) {
                        Editable text = autoCompleteTextView.getText();
                        if (text != null) {
                            Feature feature = getLoaiVatTu_Ten(text.toString());
                            if (feature != null)
                                item.setValue(text.toString());
                            else
                                Toast.makeText(VatTuActivity.this, VatTuActivity.this.getString(R.string.INCORRECT_INPUT_FORMAT_WITH_TEXT), Toast.LENGTH_LONG).show();
                        }
                    } else if (domain != null) {
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
                                    Toast.makeText(VatTuActivity.this, VatTuActivity.this.getString(R.string.INCORRECT_INPUT_FORMAT), Toast.LENGTH_LONG).show();
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
                                    Toast.makeText(VatTuActivity.this, VatTuActivity.this.getString(R.string.INCORRECT_INPUT_FORMAT), Toast.LENGTH_LONG).show();
                                }
                                break;
                        }
                    }
                    ChiTietVatTuAdapter adapter = (ChiTietVatTuAdapter) parent.getAdapter();
                    new NotifyVatTuDongHoAdapterChangeAsync(VatTuActivity.this).execute(adapter);
                }
            });
            builder.setView(layout);
            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }

}
