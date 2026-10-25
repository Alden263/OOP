package dean;
import java.util.ArrayList;
public class DsKhachHang{
    private ArrayList<KhachHang> danhSach;
    public DsKhachHang(){
        danhSach = new ArrayList<>();
    }
    public void themKhachHang(KhachHang kh){
        danhSach.add(kh);
    }
    public void hienThi(){
        for (KhachHang kh : danhSach){
            kh.hienThi();
            System.out.println("--------------------");
        }
    }
}
