
import java.util.ArrayList;

public class HoaDon {
    private String maHD;
    private String ngayLap;
    private String maKH;
    private String maNV;
    private double tongTien;

    private ArrayList<ChiTietHoaDon> dsChiTiet;

    public HoaDon(String maHD, String ngayLap,
                  String maKH, String maNV) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.tongTien = 0;
        this.dsChiTiet = new ArrayList<>();
    }

    public String getMaHD() {
        return maHD;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public String getMaKH() {
        return maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void themChiTiet(ChiTietHoaDon ct) {
        if (ct != null && maHD.equals(ct.getMaHD())) {
            dsChiTiet.add(ct);
            tinhTongTien();
        }
    }

    public double tinhTongTien() {
        tongTien = 0;

        for (ChiTietHoaDon ct : dsChiTiet) {
            tongTien += ct.tinhThanhTien();
        }

        return tongTien;
    }

    public void inHoaDon() {
        System.out.println("\n========== HOA DON ==========");
        System.out.println("Ma hoa don: " + maHD);
        System.out.println("Ngay lap: " + ngayLap);
        System.out.println("Ma khach hang: " + maKH);
        System.out.println("Ma nhan vien: " + maNV);

        System.out.println("----- Chi tiet hoa don -----");

        for (ChiTietHoaDon ct : dsChiTiet) {
            ct.hienThi();
        }

        System.out.printf("Tong tien: %.2f%n", tinhTongTien());
        System.out.println("=============================");
    }
}
