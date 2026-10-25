package dean;
public class TaiKhoan{
    private String tenDangNhap;
    private String matKhau;
    private QuyenHan quyenHan;
    public TaiKhoan(){
        tenDangNhap = "";
        matKhau = "";
        quyenHan = null;
    }
    public TaiKhoan(String tenDangNhap, String matKhau, QuyenHan quyenHan){
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.quyenHan = quyenHan;
    }
    public String getTenDangNhap(){
        return tenDangNhap;
    }
    public void setTenDangNhap(String tenDangNhap){
        this.tenDangNhap = tenDangNhap;
    }
    public String getMatKhau(){
        return matKhau;
    }
    public void setMatKhau(String matKhau){
        this.matKhau = matKhau;
    }
    public QuyenHan getQuyenHan(){
        return quyenHan;
    }
    public void setQuyenHan(QuyenHan quyenHan){
        this.quyenHan = quyenHan;
    }
    public boolean dangNhap(String tenDangNhap, String matKhau){
        return this.tenDangNhap.equals(tenDangNhap) && this.matKhau.equals(matKhau);
    }
    public void doiMatKhau(String matKhauMoi){
        this.matKhau = matKhauMoi;
    }
    public void hienThi() {
        System.out.println("Ten dang nhap: " + tenDangNhap);
        if (quyenHan != null) {
            System.out.println("Quyen: " + quyenHan.getTenQuyen());
        }
    }
}
