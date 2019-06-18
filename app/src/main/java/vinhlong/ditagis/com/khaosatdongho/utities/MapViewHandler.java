package vinhlong.ditagis.com.khaosatdongho.utities;

import android.app.Activity;
import android.view.MotionEvent;
import android.widget.Toast;

import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.data.QueryParameters;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.geometry.GeometryEngine;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReferences;
import com.esri.arcgisruntime.mapping.Viewpoint;
import com.esri.arcgisruntime.mapping.view.MapView;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.khaosatdongho.async.EditGeometryAsync;
import vinhlong.ditagis.com.khaosatdongho.async.SingleTapAdddFeatureAsync;
import vinhlong.ditagis.com.khaosatdongho.async.SingleTapMapViewAsync;
import vinhlong.ditagis.com.khaosatdongho.entities.DApplication;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {
    private MapView mapView;
    private ServiceFeatureTable dongHoKHSFT;
    private Popup popup;
    private MainActivity mainActivity;
    private DApplication dApplication;

    public MapViewHandler(MapView mapView, MainActivity mainActivity, Popup popup) {
        this.mapView = mapView;
        this.mainActivity = mainActivity;
        this.popup = popup;
        this.dApplication = (DApplication) mainActivity.getApplication();
    }

    public void setDongHoKHSFT(ServiceFeatureTable dongHoKHSFT) {
        this.dongHoKHSFT = dongHoKHSFT;
    }

    public void addFeature() {
        Point add_point = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        new SingleTapAdddFeatureAsync(mainActivity, mapView, this.dongHoKHSFT, feature -> {
//            this.dApplication.getMainActivity().dismissPin();
            if (feature != null) {
                this.popup.showPopup(feature);
            } else this.popup.dimissCallout();
        }).execute(add_point);

    }

    public void updateGeometry(ArcGISFeature feature) {
        Point point = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();

        //Kiểm tra cùng ngày, cùng vị trí đã có sự cố nào chưa, nếu có thì cảnh báo, chưa thì thêm bình thường
        new EditGeometryAsync(mainActivity, this.dongHoKHSFT, feature, aBoolean -> {
            if (aBoolean != null && aBoolean) {
                mainActivity.setChangingGeometry(false,null);

                Toast.makeText(mainActivity, "Đổi vị trí thành công", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(mainActivity, "Có lỗi xảy ra", Toast.LENGTH_SHORT).show();
            }
        }).execute(point);
    }
    public double[] onScroll() {
        Point center = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        Geometry project = GeometryEngine.project(center, SpatialReferences.getWgs84());
        return new double[]{project.getExtent().getCenter().getX(), project.getExtent().getCenter().getY()};
    }

    public void onSingleTapMapView(MotionEvent e) {
        android.graphics.Point point = new android.graphics.Point((int) e.getX(), (int) e.getY());
        new SingleTapMapViewAsync(mainActivity, mapView, feature -> {
            if (feature != null) {
                this.popup.showPopup(feature);
            } else this.popup.dimissCallout();
        }).execute(point);

    }

    public void queryByObjectID(String objectID) {
        final QueryParameters queryParameters = new QueryParameters();
        final String query = "OBJECTID = " + objectID;
        queryParameters.setWhereClause(query);
        final ListenableFuture<FeatureQueryResult> feature = dongHoKHSFT.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                if (result.iterator().hasNext()) {
                    Feature item = result.iterator().next();
                    showPopup(item);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

    }

    private void showPopup(Feature selectedFeature) {
        if (selectedFeature != null) {
            popup.showPopup((ArcGISFeature) selectedFeature);
        }
    }

    public void querySearch(String searchStr, final DanhSachDongHoKHAdapter adapter) {
        adapter.clear();
        adapter.notifyDataSetChanged();
        QueryParameters queryParameters = new QueryParameters();
        StringBuilder builder = new StringBuilder();
        for (Field field : dongHoKHSFT.getFields()) {
            switch (field.getFieldType()) {
                case OID:
                case INTEGER:
                case SHORT:
                    try {
                        int search = Integer.parseInt(searchStr);
                        builder.append(String.format("%s = %s", field.getName(), search));
                        builder.append(" or ");
                    } catch (Exception e) {

                    }
                    break;
                case FLOAT:
                case DOUBLE:
                    try {
                        double search = Double.parseDouble(searchStr);
                        builder.append(String.format("%s = %s", field.getName(), search));
                        builder.append(" or ");
                    } catch (Exception e) {

                    }
                    break;
                case TEXT:
                    builder.append(field.getName() + " like N'%" + searchStr + "%'");
                    builder.append(" or ");
                    break;
            }
        }
        builder.append(" 1 = 2 ");
        queryParameters.setWhereClause(builder.toString());
        queryParameters.setMaxFeatures(100);
        final ListenableFuture<FeatureQueryResult> feature = dongHoKHSFT.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature item = (Feature) iterator.next();
                    Map<String, Object> attributes = item.getAttributes();
                    DanhSachDongHoKHAdapter.Item dongHoKH = new DanhSachDongHoKHAdapter.Item();
                    dongHoKH.setObjectID(attributes.get(Constant.LayerFields.OBJECTID).toString());
                    Object danhboDongHo = attributes.get(Constant.DongHoKhachHangFields.ID);
                    Object tenKH = attributes.get(Constant.DongHoKhachHangFields.TenKH);
                    Object maKhachHang = attributes.get(Constant.DongHoKhachHangFields.MaKhachHang);
                    if (danhboDongHo != null)
                        dongHoKH.setIdDongHo(danhboDongHo.toString());
                    if (tenKH != null) {
                        dongHoKH.setTenKhachHang(tenKH.toString());
                    }
                    if (maKhachHang != null) {
                        dongHoKH.setMaKhachHang(maKhachHang.toString());
                    }
                    adapter.add(dongHoKH);
                    adapter.notifyDataSetChanged();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        });

    }
}