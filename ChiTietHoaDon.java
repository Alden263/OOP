
public class ChiTietHoaDon {
    private String maHD;
    private String maSP;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon(String maHD, String maSP,
                         int soLuong, double donGia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public String getMaHD() {
        return maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong > 0) {
            this.soLuong = soLuong;
        }
    }

    public void setDonGia(double donGia) {
        if (donGia >= 0) {
            this.donGia = donGia;
        }
    }

    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public void hienThi() {
        System.out.printf(
            "Ma SP: %s | So luong: %d | Don gia: %.2f | Thanh tien: %.2f%n",
            maSP, soLuong, donGia, tinhThanhTien()
        );
    }
}
