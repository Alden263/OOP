import java.util.Scanner;

public class HoaDonBanSi extends HoaDon {
    private double phanTramChietKhau; // Vi du: 5.0, 10.0 (%)
    private String tenCongTy;

    // Constructor mac dinh
    public HoaDonBanSi() {
        super();
        this.phanTramChietKhau = 0.0;
        this.tenCongTy = "";
    }

    // Constructor co tham so
    public HoaDonBanSi(String maHD, String ngayLap, String maKH, String maNV, double phanTramChietKhau, String tenCongTy) {
        super(maHD, ngayLap, maKH, maNV);
        this.phanTramChietKhau = Math.max(0.0, phanTramChietKhau);
        this.tenCongTy = tenCongTy != null ? tenCongTy : "";
    }

    // Copy constructor
    public HoaDonBanSi(HoaDonBanSi other) {
        super(other);
        if (other != null) {
            this.phanTramChietKhau = other.phanTramChietKhau;
            this.tenCongTy = other.tenCongTy;
        }
    }

    public double getPhanTramChietKhau() {
        return phanTramChietKhau;
    }

    public void setPhanTramChietKhau(double phanTramChietKhau) {
        this.phanTramChietKhau = Math.max(0.0, phanTramChietKhau);
    }

    public String getTenCongTy() {
        return tenCongTy;
    }

    public void setTenCongTy(String tenCongTy) {
        this.tenCongTy = tenCongTy;
    }

    @Override
    public double tinhGiamGia() {
        return tinhTongTienGoc() * (phanTramChietKhau / 100.0);
    }

    @Override
    public String getLoaiHoaDon() {
        return "Ban si (Dai ly)";
    }

    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);
        System.out.print("Nhap ten cong ty / dai ly: ");
        this.tenCongTy = sc.nextLine().trim();

        System.out.print("Nhap phan tram chiet khau (%): ");
        try {
            this.phanTramChietKhau = Double.parseDouble(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            this.phanTramChietKhau = 0.0;
        }
    }

    @Override
    public void inHoaDon() {
        super.inHoaDon();
        System.out.println("| Thong tin bo sung ban si: Dai ly: " + tenCongTy + " | Chiet khau: " + phanTramChietKhau + "%");
        System.out.println("+--------------------------------------------------------------------------+");
    }

    @Override
    public String toFileString() {
        // Dinh dang luu file: BAN_SI;maHD;ngayLap;maKH;maNV;phanTramChietKhau;tenCongTy
        return "BAN_SI;" + maHD + ";" + ngayLap + ";" + maKH + ";" + maNV + ";" + phanTramChietKhau + ";" + tenCongTy;
    }
}
