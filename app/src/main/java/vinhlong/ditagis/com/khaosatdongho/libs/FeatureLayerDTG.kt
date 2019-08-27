package vinhlong.ditagis.com.khaosatdongho.libs


import com.esri.arcgisruntime.layers.FeatureLayer

/**
 * Created by NGUYEN HONG on 3/14/2018.
 */

class FeatureLayerDTG(val featureLayer: FeatureLayer, var titleLayer: String?, var action: Action?) {
    var addFields: Array<String>? = null
    var updateFields: Array<String>? = null
    var queryFields: Array<String>? = null
    var outFields: Array<String>? = null
}
