import java.io.*;
import java.util.Arrays;

public class DanhSachPhieuNhap implements ghiDocFile {
    private static final String FILE_PHIEU_NHAP = "PhieuNhap.txt";

    private static final int SUC_CHUA_BAN_DAU = 10;

    private PhieuNhap[] ds = new PhieuNhap[SUC_CHUA_BAN_DAU]; // mang theo so do lop
    private int soLuong = 0;                                  // so phieu dang co trong mang
    private DanhSachNhaCungCap dsNCC; // de kiem tra ma NCC co ton tai khi lap phieu

    public DanhSachPhieuNhap(DanhSachNhaCungCap dsNCC) {
        this.dsNCC = dsNCC;
        dsNCC.setDsPhieuNhap(this); // de dsNCC biet NCC nao dang co phieu khi xoa
    }

    // co phieu nhap nao cua NCC nay khong
    public boolean coPhieuCuaNCC(String maNCC) {
        for (int i = 0; i < soLuong; i++) {
            PhieuNhap ph = ds[i];
            if (ph.getMaNCC().equalsIgnoreCase(maNCC)) {
                return true;
            }
        }
        return false;
    }

    // ===== tim kiem =====

    // tim phieu theo ma, khong thay thi tra ve null
    public PhieuNhap timTheoMa(String ma) {
        for (int i = 0; i < soLuong; i++) {
            PhieuNhap ph = ds[i];
            if (ph.getMaPN().equalsIgnoreCase(ma)) {
                return ph;
            }
        }
        return null;
    }

    // ===== sinh ma tu dong: lay so lon nhat dang co + 1 =====

    public String sinhMaPN() {
        int max = 0;
        for (int i = 0; i < soLuong; i++) {
            PhieuNhap ph = ds[i];
            max = Math.max(max, TienIchMa.soCuoi(ph.getMaPN(), "PN"));
        }
        return String.format("PN%03d", max + 1);
    }

    // them 1 phieu co san,
    // tra ve false neu null, trung ma hoac NCC khong ton tai
    public boolean them(PhieuNhap ph) {
        if (ph == null) {
            return false;
        }
        if (timTheoMa(ph.getMaPN()) != null) {
            return false;
        }
        if (dsNCC.timTheoMa(ph.getMaNCC()) == null) {
            return false;
        }
        // mang day thi noi rong gap doi
        if (soLuong == ds.length) {
            ds = Arrays.copyOf(ds, ds.length * 2);
        }
        ds[soLuong] = ph;
        soLuong++;
        return true;
    }

    // ===== doc / ghi file (interface ghiDocFile) =====

    @Override
    public void ghiFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_PHIEU_NHAP;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, java.nio.charset.StandardCharsets.UTF_8))) {
            for (int i = 0; i < soLuong; i++) {
                PhieuNhap ph = ds[i];
                pw.println("PN;" + ph.getMaPN() + ";" + ph.getNgayNhap() + ";"
                        + ph.getMaNCC() + ";" + ph.getMaNV());
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    NhapLieu nl = ct.getNhapLieu();
                    pw.println("CT;" + ct.getMaCTPN() + ";" + ph.getMaPN() + ";" + nl.getMaNhapLieu()
                            + ";" + nl.getMaSP() + ";" + nl.getSoLuongNhap() + ";" + nl.getGiaNhap());
                }
            }
            if (pw.checkError()) throw new IOException("Không thể ghi đầy đủ dữ liệu");
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
    }

    @Override
    public void docFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_PHIEU_NHAP;
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        // xoa danh sach cu truoc khi nap tu file
        ds = new PhieuNhap[SUC_CHUA_BAN_DAU];
        soLuong = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(";");
                try {
                    if (p[0].equals("PN") && p.length >= 5) {
                        // phieu loi (vd NCC khong ton tai) thi bo qua; DuLieuCuaHang se bao loi vi so dong khong khop
                        them(new PhieuNhap(p[1], p[2], p[3], p[4]));
                    } else if (p[0].equals("CT") && p.length >= 7) {
                        PhieuNhap ph = timTheoMa(p[2]);
                        if (ph == null) {
                            continue; // chi tiet khong co phieu thi bo qua
                        }
                        NhapLieu nl = new NhapLieu(p[3], p[4], Integer.parseInt(p[5]), Double.parseDouble(p[6]));
                        ph.themChiTiet(new ChiTietPhieuNhap(p[1], p[2], nl));
                    }
                } catch (Exception e) {
                    // dong bi loi thi bo qua; DuLieuCuaHang se bao loi vi so dong khong khop
                }
            }
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
    }

    public PhieuNhap[] layDanhSach() { return Arrays.copyOf(ds, soLuong); }
}
