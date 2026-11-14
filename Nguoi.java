
class MaException extends Exception{
    public MaException(String message){
        super(message);
    }
}
class soDienThoaiException extends Exception{
    public soDienThoaiException(String message){
        super(message);
    }
}
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
    public void setMa(String ma) throws MaException{
        if (ma == null || ma.trim().isEmpty()){
            throw new MaException("Mã không được để trống!");
        }
        if (!ma.matches("NV\\d{3}|KH\\d{3}")){
            throw new MaException("Mã phải theo định dạng");
        }
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
    public void setSoDienThoai(String soDienThoai) throws soDienThoaiException{
        if (soDienThoai == null || soDienThoai.trim().isEmpty()){
            throw new MaException("Số điện thoại không được để trống!");
        }
        if(!soDienThoai.matches("\\d{10}")){
            throw new soDienThoaiException("Số điện thoại không hợp lệ");
        }
        this.soDienThoai = soDienThoai;
    }
    public abstract void hienThi();
}
