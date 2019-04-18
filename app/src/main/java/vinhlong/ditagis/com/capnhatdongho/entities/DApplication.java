package vinhlong.ditagis.com.capnhatdongho.entities;

import android.app.Application;
import android.location.Location;

import com.esri.arcgisruntime.data.ArcGISFeature;
import com.esri.arcgisruntime.geometry.Geometry;
import com.esri.arcgisruntime.layers.FeatureLayer;


import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import vinhlong.ditagis.com.capnhatdongho.Editing.EditingVatTu;
import vinhlong.ditagis.com.capnhatdongho.entities.entitiesDB.User;
import vinhlong.ditagis.com.capnhatdongho.libs.FeatureLayerDTG;
import vinhlong.ditagis.com.capnhatdongho.utities.Constant;

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
}