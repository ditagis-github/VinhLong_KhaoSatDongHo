package vinhlong.ditagis.com.khaosatdongho.entities

class VatTu {
    var maVatTu: String? = null
    var tenVatTu: String? = null
    var donViTinh: String? = null
    private var vT: String? = null
    private var nC: String? = null
    private var mTC: String? = null
    var maHSDG: String? = null

    fun getvT(): String? {
        return vT
    }

    fun setvT(vT: String) {
        this.vT = vT
    }

    fun getnC(): String? {
        return nC
    }

    fun setnC(nC: String) {
        this.nC = nC
    }

    fun getmTC(): String? {
        return mTC
    }

    fun setmTC(mTC: String) {
        this.mTC = mTC
    }
}