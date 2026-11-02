package dean;
public class QuyenHan{
    private String maQuyen;
    private String tenQuyen;
    private String moTa;
    public final static QuyenHan Nhan_vien= new QuyenHan("NV","Nhan vien","duoc ban hang, nhap hoa don va phieu nhap lieu");
    public final static QuyenHan Quan_ly = new QuyenHan("QL","Quan ly","quan ly nhan vien, san pham, xem duoc thong tin hoa don, phieu nhap lieu va san pham");
    public final static QuyenHan Admin =new QuyenHan("Ad","Admin","quyen quan ly he thong, them xoa sua tai khoan cho nhan vien");
    public QuyenHan(){
        maQuyen = "";
        tenQuyen = "";
        moTa = "";
    }
    public QuyenHan(String maQuyen, String tenQuyen, String moTa){
        this.maQuyen = maQuyen;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
    }
    public String getMaQuyen(){
        return maQuyen;
    }
    public void setMaQuyen(String maQuyen){
        this.maQuyen = maQuyen;
    }
    public String getTenQuyen(){
        return tenQuyen;
    }
    public void setTenQuyen(String tenQuyen){
        this.tenQuyen = tenQuyen;
    }
    public String getMoTa(){
        return moTa;
    }
    public void setMoTa(String moTa){
        this.moTa = moTa;
    }
    public void hienThi(){
        System.out.println("Ma quyen: " + maQuyen);
        System.out.println("Ten quyen: " + tenQuyen);
        System.out.println("Mo ta: " + moTa);
    }
}

