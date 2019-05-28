package vinhlong.ditagis.com.capnhatdongho.utities;

import java.text.SimpleDateFormat;

/**
 * Created by ThanLe on 3/1/2018.
 */

public class Constant {
    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DDMMYYYY = new SimpleDateFormat("ddMMyyyy");

    public static final String DBDONG_HO_NUOC = "DBDongHoNuoc";
    public static final String IDMAUKIEMNGHIEM = "IDMauKiemNghiem";
    public static final String DIACHI = "DiaChi";
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
        public static final String IS_ACCESS = SERVER_API + "/Account/IsAccess/m_cndh";
    }

    public static class REQUEST {
        public static final int ID_UPDATE_ATTACHMENT = 50;
    }

    public static class LayerFields {
        public static final String OBJECTID = "OBJECTID";
    }

    public static class DefineST {
        public static final String DonViTien = " (đồng)";
    }

    public static class DongHoKhachHangFields {
        public static final String MaKhachHang = "IDMaDoiTuong";
        public static final String DBDongHoNuoc = "DBDongHoNuoc";
        public static final String TenThueBao = "TenThueBao";
        public static final String GhiChu = "GhiChu";
        public static final String NgayCapNhat = "NgayCapNhat";
        public static final String NguoiCapNhat = "NguoiCapNhat";

    }

    public static class VatTuFields {
        public static final String DBDongHo = "DBDongHo";
        public static final String MaKhachHang = "MaKhachHang";
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
        public static final String GiaVatTu = "GiaVatTu";
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
        public static final String DHKHLYR = "dhkhLYR";
        public static final String VATTUDONGHOTBL = "vattudonghoTBL";
        public static final String DMVATTUTBL = "vattuTBL";

    }

    private static Constant mInstance = null;

    public static Constant getInstance() {
        if (mInstance == null)
            mInstance = new Constant();
        return mInstance;
    }

    private Constant() {
    }


    public static class FIELD_DIEM_DANH_GIA {
        public static final String CANH_BAO_VUOT_NGUONG = "CanhBaoVuotNguong";
    }

    public static class VALUE_CANH_BAO_VUOT_NGUONG {
        public static final short VUOT = 1;
        public static final short KHONG_VUOT = 2;
    }
}
