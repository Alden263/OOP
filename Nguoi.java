package dean;
public abstract class Nguoi{
    protected String ma;
    protected String hoTen;
    protected String soDienThoai;
    public Nguoi() {
        ma = "";
        hoTen = "";
        soDienThoai = "";
    }
    public Nguoi(String ma, String hoTen, String soDienThoai){
        this.ma = ma;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
    }
    public String getMa(){
        return ma;
    }
    public void setMa(String ma){
        this.ma = ma;
    }
    public String getHoTen(){
        return hoTen;
    }
    public void setHoTen(String hoTen){
        this.hoTen = hoTen;
    }
    public String getSoDienThoai(){
        return soDienThoai;
    }
    public void setSoDienThoai(String soDienThoai){
        this.soDienThoai = soDienThoai;
    }
    public abstract void hienThi();
}
