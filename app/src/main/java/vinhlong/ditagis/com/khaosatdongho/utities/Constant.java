package vinhlong.ditagis.com.khaosatdongho.utities;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final String DIACHI = "DiaChi";
    public static final String EMPTY = "";
    public static final String NGAY_CAP_NHAT = "NgayCapNhat";
    public static final int REQUEST_LOGIN = 0;
    private static final String SERVER_API = "http://vwa.ditagis.com/api";


    //    private static final String SERVER_API = "http://113.161.88.180:798/apiv1/api";
    public class AttachmentName {
        public static final String ADD = "img_%s_%d.png";
        public static final String UPDATE = "img_%s_%d.png";
    }

    public static class API_URL {
        public static final String LOGIN = SERVER_API + "/Login";
        public static final String DISPLAY_NAME = SERVER_API + "/Account/Profile";
        public static final String LAYER_INFO = SERVER_API + "/Account/LayerInfo";
        public static final String IS_ACCESS = SERVER_API + "/Account/IsAccess/m_gmdhks";
        public static final String TENMAU_LIST = SERVER_API + "/gmdh/thietlapmau/laytenmau";
        public static final String VATTU_THEOMAU = SERVER_API + "/odata/gmdh/ThietLapMaus?$select=*&$filter=TENMAU eq '%s'";
        public static final String VATTU_LIST = SERVER_API + "/odata/GMDH/VatTuThietKes";
        public static final String INSERT_VATTU = SERVER_API + "/odata/gmdh/HoSoGanMoiVatTuTKs";
        public static final String DELETE_VATTUS = SERVER_API + "/GMDH/HoSoGanMoiVatTu/XoaVatTuTheoIDKhachHang/%s";
        public static final String VATTUS_DONGHO = SERVER_API + "/odata/GMDH/HoSoGanMoiVatTuTKs?$select=*&$filter=IDKhachHang eq '%s'";
    }
    public static class METHOD {
        public static final String DELETE = "DELETE";
        public static final String POST = "POST";
        public static final String GET = "GET";
    }
    public static class REQUEST {
        public static final int ID_UPDATE_ATTACHMENT = 50;
        public static final int ID_UPDATE_ATTRIBUTE = 10;
    }
    public static class TENMAU {
        public static final String SELECT = "Chọn thiết lập mẫu";
    }
    public static class LayerFields {
        public static final String OBJECTID = "OBJECTID";
    }

    public static class DefineST {
        public static final String DonViTien = " (đồng)";
    }

    public static class TinhTrangDongHoKhachHang {
        public static final String DANG_KHAO_SAT = "DKS";
        public static final String DANG_THIET_KE = "DTK";
    }
    public static class DongHoKhachHangFields {
        public static final String OBJECT_ID = "OBJECTID";
        public static final String ID = "ID";
        public static final String TINH_TRANG = "TinhTrang";
        public static final String CMND = "CMND";
        public static final String TEN_KH = "TenKH";
        public static final String GHI_CHU = "GhiChu";
        public static final String GHI_CHU_KS = "GhiChuKS";
        public static final String NGAY_CAP_NHAT = "TGGiaoKS";
        public static final String NGUOI_CAP_NHAT = "NVKhaoSat";
        public static final String SO_DIEN_THOAI = "SoDienThoai";
        public static final String DIA_CHI = "DiaChi";
        public static final String DIA_CHI_LAP_DAT = "DiaChiLapDat";

        public static final List<String> UpdateFields = Arrays.asList(TEN_KH, CMND, DIA_CHI_LAP_DAT, SO_DIEN_THOAI, GHI_CHU_KS);
        public static final List<String> OutFields = Arrays.asList(ID, TEN_KH, CMND, DIA_CHI, DIA_CHI_LAP_DAT, SO_DIEN_THOAI, GHI_CHU_KS);
    }


    public static class VatTuFields {
        public static final String DBDongHo = "DBDongHo";
        public static final String MaKhachHang = "CMND";
        public static final String MaVatTu = "MaVatTu";
        public static final String SoLuong = "SoLuong";
        public static final String NgayCapNhat = "NgayCapNhat";
    }

    public static class VatTuAlias {
        public static final String LoaiVatTu = "Loại vật tư";
    }

    public static class LoaiVatTuFields {
        public static final String MaVatTu = "MaVatTu";
        public static final String TenVatTu = "TenVatTu";
        public static final String DonViTinh = "DonViTinh";
        public static final String GiaVatTu = "VT";
        public static final String GiaNC = "GiaNC";
        public static final String GiaMay = "GiaMay";
    }
    public static class HanhChinhFields {
        public static final String MAXA = "MAXA";
        public static final String TENHANHCHINH = "TenHanhChinh";
        public static final String MAHUYEN = "MAHUYEN";
        public static final String TENHUYEN = "TenHuyen";
    }
    public static final int idDongHoKhachHang = 5;

    public static class IDLayer {
        public static final String BASEMAP = "BASEMAP";
        public static final String CHUYENDE = "CHUYENDE";
        public static final String DHKHLYR = "gmdhLYR";
        public static final String VATTUDONGHOTBL = "vattudonghoTBL";
        public static final String DMVATTUTBL = "vattuthietkeBTL";

    }

    private static Constant mInstance = null;

    public static Constant getInstance() {
        if (mInstance == null)
            mInstance = new Constant();
        return mInstance;
    }

    private Constant() {

    }

}
