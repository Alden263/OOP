import java.util.Scanner;

public abstract class SanPham {
    protected String maSP;
    protected String tenSP;
    protected double donGia;
    protected int soLuong;

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public SanPham(){
        this.maSP="";
        this.tenSP="";
        this.donGia=0;
        this.soLuong=0;
    }
    public SanPham(String maSP, String tenSP, double donGia, int soLuong){
        this.maSP=maSP;
        this.tenSP=tenSP;
        this.donGia=donGia;
        this.soLuong=soLuong;
    }
    public void nhap(Scanner sc){
        System.out.println("Nhập mã sản phẩm ");
        maSP=sc.nextLine();
        System.out.println("Nhập tên sản phẩm ");
        tenSP=sc.nextLine();
        System.out.println("Nhập giá bán ");
        donGia=Double.parseDouble(sc.nextLine());
        System.out.println("Nhập số lượng tồn kho ");
        soLuong=Integer.parseInt(sc.nextLine());
    }
    public void hienthi(){
        System.out.println("Mã SP: " + maSP + " | Tên SP: " + tenSP +
                " | Đơn giá: " + donGia + " | Số lượng: " + soLuong);
    }
    public abstract double tinhGiaBan();




}
