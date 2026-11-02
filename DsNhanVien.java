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
    public void xoaNhanVien(NhanVien nv){
        danhSach.remove(nv);
    }
    public void xoaNhanVienTheoMa(String maNV){
        for (NhanVien nv : danhSach){
            if (nv.getMa().equals(maNV)){
                danhSach.remove(nv);
                break;
            }
        }
    }
    public NhanVien timKiemTheoMa(String maNV){
        for (NhanVien nv : danhSach){
            if (nv.getMa().equals(maNV)){
                return nv;
            }
        }
        return null;
    }
    public ArrayList<NhanVien> timKiemTheoTen(String ten){
        ArrayList<NhanVien> ketQua = new ArrayList<>();
        for (NhanVien nv : danhSach) {
            if (nv.getHoTen().equalsIgnoreCase(ten)) {
                ketQua.add(nv);
            }
        }
        return ketQua;
    }
    public void hienThi(){
        for (NhanVien nv : danhSach){
            nv.hienThi();
            System.out.println("--------------------");
        }
    }
}
