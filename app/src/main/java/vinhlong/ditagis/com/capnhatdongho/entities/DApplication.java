package vinhlong.ditagis.com.capnhatdongho.entities;

import android.app.Application;
import android.location.Location;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.Geometry;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import vinhlong.ditagis.com.capnhatdongho.entities.entitiesDB.User;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;

public class DApplication extends Application {



    private User userDangNhap;

    public User getUserDangNhap() {
        return userDangNhap;
    }

    public void setUserDangNhap(User userDangNhap) {
        this.userDangNhap = userDangNhap;
    }


    private short loaiVatTu;

    public short getLoaiVatTu() {
        return loaiVatTu;
    }

    public void setLoaiVatTu(short loaiVatTu) {
        this.loaiVatTu = loaiVatTu;
    }


    private Geometry geometry;

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    private ArcGISFeature arcGISFeature;

    public ArcGISFeature getArcGISFeature() {
        return arcGISFeature;
    }

    public void setArcGISFeature(ArcGISFeature arcGISFeature) {
        this.arcGISFeature = arcGISFeature;
    }


    private Location mLocation;

    public Location getmLocation() {
        return mLocation;
    }

    public void setmLocation(Location mLocation) {
        this.mLocation = mLocation;
    }
}