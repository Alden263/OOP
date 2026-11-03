import java.util.Scanner;

public class HoaDonBanLe extends HoaDon {
    private int diemTichLuySuDung; // Moi diem tuong duong giam 1.000d

    // Constructor mac dinh
    public HoaDonBanLe() {
        super();
        this.diemTichLuySuDung = 0;
    }

    // Constructor co tham so
    public HoaDonBanLe(String maHD, String ngayLap, String maKH, String maNV, int diemTichLuySuDung) {
        super(maHD, ngayLap, maKH, maNV);
        this.diemTichLuySuDung = Math.max(0, diemTichLuySuDung);
    }

    // Copy constructor
    public HoaDonBanLe(HoaDonBanLe other) {
        super(other);
        if (other != null) {
            this.diemTichLuySuDung = other.diemTichLuySuDung;
        }
    }

    public int getDiemTichLuySuDung() {
        return diemTichLuySuDung;
    }

    public void setDiemTichLuySuDung(int diemTichLuySuDung) {
        this.diemTichLuySuDung = Math.max(0, diemTichLuySuDung);
    }

    @Override
    public double tinhGiamGia() {
        // Giam 1.000d moi diem, toi da khong qua 20% tien goc
        double tienGiam = diemTichLuySuDung * 1000.0;
        double toiDa = tinhTongTienGoc() * 0.20;
        return Math.min(tienGiam, toiDa);
    }

    @Override
    public String getLoaiHoaDon() {
        return "Ban le";
    }

    @Override
    public void nhap(Scanner sc) {
        super.nhap(sc);
        System.out.print("Nhap diem tich luy khach muon su dung: ");
        try {
            this.diemTichLuySuDung = Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            this.diemTichLuySuDung = 0;
        }
    }

    @Override
    public String toFileString() {
        // Dinh dang luu file: BAN_LE;maHD;ngayLap;maKH;maNV;diemTichLuySuDung
        return "BAN_LE;" + maHD + ";" + ngayLap + ";" + maKH + ";" + maNV + ";" + diemTichLuySuDung;
    }
}
