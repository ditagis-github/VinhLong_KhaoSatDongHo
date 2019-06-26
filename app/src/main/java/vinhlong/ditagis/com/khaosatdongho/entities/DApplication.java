package vinhlong.ditagis.com.khaosatdongho.entities;

import android.app.Application;
import android.net.Uri;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.data.ServiceFeatureTable;

import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User;
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG;

public class DApplication extends Application {


    private FeatureLayerDTG dongHoKHDTG;
    private FeatureLayerDTG vatTuDHDTG;
    private ServiceFeatureTable dongHoKHSFT;
    private ServiceFeatureTable vatTuKHSFT;
    private ServiceFeatureTable dmVatTuKHSFT;

    public ServiceFeatureTable getDmVatTuKHSFT() {
        return dmVatTuKHSFT;
    }

    public void setDmVatTuKHSFT(ServiceFeatureTable dmVatTuKHSFT) {
        this.dmVatTuKHSFT = dmVatTuKHSFT;
    }

    public ServiceFeatureTable getVatTuKHSFT() {
        return vatTuKHSFT;
    }

    public void setVatTuKHSFT(ServiceFeatureTable vatTuKHSFT) {
        this.vatTuKHSFT = vatTuKHSFT;
    }

    public ServiceFeatureTable getDongHoKHSFT() {
        return dongHoKHSFT;
    }

    public void setDongHoKHSFT(ServiceFeatureTable dongHoKHSFT) {
        this.dongHoKHSFT = dongHoKHSFT;
    }

    public FeatureLayerDTG getDongHoKHDTG() {
        return dongHoKHDTG;
    }

    public void setDongHoKHDTG(FeatureLayerDTG dongHoKHDTG) {
        this.dongHoKHDTG = dongHoKHDTG;
    }

    public FeatureLayerDTG getVatTuDHDTG() {
        return vatTuDHDTG;
    }

    public void setVatTuDHDTG(FeatureLayerDTG vatTuDHDTG) {
        this.vatTuDHDTG = vatTuDHDTG;
    }


    // user
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private MainActivity mainActivity;

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    // URI
    private Uri uri;
    private ArcGISFeature selectedFeature;

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public ArcGISFeature getSelectedFeature() {
        return selectedFeature;
    }

    public void setSelectedFeature(ArcGISFeature selectedFeature) {
        this.selectedFeature = selectedFeature;
    }

}