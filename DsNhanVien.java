package dean;
import java.util.ArrayList;
public class DsNhanVien{
    private ArrayList<NhanVien> danhSach;
    public DsNhanVien(){
        danhSach = new ArrayList<>();
    }
    public void themNhanVien(NhanVien nv){
        danhSach.add(nv);
    }
    public void hienThi(){
        for (NhanVien nv : danhSach){
            nv.hienThi();
            System.out.println("--------------------");
        }
    }
}
