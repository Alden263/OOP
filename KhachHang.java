package dean;
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
    @Override
    public void hienThi() {
        System.out.println("Ma khach hang: " + ma);
        System.out.println("Ho ten: " + hoTen);
        System.out.println("So dien thoai: " + soDienThoai);
        System.out.println("Dia chi: " + diaChi);
    }
}

