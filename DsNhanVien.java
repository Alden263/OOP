
import java.util.ArrayList;
public class DsNhanVien{
    private ArrayList<NhanVien> danhSach;
    public DsNhanVien() throws Exception {
        danhSach = new ArrayList<>();
        docFile();
    }
    public void themNhanVien(NhanVien nv){
        danhSach.add(nv);
        ghiFile();
    }
    public void xoaNhanVien(NhanVien nv){
        danhSach.remove(nv);
        ghiFile();
    }
    public void xoaNhanVienTheoMa(String maNV){
        NhanVien nv = timKiemTheoMa(maNV);
        if (nv != null) {
            danhSach.remove(nv);
            ghiFile();
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
    public void ghiFile(){
        String noiDung = "";
        for (NhanVien nv : danhSach){
            noiDung += nv.getMa() + ";"
                    + nv.getHoTen() + ";"
                    + nv.getSoDienThoai() + ";"
                    + nv.getChucVu() + ";"
                    + nv.getLuong() + "\n";
        }
        XuLyFile.ghiFile("NhanVien.txt", noiDung);
    }
    // doc danh sach nhan vien tu file
    public void docFile() throws Exception {
        danhSach.clear();
        String noiDung = XuLyFile.docFile("NhanVien.txt");
        String[] dong = noiDung.split("\n");
        for (String s : dong) {
            if (!s.trim().isEmpty()) {
                String[] tt = s.split(";");
                if (tt.length == 5) {
                    NhanVien nv = new NhanVien(
                        tt[0],
                        tt[1],
                        tt[2],
                        tt[3],
                        Double.parseDouble(tt[4]),
                        null
                    );
                    danhSach.add(nv);
                }
            }
        }
    }
}
