package dean;
public class DsKhachHang{
    private KhachHang[] danhSach;
    private int soLuong; 
    public DsKhachHang(int kichThuoc){
        danhSach = new KhachHang[kichThuoc]; 
        soLuong = 0;
    }
    public void themKhachHang(KhachHang kh){
        if (soLuong < danhSach.length) {
            danhSach[soLuong] = kh;
            soLuong++;
        } 
        else{
            System.out.println("Danh sách đã đầy, không thể thêm khách hàng mới!");
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

                // Dời các phần tử phía sau lên trước
                for (int j = i; j < soLuong - 1; j++) {
                    danhSach[j] = danhSach[j + 1];
                }

                // Xóa phần tử cuối cùng
                danhSach[soLuong - 1] = null;
                soLuong--;

                System.out.println("Xóa khách hàng thành công!");
                return true;
            }
        }

        System.out.println("Không tìm thấy khách hàng có mã: " + ma);
        return false;
    }
}
