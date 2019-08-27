package vinhlong.ditagis.com.khaosatdongho.utities

import java.text.SimpleDateFormat
import java.util.Arrays

/**
 * Created by ThanLe on 3/1/2018.
 */

class Constant private constructor() {


    //    private static final String SERVER_API = "http://113.161.88.180:798/apiv1/api";
    object AttachmentName {
        val ADD = "img_%s_%d.png"
        val UPDATE = "img_%s_%d.png"
    }
    object PreferenceKey {
        const val USERNAME = "hovaten"
        const val FULL_NAME = "ten"
        const val EMAIL = "email"
        const val PASSWORD = "sdt"
        const val USER_MANUAL = "user_manual"
        const val LOGIN_API = "preference_login_api"
    }
    object API_URL {
        val LOGIN = "$SERVER_API/Login"
        val DISPLAY_NAME = "$SERVER_API/Account/Profile"
        val LAYER_INFO = "$SERVER_API/Account/LayerInfo"
        val IS_ACCESS = "$SERVER_API/Account/IsAccess/m_gmdhks"
        val TENMAU_LIST = "$SERVER_API/gmdh/thietlapmau/laytenmau"
        val VATTU_THEOMAU = "$SERVER_API/odata/gmdh/ThietLapMaus?\$select=*&\$filter=TENMAU eq '%s'"
        val VATTU_LIST = "$SERVER_API/odata/GMDH/VatTuThietKes"
        val INSERT_VATTU = "$SERVER_API/odata/gmdh/HoSoGanMoiVatTuTKs"
        val DELETE_VATTUS = "$SERVER_API/GMDH/HoSoGanMoiVatTu/XoaVatTuTheoIDKhachHang/%s"
        val VATTUS_DONGHO = "$SERVER_API/odata/GMDH/HoSoGanMoiVatTuTKs?\$select=*&\$filter=IDKhachHang eq %d"
    }

    object METHOD {
        val DELETE = "DELETE"
        val POST = "POST"
        val GET = "GET"
    }

    object REQUEST {
        val ID_UPDATE_ATTACHMENT = 50
        val ID_UPDATE_ATTRIBUTE = 10
        val ID_UPDATE_VAT_TU = 11
    }

    object TENMAU {
        val SELECT = "Chọn thiết lập mẫu"
    }

    object LayerFields {
        val OBJECTID = "OBJECTID"
    }

    object DefineST {
        val DonViTien = " (đồng)"
    }

    object TinhTrangDongHoKhachHang {
        val DANG_KHAO_SAT = "DKS"
        val DANG_THIET_KE = "DTK"
    }

    object DongHoKhachHangFields {
        val OBJECT_ID = "OBJECTID"
        val ID = "ID"
        val TINH_TRANG = "TinhTrang"
        val CMND = "CMND"
        val TEN_KH = "TenKH"
        val GHI_CHU = "GhiChu"
        val GHI_CHU_KS = "GhiChuKS"
        val NGAY_CAP_NHAT = "TGGiaoKS"
        val NGUOI_CAP_NHAT = "NVKhaoSat"
        val SO_DIEN_THOAI = "SoDienThoai"
        val DIA_CHI = "DiaChi"
        val DIA_CHI_LAP_DAT = "DiaChiLapDat"

        val UpdateFields = Arrays.asList(TEN_KH, CMND, DIA_CHI_LAP_DAT, SO_DIEN_THOAI, GHI_CHU_KS)
        val OutFields = Arrays.asList(ID, TEN_KH, CMND, DIA_CHI, DIA_CHI_LAP_DAT, SO_DIEN_THOAI, GHI_CHU_KS)
    }


    object VatTuFields {
        val DBDongHo = "DBDongHo"
        val MaKhachHang = "CMND"
        val ID = "IDKhachHang"
        val MaVatTu = "MaVatTu"
        val SoLuong = "SoLuong"
        val NgayCapNhat = "NgayCapNhat"
        val GIA_NC = "GiaNC"
    }

    object VatTuAlias {
        val LoaiVatTu = "Loại vật tư"
    }

    object LoaiVatTuFields {
        val MaVatTu = "MaVatTu"
        val TenVatTu = "TenVatTu"
        val DonViTinh = "DonViTinh"
        val GiaVatTu = "VT"
        val GiaNC = "GiaNC"
        val GiaMay = "GiaMay"
    }

    object HanhChinhFields {
        val MAXA = "MAXA"
        val TENHANHCHINH = "TenHanhChinh"
        val MAHUYEN = "MAHUYEN"
        val TENHUYEN = "TenHuyen"
    }

    object IDLayer {
        val BASEMAP = "BASEMAP"
        val CHUYENDE = "CHUYENDE"
        val DHKHLYR = "gmdhLYR"
        val VATTUDONGHOTBL = "vattudonghoTBL"
        val DMVATTUTBL = "vattuthietkeBTL"

    }

    companion object {
        val DATE_FORMAT = SimpleDateFormat("dd/MM/yyyy")
        val DIACHI = "DiaChi"
        val EMPTY = ""
        val NGAY_CAP_NHAT = "NgayCapNhat"
        val PATH = "vlong_ksatdongho"
        val REQUEST_LOGIN = 0
        private val SERVER_API = "http://vwa.ditagis.com/api"
        val idDongHoKhachHang = 5

        private var mInstance: Constant? = null

        val instance: Constant
            get() {
                if (mInstance == null)
                    mInstance = Constant()
                return mInstance!!
            }
    }

}
