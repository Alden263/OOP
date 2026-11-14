
class LuongException extends Exception {
    public LuongException(String message){
        super(message);
    }
}
class ChucVuException extends RuntimeException {
    public ChucVuException(String message){
        super(message);
    }
}
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
        setChucVu(chucVu);
        setLuong(luong);
        this.taiKhoan = taiKhoan;
    }
    public String getChucVu(){
        return chucVu;
    }
    public void setChucVu(String chucVu){
        if (chucVu == null || chucVu.trim().isEmpty()){
            throw new ChucVuException("Chức vụ không được để trống!");
        }
        this.chucVu = chucVu;
    }
    public double getLuong() {
        return luong;
    }
    public void setLuong(double luong) throws LuongException{
        if (luong < 0) {
            throw new LuongException("Lương không được âm!");
        }
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

