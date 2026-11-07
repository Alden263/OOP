import java.util.Scanner;

public abstract class SanPham {
    protected String maSP;
    protected String tenSP;
    protected double donGia;
    protected int soLuong;
    private static int demLT = 0;
    private static int demPC = 0;
    private static int demLK = 0;
    public static String sinhMa(String loai) {
        if (loai.equalsIgnoreCase("LT")) {
            return String.format("LT%03d", ++demLT);
        } else if (loai.equalsIgnoreCase("PC")) {
            return String.format("PC%03d", ++demPC);
        } else if (loai.equalsIgnoreCase("LK")) {
            return String.format("LK%03d", ++demLK);
        }
        return String.format("SP%03d", ++demLT);
    }

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
