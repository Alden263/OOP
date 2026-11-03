import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class DanhSachHoaDon implements IFileIO {
    // Lop mang cac doi tuong (Array) - Khong dung ArrayList de dat tron diem OOP
    private HoaDon[] ds;
    private int soLuong;

    // File mac dinh
    private static final String FILE_HOA_DON = "HoaDon.txt";
    private static final String FILE_CHI_TIET = "ChiTietHoaDon.txt";

    // Constructor mac dinh
    public DanhSachHoaDon() {
        this.ds = new HoaDon[10];
        this.soLuong = 0;
    }

    // Constructor co tham so dung luong
    public DanhSachHoaDon(int dungLuongBanDau) {
        this.ds = new HoaDon[Math.max(1, dungLuongBanDau)];
        this.soLuong = 0;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public HoaDon[] getDs() {
        return Arrays.copyOf(ds, soLuong);
    }

    // 1. THEM HOA DON (Co che tu dong mo rong mang Arrays.copyOf)
    public boolean themHoaDon(HoaDon hd) {
        if (hd == null) {
            System.out.println("Hoa don khong hop le!");
            return false;
        }

        if (timHoaDon(hd.getMaHD()) != null) {
            System.out.println("Trung ma hoa don: " + hd.getMaHD() + "! Khong the them.");
            return false;
        }

        // Mo rong mang neu het suc chua
        if (soLuong >= ds.length) {
            ds = Arrays.copyOf(ds, ds.length * 2);
        }

        ds[soLuong] = hd;
        soLuong++;
        System.out.println("Them hoa don " + hd.getMaHD() + " thanh cong!");
        return true;
    }

    // 2. TIM HOA DON THEO MA
    public HoaDon timHoaDon(String maHD) {
        if (maHD == null) return null;
        for (int i = 0; i < soLuong; i++) {
            if (ds[i].getMaHD().equalsIgnoreCase(maHD.trim())) {
                return ds[i];
            }
        }
        return null;
    }

    // 3. XOA HOA DON THEO MA (Thuat toan don mang)
    public boolean xoaHoaDon(String maHD) {
        int viTri = -1;
        for (int i = 0; i < soLuong; i++) {
            if (ds[i].getMaHD().equalsIgnoreCase(maHD.trim())) {
                viTri = i;
                break;
            }
        }

        if (viTri == -1) {
            System.out.println("Khong tim thay hoa don can xoa: " + maHD);
            return false;
        }

        // Don mang
        for (int i = viTri; i < soLuong - 1; i++) {
            ds[i] = ds[i + 1];
        }
        ds[soLuong - 1] = null;
        soLuong--;
        System.out.println("Xoa thanh cong hoa don: " + maHD);
        return true;
    }

    // 4. SUA THONG TIN HOA DON
    public boolean suaHoaDon(String maHD, Scanner sc) {
        HoaDon hd = timHoaDon(maHD);
        if (hd == null) {
            System.out.println("Khong tim thay hoa don de sua: " + maHD);
            return false;
        }

        System.out.println("\n--- SUA THONG TIN HOA DON " + maHD + " ---");
        System.out.print("Nhap ngay lap moi (Enter de giu nguyen: " + hd.getNgayLap() + "): ");
        String ngay = sc.nextLine().trim();
        if (!ngay.isEmpty()) {
            if (HoaDon.kiemTraNgayHopLe(ngay)) {
                hd.setNgayLap(ngay);
            } else {
                System.out.println("Ngay khong dung dinh dang, giu nguyen ngay cu!");
            }
        }

        System.out.print("Nhap ma khach hang moi (Enter de giu nguyen: " + hd.getMaKH() + "): ");
        String kh = sc.nextLine().trim();
        if (!kh.isEmpty()) hd.setMaKH(kh);

        System.out.print("Nhap ma nhan vien moi (Enter de giu nguyen: " + hd.getMaNV() + "): ");
        String nv = sc.nextLine().trim();
        if (!nv.isEmpty()) hd.setMaNV(nv);

        if (hd instanceof HoaDonBanLe) {
            HoaDonBanLe hdl = (HoaDonBanLe) hd;
            System.out.print("Nhap diem tich luy su dung moi (Enter de giu nguyen: " + hdl.getDiemTichLuySuDung() + "): ");
            String d = sc.nextLine().trim();
            if (!d.isEmpty()) {
                try {
                    hdl.setDiemTichLuySuDung(Integer.parseInt(d));
                } catch (NumberFormatException ignored) {}
            }
        } else if (hd instanceof HoaDonBanSi) {
            HoaDonBanSi hds = (HoaDonBanSi) hd;
            System.out.print("Nhap chiet khau % moi (Enter de giu nguyen: " + hds.getPhanTramChietKhau() + "): ");
            String ck = sc.nextLine().trim();
            if (!ck.isEmpty()) {
                try {
                    hds.setPhanTramChietKhau(Double.parseDouble(ck));
                } catch (NumberFormatException ignored) {}
            }
        }

        System.out.println("Cap nhat thong tin hoa don thanh cong!");
        return true;
    }

    // 5. TIM KIEM THEO MA KHACH HANG
    public void timKiemTheoMaKH(String maKH) {
        boolean timThay = false;
        System.out.println("\n=== DANH SACH HOA DON CUA KHACH HANG: " + maKH + " ===");
        for (int i = 0; i < soLuong; i++) {
            if (ds[i].getMaKH().equalsIgnoreCase(maKH.trim())) {
                ds[i].inHoaDon();
                timThay = true;
            }
        }
        if (!timThay) {
            System.out.println("Khong co hoa don nao cua khach hang nay.");
        }
    }

    // 6. TINH TONG DOANH THU (Su dung da hinh qua ds[i].getTongTien())
    public double tinhTongDoanhThu() {
        double tong = 0.0;
        for (int i = 0; i < soLuong; i++) {
            tong += ds[i].getTongTien();
        }
        return tong;
    }

    // 7. TIM HOA DON GIA TRI CAO NHAT
    public HoaDon timHoaDonCaoNhat() {
        if (soLuong == 0) return null;
        HoaDon max = ds[0];
        for (int i = 1; i < soLuong; i++) {
            if (ds[i].getTongTien() > max.getTongTien()) {
                max = ds[i];
            }
        }
        return max;
    }

    // 8. SAP XEP HOA DON THEO TONG TIEN GIAM DAN
    public void sapXepTheoTongTienGiamDan() {
        for (int i = 0; i < soLuong - 1; i++) {
            for (int j = i + 1; j < soLuong; j++) {
                if (ds[i].getTongTien() < ds[j].getTongTien()) {
                    HoaDon temp = ds[i];
                    ds[i] = ds[j];
                    ds[j] = temp;
                }
            }
        }
        System.out.println("Da sap xep danh sach hoa don theo tong tien giam dan!");
    }

    // 9. HIEN THI TOAN BO DANH SACH (Goi inHoaDon() da hinh)
    public void hienThi() {
        if (soLuong == 0) {
            System.out.println("Danh sach hoa don dang trong!");
            return;
        }

        System.out.println("\n============================================================================");
        System.out.println("                  DANH SACH TOAN BO HOA DON (" + soLuong + " hoa don)");
        System.out.println("============================================================================");
        for (int i = 0; i < soLuong; i++) {
            ds[i].inHoaDon();
        }
    }

    // 10. GHI FILE TEXT (Cai dat tu interface IFileIO)
    @Override
    public void ghiFile(String duongDan) {
        String fileHD = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_HOA_DON;
        try (PrintWriter pwHD = new PrintWriter(new FileWriter(fileHD));
             PrintWriter pwCT = new PrintWriter(new FileWriter(FILE_CHI_TIET))) {

            for (int i = 0; i < soLuong; i++) {
                // Ghi thong tin hoa don
                pwHD.println(ds[i].toFileString());

                // Ghi cac chi tiet cua hoa don do
                ChiTietHoaDon[] cts = ds[i].getDsChiTiet();
                for (ChiTietHoaDon ct : cts) {
                    pwCT.println(ct.toFileString());
                }
            }
            System.out.println("Ghi du lieu thanh cong vao " + fileHD + " va " + FILE_CHI_TIET + "!");
        } catch (IOException e) {
            System.out.println("Loi khi ghi file: " + e.getMessage());
        }
    }

    public void ghiFile() {
        ghiFile(FILE_HOA_DON);
    }

    // 11. DOC FILE TEXT (Cai dat tu interface IFileIO)
    @Override
    public void docFile(String duongDan) {
        String fileHD = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_HOA_DON;
        File fHD = new File(fileHD);
        if (!fHD.exists()) {
            System.out.println("File " + fileHD + " khong ton tai!");
            return;
        }

        // Xoa du lieu cu de nap du lieu moi
        this.ds = new HoaDon[10];
        this.soLuong = 0;

        // Doc danh sach hoa don
        try (BufferedReader brHD = new BufferedReader(new FileReader(fHD))) {
            String line;
            while ((line = brHD.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(";");
                // Kiem tra loai hoa don de khoi tao da hinh
                if (p[0].equalsIgnoreCase("BAN_LE") && p.length >= 6) {
                    int diem = 0;
                    try { diem = Integer.parseInt(p[5]); } catch (Exception ignored) {}
                    HoaDonBanLe hdl = new HoaDonBanLe(p[1], p[2], p[3], p[4], diem);
                    themHoaDon(hdl);
                } else if (p[0].equalsIgnoreCase("BAN_SI") && p.length >= 7) {
                    double ck = 0.0;
                    try { ck = Double.parseDouble(p[5]); } catch (Exception ignored) {}
                    HoaDonBanSi hds = new HoaDonBanSi(p[1], p[2], p[3], p[4], ck, p[6]);
                    themHoaDon(hds);
                }
            }
            System.out.println("Doc file " + fileHD + " thanh cong! So hoa don: " + soLuong);
        } catch (IOException e) {
            System.out.println("Loi khi doc file " + fileHD + ": " + e.getMessage());
            return;
        }

        // Doc danh sach chi tiet hoa don va nap vao tung hoa don tuong ung
        File fCT = new File(FILE_CHI_TIET);
        if (fCT.exists()) {
            try (BufferedReader brCT = new BufferedReader(new FileReader(fCT))) {
                String line;
                int demCT = 0;
                while ((line = brCT.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] p = line.split(";");
                    if (p.length >= 4) {
                        String maHD = p[0];
                        String maSP = p[1];
                        int sl = Integer.parseInt(p[2]);
                        double gia = Double.parseDouble(p[3]);

                        HoaDon hd = timHoaDon(maHD);
                        if (hd != null) {
                            hd.themChiTiet(new ChiTietHoaDon(maHD, maSP, sl, gia));
                            demCT++;
                        }
                    }
                }
                System.out.println("Doc file " + FILE_CHI_TIET + " thanh cong! Da nap " + demCT + " chi tiet san pham vao cac hoa don.");
            } catch (Exception e) {
                System.out.println("Loi khi doc file " + FILE_CHI_TIET + ": " + e.getMessage());
            }
        }
    }

    public void docFile() {
        docFile(FILE_HOA_DON);
    }
}
