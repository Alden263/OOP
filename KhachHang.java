
public class KhachHang extends Nguoi{
    private String diaChi;
    public KhachHang(){
        super();
        diaChi = "";
    }
    public KhachHang(String ma, String hoTen, String soDienThoai, String diaChi){
        super(ma, hoTen, soDienThoai);
        this.diaChi = diaChi;
    }
    public String getDiaChi() {
        return diaChi;
    }
    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}

