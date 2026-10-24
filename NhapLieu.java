public class NhapLieu {
    private String maNhapLieu;
    private String maSP;       // ma san pham duoc nhap
    private int soLuongNhap;
    private double giaNhap;    // gia nhap vao (khong phai gia ban)

    // constructor
    public NhapLieu(String maNhapLieu, String maSP, int soLuongNhap, double giaNhap) {
        this.maNhapLieu = maNhapLieu;
        this.maSP = maSP;
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
    }

    // getter setter
    public String getMaNhapLieu() { return maNhapLieu; }
    public void setMaNhapLieu(String maNhapLieu) { this.maNhapLieu = maNhapLieu; }

    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }

    public int getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }

    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) { this.giaNhap = giaNhap; }

    // thanh tien = so luong * gia nhap
    public double tinhThanhTien() {
        return soLuongNhap * giaNhap;
    }

    @Override
    public String toString() {
        return "NhapLieu{maNhapLieu=" + maNhapLieu + ", maSP=" + maSP
                + ", soLuongNhap=" + soLuongNhap + ", giaNhap=" + giaNhap + "}";
    }
}