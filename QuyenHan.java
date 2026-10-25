package dean;
public class QuyenHan{
    private String maQuyen;
    private String tenQuyen;
    private String moTa;
    public QuyenHan(){
        maQuyen = "";
        tenQuyen = "";
        moTa = "";
    }
    public QuyenHan(String maQuyen, String tenQuyen, String moTa) {
        this.maQuyen = maQuyen;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
    }
    public String getMaQuyen() {
        return maQuyen;
    }
    public void setMaQuyen(String maQuyen) {
        this.maQuyen = maQuyen;
    }
    public String getTenQuyen() {
        return tenQuyen;
    }
    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }
    public String getMoTa() {
        return moTa;
    }
    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
    public void hienThi() {
        System.out.println("Ma quyen: " + maQuyen);
        System.out.println("Ten quyen: " + tenQuyen);
        System.out.println("Mo ta: " + moTa);
    }
}

