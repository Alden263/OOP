package dean;
public class NhanVien extends Nguoi{
    private String chucVu;
    private double luong;
    private TaiKhoan taiKhoan;
    public NhanVien(){
        super();
        chucVu = "";
        luong = 0;
        taiKhoan = null;
    }
    public NhanVien(String ma, String hoTen, String soDienThoai, String chucVu, double luong, TaiKhoan taiKhoan){
        super(ma, hoTen, soDienThoai);
        this.chucVu = chucVu;
        this.luong = luong;
        this.taiKhoan = taiKhoan;
    }
    public String getChucVu(){
        return chucVu;
    }
    public void setChucVu(String chucVu){
        this.chucVu = chucVu;
    }
    public double getLuong() {
        return luong;
    }
    public void setLuong(double luong){
        this.luong = luong;
    }
    public TaiKhoan getTaiKhoan(){
        return taiKhoan;
    }
    public void setTaiKhoan(TaiKhoan taiKhoan){
        this.taiKhoan = taiKhoan;
    }
    @Override
    public void hienThi() {
        System.out.println("Ma nhan vien: " + ma);
        System.out.println("Ho ten: " + hoTen);
        System.out.println("So dien thoai: " + soDienThoai);
        System.out.println("Chuc vu: " + chucVu);
        System.out.println("Luong: " + luong);
        if (taiKhoan != null) {
            taiKhoan.hienThi();
        }
    }
}

