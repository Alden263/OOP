import java.io.*;
import java.util.Arrays;

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
            return false;
        }

        if (timHoaDon(hd.getMaHD()) != null) {
            return false;   // trung ma hoa don thi khong them
        }

        // Mo rong mang neu het suc chua
        if (soLuong >= ds.length) {
            ds = Arrays.copyOf(ds, ds.length * 2);
        }

        ds[soLuong] = hd;
        soLuong++;
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
    }

    // 10. GHI FILE TEXT (Cai dat tu interface IFileIO)
    @Override
    public void ghiFile(String duongDan) {
        String fileHD = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_HOA_DON;
        try (PrintWriter pwHD = new PrintWriter(new FileWriter(fileHD, java.nio.charset.StandardCharsets.UTF_8));
             PrintWriter pwCT = new PrintWriter(new FileWriter(duongDanChiTiet(duongDan), java.nio.charset.StandardCharsets.UTF_8))) {

            for (int i = 0; i < soLuong; i++) {
                // Ghi thong tin hoa don
                pwHD.println(ds[i].toFileString());

                // Ghi cac chi tiet cua hoa don do
                ChiTietHoaDon[] cts = ds[i].getDsChiTiet();
                for (ChiTietHoaDon ct : cts) {
                    pwCT.println(ct.toFileString());
                }
            }
            if (pwHD.checkError() || pwCT.checkError()) throw new IOException("Lỗi ghi hóa đơn hoặc chi tiết");
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
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
            return;
        }

        // Xoa du lieu cu de nap du lieu moi
        this.ds = new HoaDon[10];
        this.soLuong = 0;

        // Doc danh sach hoa don
        try (BufferedReader brHD = new BufferedReader(new FileReader(fHD, java.nio.charset.StandardCharsets.UTF_8))) {
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
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }

        // Doc danh sach chi tiet hoa don va nap vao tung hoa don tuong ung
        File fCT = new File(duongDanChiTiet(duongDan));
        if (fCT.exists()) {
            try (BufferedReader brCT = new BufferedReader(new FileReader(fCT, java.nio.charset.StandardCharsets.UTF_8))) {
                String line;
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
                        }
                    }
                }
            } catch (Exception e) { throw new IllegalStateException("Lỗi đọc chi tiết hóa đơn", e); }
        }
    }

    public void docFile() {
        docFile(FILE_HOA_DON);
    }
    private String duongDanChiTiet(String duongDan) {
        File f = new File(duongDan == null || duongDan.isEmpty() ? FILE_HOA_DON : duongDan);
        return new File(f.getAbsoluteFile().getParentFile(), FILE_CHI_TIET).getPath();
    }
}
