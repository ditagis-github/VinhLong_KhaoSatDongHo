package vinhlong.ditagis.com.capnhatdongho.utities;

import android.app.Activity;
import android.view.MotionEvent;

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

import vinhlong.ditagis.com.capnhatdongho.MainActivity;
import vinhlong.ditagis.com.capnhatdongho.adapter.DanhSachDongHoKHAdapter;
import vinhlong.ditagis.com.capnhatdongho.async.SingleTapAdddFeatureAsync;
import vinhlong.ditagis.com.capnhatdongho.async.SingleTapMapViewAsync;
import vinhlong.ditagis.com.capnhatdongho.entities.DApplication;


/**
 * Created by ThanLe on 2/2/2018.
 */

public class MapViewHandler extends Activity {
    private MapView mapView;
    private ServiceFeatureTable hongHoKHSFT;
    private Popup popup;
    private MainActivity mainActivity;
    private DApplication dApplication;

    public MapViewHandler(MapView mapView, MainActivity mainActivity, Popup popup) {
        this.mapView = mapView;
        this.mainActivity = mainActivity;
        this.popup = popup;
        this.dApplication = (DApplication) mainActivity.getApplication();
    }

    public void setHongHoKHSFT(ServiceFeatureTable hongHoKHSFT) {
        this.hongHoKHSFT = hongHoKHSFT;
    }

    public void addFeature() {
        Point add_point = mapView.getCurrentViewpoint(Viewpoint.Type.CENTER_AND_SCALE).getTargetGeometry().getExtent().getCenter();
        new SingleTapAdddFeatureAsync(mainActivity, mapView, this.hongHoKHSFT, feature -> {
//            this.dApplication.getMainActivity().addFeatureClose();
            if (feature != null) {
                this.popup.showPopup(feature);
            } else this.popup.dimissCallout();
        }).execute(add_point);
        ;
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
        final ListenableFuture<FeatureQueryResult> feature = hongHoKHSFT.queryFeaturesAsync(queryParameters, ServiceFeatureTable.QueryFeatureFields.LOAD_ALL);
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
        for (Field field : hongHoKHSFT.getFields()) {
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
        final ListenableFuture<FeatureQueryResult> feature = hongHoKHSFT.queryFeaturesAsync(queryParameters);
        feature.addDoneListener(() -> {
            try {
                FeatureQueryResult result = feature.get();
                Iterator iterator = result.iterator();
                while (iterator.hasNext()) {
                    Feature item = (Feature) iterator.next();
                    Map<String, Object> attributes = item.getAttributes();
                    DanhSachDongHoKHAdapter.Item dongHoKH = new DanhSachDongHoKHAdapter.Item();
                    dongHoKH.setObjectID(attributes.get(Constant.LayerFields.OBJECTID).toString());
                    Object danhboDongHo = attributes.get(Constant.DongHoKhachHangFields.DBDongHoNuoc);
                    Object tenThueBao = attributes.get(Constant.DongHoKhachHangFields.TenThueBao);
                    Object maKhachHang = attributes.get(Constant.DongHoKhachHangFields.MaKhachHang);
                    if (danhboDongHo != null)
                        dongHoKH.setDbDongHoNuoc(danhboDongHo.toString());
                    if (tenThueBao != null) {
                        dongHoKH.setTenThueBao(tenThueBao.toString());
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