import java.util.ArrayList;

public class DanhSachSanPham {
    protected int soLuong;

    public ArrayList<SanPham> getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(ArrayList<SanPham> dsSanPham) {
        this.dsSanPham = dsSanPham;
    }

    private ArrayList <SanPham> dsSanPham;
    public DanhSachSanPham(int soLuong, ArrayList<SanPham> dsSanPham) {
        this.soLuong = soLuong;
        this.dsSanPham = dsSanPham;
    }
    public DanhSachSanPham(){
        this.soLuong=0;
        this.dsSanPham=new ArrayList<>();
    }
    public void themSanPham(SanPham sp){
        dsSanPham.add(sp);
        System.out.println("✔ Thêm sản phẩm thành công vào danh sách!");
    }
    public void hienThiDanhSach(){
        if(dsSanPham.isEmpty()){
            System.out.println("Danh sách sản phẩm hiện đang trống");
        }
        for(int i=0; i<= dsSanPham.size();i++){
            System.out.print((i + 1) + ". ");
            dsSanPham.get(i).hienthi(); // Gọi phương thức hiển thị của từng sản phẩm
            // Gọi phương thức abstract tinhGiaBan() đã được override ở lớp con
            System.out.println("   ➔ Giá bán thực tế: " + dsSanPham.get(i).tinhGiaBan());
            System.out.println("-----------------------------------------------------");
        }
    }
    public SanPham searchSanPham(String maSP){
        for(SanPham sp:dsSanPham){
            if(sp.getMaSP().equalsIgnoreCase(maSP)){
                return sp;
            }
        }
        return null;
    }
    public boolean xoaSanPham(String maSP){
        SanPham sp=searchSanPham(maSP);
        if(sp!=null){
            dsSanPham.remove(sp);
            return true;
        }
        return false;

    }

}
