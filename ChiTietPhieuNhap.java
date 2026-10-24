public class ChiTietPhieuNhap {
    private String maCTPN;
    private String maPN;        // thuoc phieu nhap nao
    private String maNhapLieu;  // giu theo so do class

    // quan he HAS-A voi NhapLieu (duong noi trong so do)
    private NhapLieu nhapLieu;

    // constructor, truyen luon doi tuong NhapLieu vao
    public ChiTietPhieuNhap(String maCTPN, String maPN, NhapLieu nhapLieu) {
        // check null cho do bi loi
        if (nhapLieu == null) {
            throw new IllegalArgumentException("NhapLieu khong duoc null");
        }
        this.maCTPN = maCTPN;
        this.maPN = maPN;
        this.nhapLieu = nhapLieu;
        this.maNhapLieu = nhapLieu.getMaNhapLieu(); // lay tu nhapLieu cho khop
    }

    // getter setter
    public String getMaCTPN() { return maCTPN; }
    public void setMaCTPN(String maCTPN) { this.maCTPN = maCTPN; }

    public String getMaPN() { return maPN; }
    public void setMaPN(String maPN) { this.maPN = maPN; }

    public String getMaNhapLieu() { return maNhapLieu; }

    public NhapLieu getNhapLieu() { return nhapLieu; }

    // doi NhapLieu thi doi luon maNhapLieu de khong bi lech
    public void setNhapLieu(NhapLieu nhapLieu) {
        if (nhapLieu == null) {
            throw new IllegalArgumentException("NhapLieu khong duoc null");
        }
        this.nhapLieu = nhapLieu;
        this.maNhapLieu = nhapLieu.getMaNhapLieu();
    }

    // tinh thanh tien, goi qua NhapLieu vi so luong va gia nam o do
    public double tinhThanhTien() {
        return nhapLieu.tinhThanhTien();
    }

    @Override
    public String toString() {
        return "ChiTietPhieuNhap{maCTPN=" + maCTPN + ", maPN=" + maPN
                + ", maNhapLieu=" + maNhapLieu + "}";
    }
}