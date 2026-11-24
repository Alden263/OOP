public class ChiTietHoaDon {
    private String maHD;
    private String maSP;
    private int soLuong;
    private double donGia;

    // Constructor mac dinh
    public ChiTietHoaDon() {
        this.maHD = "";
        this.maSP = "";
        this.soLuong = 0;
        this.donGia = 0.0;
    }

    // Constructor co tham so
    public ChiTietHoaDon(String maHD, String maSP, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.soLuong = Math.max(0, soLuong);
        this.donGia = Math.max(0.0, donGia);
    }

    // Copy constructor (ham thiet lap sao chep)
    public ChiTietHoaDon(ChiTietHoaDon other) {
        if (other != null) {
            this.maHD = other.maHD;
            this.maSP = other.maSP;
            this.soLuong = other.soLuong;
            this.donGia = other.donGia;
        }
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong > 0) {
            this.soLuong = soLuong;
        }
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia >= 0) {
            this.donGia = donGia;
        }
    }

    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public String toFileString() {
        return maHD + ";" + maSP + ";" + soLuong + ";" + donGia;
    }
}
