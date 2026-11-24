
public class LinhKien extends SanPham {
    protected String loaiLinhKien;
    protected String hangSanXuat;

    public String getLoaiLinhKien() {
        return loaiLinhKien;
    }

    public void setLoaiLinhKien(String loaiLinhKien) {
        this.loaiLinhKien = loaiLinhKien;
    }

    public String getHangSanXuat() {
        return hangSanXuat;
    }

    public void setHangSanXuat(String hangSanXuat) {
        this.hangSanXuat = hangSanXuat;
    }


    public LinhKien() {
        super();
        this.loaiLinhKien = "";
        this.hangSanXuat = "";
    }

    public LinhKien(String maSP, String tenSP, double donGia, int soLuong, String loaiLinhKien, String hangSanXuat) {
        super(maSP, tenSP, donGia, soLuong);
        this.loaiLinhKien = loaiLinhKien;
        this.hangSanXuat = hangSanXuat;
    }

    @Override
    public  double tinhGiaBan(){
        return getDonGia();
    }
}
