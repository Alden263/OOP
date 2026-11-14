
public class DsKhachHang{
    private KhachHang[] danhSach;
    private int soLuong; 
    public DsKhachHang(int kichThuoc){
        danhSach = new KhachHang[kichThuoc]; 
        soLuong = 0;
        docFile();
    }
    public void themKhachHang(KhachHang kh){
        if (soLuong < danhSach.length) {
            danhSach[soLuong] = kh;
            soLuong++;
            ghiFile();
        } 
        else{
            System.out.println("Danh sach da day, khong the them khach hang moi!");
        }
    }
    public void xuatKhachHang(){
        if (soLuong == 0){
            System.out.println("Danh sach rong");
            return;
        }
        System.out.println("Danh sach khach hang:");
        for (int i = 0; i < soLuong; i++){
            System.out.println("Khach hang thu " + (i + 1));
            danhSach[i].hienThi();
            System.out.println();
        }
    }  
    public void hienThi(){
        for (int i = 0; i < soLuong; i++) {
            danhSach[i].hienThi();
            System.out.println("--------------------");
        }
    }
    public KhachHang timTheoMa(String ma){
        for (int i = 0; i < soLuong; i++){
            if (danhSach[i].getMa().equalsIgnoreCase(ma)){
                return danhSach[i];
            }
        }
        return null;
    }
    public void timTheoTen(String ten){
        boolean found = false;
        for (int i = 0; i < soLuong; i++){
            if (danhSach[i].getHoTen().equalsIgnoreCase(ten)){
                danhSach[i].hienThi();
                System.out.println("--------------------");
                found = true;
            }
        }
        if (!found){
            System.out.println("Không tìm thấy khách hàng với tên: " + ten);
        }
    }
    public KhachHang timTheoSoDienThoai(String sdt){
        for (int i = 0; i < soLuong; i++){
            if (danhSach[i].getSoDienThoai().equals(sdt)){
                return danhSach[i];
            }
        }
        return null;
    }
    public boolean xoaKhachHang(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (danhSach[i].getMa().equalsIgnoreCase(ma)) {
                for (int j = i; j < soLuong - 1; j++) {
                    danhSach[j] = danhSach[j + 1];
                }
                // Xóa phần tử cuối cùng
                danhSach[soLuong - 1] = null;
                soLuong--;
                ghiFile();
                System.out.println("Xóa khách hàng thành công!");
                return true;
            }
        }
        System.out.println("Không tìm thấy khách hàng có mã: " + ma);
        return false;
    }
    public void ghiFile() {
        String noiDung = "";
        for (int i = 0; i < soLuong; i++) {
            KhachHang kh = danhSach[i];
            noiDung += kh.getMa() + ";"
                    + kh.getHoTen() + ";"
                    + kh.getSoDienThoai() + ";"
                    + kh.getDiaChi() + "\n";
        }
        XuLyFile.ghiFile("KhachHang.txt", noiDung);
    }

    // Đọc danh sách khách hàng từ file
    public void docFile() {
        soLuong = 0;
        String noiDung = XuLyFile.docFile("KhachHang.txt");
        String[] dong = noiDung.split("\n");
        for (String s : dong) {
            if (!s.trim().isEmpty()) {
                String[] tt = s.split(";", -1);
                if (tt.length == 4) {
                    if (soLuong < danhSach.length) {
                        KhachHang kh = new KhachHang(
                            tt[0],
                            tt[1],
                            tt[2],
                            tt[3]
                        );
                        danhSach[soLuong] = kh;
                        soLuong++;
                    } else {
                        System.out.println("Mang khach hang da day!");
                        break;
                    }
                }
            }
        }
    }
}
