package vinhlong.ditagis.com.khaosatdongho.entities;

import android.app.Application;
import android.net.Uri;

import com.esri.arcgisruntime.data.ArcGISFeature;

import vinhlong.ditagis.com.khaosatdongho.Editing.EditingVatTu;
import vinhlong.ditagis.com.khaosatdongho.MainActivity;
import vinhlong.ditagis.com.khaosatdongho.entities.entitiesDB.User;
import vinhlong.ditagis.com.khaosatdongho.libs.FeatureLayerDTG;

public class DApplication extends Application {
    private FeatureLayerDTG dongHoKHDTG;
    private FeatureLayerDTG vatTuDHDTG;
    private EditingVatTu editingVatTu;

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

    public EditingVatTu getEditingVatTu() {
        return editingVatTu;
    }

    public void setEditingVatTu(EditingVatTu editingVatTu) {
        this.editingVatTu = editingVatTu;
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