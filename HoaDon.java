import java.util.Arrays;
import java.util.Scanner;

public abstract class HoaDon {
    protected String maHD;
    protected String ngayLap;
    protected String maKH;
    protected String maNV;

    // Quan ly chi tiet hoa don bang mang doi tuong tinh/dong (Array)
    protected ChiTietHoaDon[] dsChiTiet;
    protected int soLuongChiTiet;

    // Thuoc tinh static dem so luong hoa don da tao
    protected static int demHoaDon = 0;

    // Constructor mac dinh
    public HoaDon() {
        this.maHD = "";
        this.ngayLap = "";
        this.maKH = "";
        this.maNV = "";
        this.dsChiTiet = new ChiTietHoaDon[5];
        this.soLuongChiTiet = 0;
        demHoaDon++;
    }

    // Constructor co tham so
    public HoaDon(String maHD, String ngayLap, String maKH, String maNV) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.maKH = maKH;
        this.maNV = maNV;
        this.dsChiTiet = new ChiTietHoaDon[5];
        this.soLuongChiTiet = 0;
        demHoaDon++;
    }

    // Copy constructor (ham thiet lap sao chep)
    public HoaDon(HoaDon other) {
        if (other != null) {
            this.maHD = other.maHD;
            this.ngayLap = other.ngayLap;
            this.maKH = other.maKH;
            this.maNV = other.maNV;
            this.soLuongChiTiet = other.soLuongChiTiet;
            this.dsChiTiet = new ChiTietHoaDon[Math.max(5, other.soLuongChiTiet)];
            for (int i = 0; i < other.soLuongChiTiet; i++) {
                this.dsChiTiet[i] = new ChiTietHoaDon(other.dsChiTiet[i]);
            }
            demHoaDon++;
        }
    }

    // Phuong thuc static sinh ma hoa don tu dong
    public static String sinhMaTuDong() {
        return String.format("HD%03d", demHoaDon + 1);
    }

    // Phuong thuc static kiem tra dinh dang ngay dd/MM/yyyy co ban
    public static boolean kiemTraNgayHopLe(String ngay) {
        if (ngay == null) return false;
        return ngay.matches("^\\d{1,2}/\\d{1,2}/\\d{4}$");
    }

    public static int getDemHoaDon() {
        return demHoaDon;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(String ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getMaNV() {
        return maNV;
    }

    public void setMaNV(String maNV) {
        this.maNV = maNV;
    }

    public ChiTietHoaDon[] getDsChiTiet() {
        return Arrays.copyOf(dsChiTiet, soLuongChiTiet);
    }

    public int getSoLuongChiTiet() {
        return soLuongChiTiet;
    }

    // Them chi tiet hoa don vao mang doi tuong (tu dong mo rong mang bang Arrays.copyOf)
    public void themChiTiet(ChiTietHoaDon ct) {
        if (ct == null) return;

        // Kiem tra neu trung san pham thi cong don so luong
        for (int i = 0; i < soLuongChiTiet; i++) {
            if (dsChiTiet[i].getMaSP().equalsIgnoreCase(ct.getMaSP())) {
                dsChiTiet[i].setSoLuong(dsChiTiet[i].getSoLuong() + ct.getSoLuong());
                return;
            }
        }

        // Mo rong mang neu day
        if (soLuongChiTiet >= dsChiTiet.length) {
            dsChiTiet = Arrays.copyOf(dsChiTiet, dsChiTiet.length * 2);
        }

        dsChiTiet[soLuongChiTiet] = ct;
        soLuongChiTiet++;
    }

    // Xoa mot chi tiet hoa don theo maSP (don mang)
    public boolean xoaChiTiet(String maSP) {
        int index = -1;
        for (int i = 0; i < soLuongChiTiet; i++) {
            if (dsChiTiet[i].getMaSP().equalsIgnoreCase(maSP)) {
                index = i;
                break;
            }
        }

        if (index == -1) return false;

        for (int i = index; i < soLuongChiTiet - 1; i++) {
            dsChiTiet[i] = dsChiTiet[i + 1];
        }
        dsChiTiet[soLuongChiTiet - 1] = null;
        soLuongChiTiet--;
        return true;
    }

    // Tinh tong tien goc chua tru giam gia
    public double tinhTongTienGoc() {
        double tong = 0.0;
        for (int i = 0; i < soLuongChiTiet; i++) {
            tong += dsChiTiet[i].tinhThanhTien();
        }
        return tong;
    }

    // Phuong thuc truu tuong (abstract) - Moi loai hoa don tinh giam gia khac nhau (Da hinh)
    public abstract double tinhGiamGia();

    // Phuong thuc truu tuong lay ten loai hoa don
    public abstract String getLoaiHoaDon();

    // Tong tien thuc thanh toan
    public double getTongTien() {
        return Math.max(0, tinhTongTienGoc() - tinhGiamGia());
    }

    // Nhap thong tin co ban cua hoa don tu ban phim
    public void nhap(Scanner sc) {
        System.out.print("Nhap ma hoa don (hoac Enter de tu sinh): ");
        String ma = sc.nextLine().trim();
        if (ma.isEmpty()) {
            this.maHD = sinhMaTuDong();
            System.out.println("Ma hoa don tu dong: " + this.maHD);
        } else {
            this.maHD = ma;
        }

        do {
            System.out.print("Nhap ngay lap (dd/MM/yyyy): ");
            this.ngayLap = sc.nextLine().trim();
            if (!kiemTraNgayHopLe(this.ngayLap)) {
                System.out.println("Dinh dang ngay khong hop le! Vui long nhap lai (vd: 15/10/2026).");
            }
        } while (!kiemTraNgayHopLe(this.ngayLap));

        System.out.print("Nhap ma khach hang: ");
        this.maKH = sc.nextLine().trim();

        System.out.print("Nhap ma nhan vien lap: ");
        this.maNV = sc.nextLine().trim();
    }

    public void inHoaDon() {
        System.out.println("\n+--------------------------------------------------------------------------+");
        System.out.printf("| %-72s |\n", "HOA DON: " + maHD + " (" + getLoaiHoaDon() + ")");
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.printf("| Ngay lap       : %-55s |\n", ngayLap);
        System.out.printf("| Ma khach hang  : %-55s |\n", maKH);
        System.out.printf("| Ma nhan vien   : %-55s |\n", maNV);
        System.out.println("+--------------------------------------------------------------------------+");
        System.out.println("|                            CHI TIET SAN PHAM                             |");
        System.out.println("+--------------------------------------------------------------------------+");

        if (soLuongChiTiet == 0) {
            System.out.println("| (Chua co chi tiet san pham nao)                                          |");
        } else {
            for (int i = 0; i < soLuongChiTiet; i++) {
                dsChiTiet[i].hienThi();
            }
        }

        System.out.println("+--------------------------------------------------------------------------+");
        System.out.printf("| Tong tien goc  : %-55.2f |\n", tinhTongTienGoc());
        System.out.printf("| Giam gia       : %-55.2f |\n", tinhGiamGia());
        System.out.printf("| THANH TOAN     : %-55.2f |\n", getTongTien());
        System.out.println("+--------------------------------------------------------------------------+");
    }

    // Chuoi dinh dang de ghi xuong file text: Loai;maHD;ngayLap;maKH;maNV;thongSoRieng
    public abstract String toFileString();
}
