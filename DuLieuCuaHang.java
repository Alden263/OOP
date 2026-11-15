import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Scanner;
import java.util.UUID;

/**
 * Giữ các danh sách dùng chung và lưu chúng theo từng phiên bản.
 * Chỉ đổi tệp CURRENT sau khi tất cả tệp của phiên bản mới đã ghi xong.
 * Nếu lưu thất bại, phiên bản cũ vẫn được dùng và giao diện nạp lại nó.
 */
public class DuLieuCuaHang {
    public final DanhSachSanPham sanPham;
    public final DanhSachHoaDon hoaDon;
    public final DsKhachHang khachHang;
    public final DsNhanVien nhanVien;
    public final DanhSachNhaCungCap nhaCungCap;
    public final DanhSachPhieuNhap phieuNhap;
    private final Path thuMuc;
    private static final String[] CAC_TEP = {
            "SanPham.txt", "HoaDon.txt", "ChiTietHoaDon.txt", "KhachHang.txt",
            "NhanVien.txt", "NhaCungCap.txt", "PhieuNhap.txt"
    };

    public DuLieuCuaHang(Scanner sc, Path thuMuc) throws Exception {
        this.thuMuc = thuMuc;
        sanPham = new DanhSachSanPham();
        hoaDon = new DanhSachHoaDon();
        // Không tự đọc/ghi tệp gốc: việc lưu được điều phối chung bên dưới.
        khachHang = new DsKhachHang(10, false);
        nhanVien = new DsNhanVien(false);
        nhaCungCap = new DanhSachNhaCungCap(sc);
        phieuNhap = new DanhSachPhieuNhap(sc, nhaCungCap);
    }

    public void doc() throws Exception {
        Path current = thuMuc.resolve("CURRENT");
        Path nguon = Paths.get("."); // Nhập dữ liệu tệp cũ ở lần chạy đầu.
        boolean phienBan = Files.exists(current);
        if (phienBan) {
            String ma = Files.readString(current, StandardCharsets.UTF_8).trim();
            UUID.fromString(ma); // Chỉ nhận tên phiên bản UUID, không nhận đường dẫn tùy ý.
            nguon = thuMuc.resolve(ma);
        }
        for (String ten : CAC_TEP) {
            Path f = nguon.resolve(ten);
            if (phienBan && !Files.isRegularFile(f)) throw new IOException("Thiếu tệp dữ liệu: " + f);
            if (Files.isRegularFile(f)) kiemTraTep(f, ten);
        }
        if (Files.exists(nguon.resolve("SanPham.txt"))) sanPham.docFile(nguon.resolve("SanPham.txt").toString());
        if (Files.exists(nguon.resolve("KhachHang.txt"))) khachHang.docFile(nguon.resolve("KhachHang.txt").toString());
        if (Files.exists(nguon.resolve("NhanVien.txt"))) nhanVien.docFile(nguon.resolve("NhanVien.txt").toString());
        if (Files.exists(nguon.resolve("NhaCungCap.txt"))) nhaCungCap.docFile(nguon.resolve("NhaCungCap.txt").toString());
        if (Files.exists(nguon.resolve("PhieuNhap.txt"))) phieuNhap.docFile(nguon.resolve("PhieuNhap.txt").toString());
        if (Files.exists(nguon.resolve("HoaDon.txt"))) hoaDon.docFile(nguon.resolve("HoaDon.txt").toString());
        // Bảo vệ nhập dữ liệu cũ: không để bộ đọc âm thầm bỏ dòng.
        kiemTraSoLuong(nguon);
    }

    public void luu() throws IOException {
        Files.createDirectories(thuMuc);
        String ma = UUID.randomUUID().toString();
        Path moi = Files.createDirectory(thuMuc.resolve(ma));
        try {
            sanPham.ghiFile(moi.resolve("SanPham.txt").toString());
            hoaDon.ghiFile(moi.resolve("HoaDon.txt").toString());
            khachHang.ghiFile(moi.resolve("KhachHang.txt").toString());
            nhanVien.ghiFile(moi.resolve("NhanVien.txt").toString());
            nhaCungCap.ghiFile(moi.resolve("NhaCungCap.txt").toString());
            phieuNhap.ghiFile(moi.resolve("PhieuNhap.txt").toString());
            for (String ten : CAC_TEP) kiemTraTep(moi.resolve(ten), ten);
            Path conTroTam = thuMuc.resolve("CURRENT-" + ma + ".tmp");
            Files.writeString(conTroTam, ma + "\n", StandardCharsets.UTF_8);
            // Không thay bằng di chuyển thường: hệ tệp phải hỗ trợ đổi con trỏ nguyên tử.
            Files.move(conTroTam, thuMuc.resolve("CURRENT"),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (RuntimeException e) {
            throw new IOException("Lưu thất bại; vẫn giữ phiên bản trước.", e);
        }
    }

    private static void kiemTraTep(Path tep, String ten) throws IOException {
        int dong = 0;
        java.util.Set<String> maDaCo = new java.util.HashSet<>();
        for (String line : Files.readAllLines(tep, StandardCharsets.UTF_8)) {
            dong++;
            if (line.isBlank()) continue;
            String[] a = line.split(";", -1);
            int can = switch (ten) {
                case "SanPham.txt" -> switch (a[0]) {
                    case "LAPTOP" -> 8; case "PC" -> 9; case "LINHKIEN" -> 7; default -> -1;
                };
                case "HoaDon.txt" -> a[0].equals("BAN_LE") ? 6 : a[0].equals("BAN_SI") ? 7 : -1;
                case "ChiTietHoaDon.txt", "KhachHang.txt", "NhaCungCap.txt" -> 4;
                case "NhanVien.txt" -> 5;
                case "PhieuNhap.txt" -> a[0].equals("PN") ? 5 : a[0].equals("CT") ? 7 : -1;
                default -> -1;
            };
            if (a.length != can) throw new IOException(tep + ": sai cấu trúc ở dòng " + dong);
            try {
                int viTriMa = ten.equals("SanPham.txt") || ten.equals("HoaDon.txt") || ten.equals("PhieuNhap.txt") ? 1 : 0;
                String khoa = a[viTriMa].trim().toUpperCase(java.util.Locale.ROOT);
                if (ten.equals("ChiTietHoaDon.txt")) khoa += "/" + a[1].toUpperCase(java.util.Locale.ROOT);
                if (ten.equals("PhieuNhap.txt")) khoa = a[0] + "/" + khoa;
                if (khoa.isBlank() || !maDaCo.add(khoa)) throw new IllegalArgumentException("Mã rỗng hoặc trùng");
                if (ten.equals("SanPham.txt")) {
                    soTien(a[3]); soLuong(a[4], true);
                    if (a[0].equals("LAPTOP")) { soLuong(a[6], false); soLuong(a[7], false); }
                    if (a[0].equals("PC")) { soLuong(a[7], false); soLuong(a[8], false); }
                } else if (ten.equals("NhanVien.txt")) soTien(a[4]);
                else if (ten.equals("ChiTietHoaDon.txt")) { soLuong(a[2], false); soTien(a[3]); }
                else if (ten.equals("PhieuNhap.txt") && a[0].equals("CT")) { soLuong(a[5], false); soTien(a[6]); }
                else if (ten.equals("HoaDon.txt")) {
                    if (a[0].equals("BAN_LE")) soLuong(a[5], true);
                    else { soTien(a[5]); if (Double.parseDouble(a[5]) > 100) throw new IllegalArgumentException("Chiết khấu > 100%"); }
                }
            } catch (RuntimeException e) { throw new IOException(tep + ": dữ liệu không hợp lệ ở dòng " + dong, e); }
        }
    }

    private static void soTien(String s) {
        double n = Double.parseDouble(s);
        if (!Double.isFinite(n) || n < 0) throw new IllegalArgumentException("Số tiền không hợp lệ");
    }
    private static void soLuong(String s, boolean choPhepKhong) {
        if (Integer.parseInt(s) < (choPhepKhong ? 0 : 1)) throw new IllegalArgumentException("Số lượng không hợp lệ");
    }
    private long dem(Path thuMuc, String tep, String tienTo) throws IOException {
        Path p = thuMuc.resolve(tep);
        if (!Files.exists(p)) return 0;
        return Files.readAllLines(p, StandardCharsets.UTF_8).stream()
                .filter(s -> !s.isBlank() && (tienTo == null || s.startsWith(tienTo))).count();
    }
    private void kiemTraSoLuong(Path p) throws IOException {
        long chiTietHD = 0, chiTietPN = 0;
        for (HoaDon h : hoaDon.getDs()) chiTietHD += h.getSoLuongChiTiet();
        for (PhieuNhap ph : phieuNhap.layDanhSach()) chiTietPN += ph.getDsChiTiet().size();
        if (dem(p,"SanPham.txt",null) != sanPham.getDsSanPham().size()
                || dem(p,"HoaDon.txt",null) != hoaDon.getSoLuong()
                || dem(p,"ChiTietHoaDon.txt",null) != chiTietHD
                || dem(p,"KhachHang.txt",null) != khachHang.layDanhSach().length
                || dem(p,"NhanVien.txt",null) != nhanVien.layDanhSach().length
                || dem(p,"NhaCungCap.txt",null) != nhaCungCap.layDanhSach().length
                || dem(p,"PhieuNhap.txt","PN;") != phieuNhap.layDanhSach().length
                || dem(p,"PhieuNhap.txt","CT;") != chiTietPN) {
            throw new IOException("Có dòng dữ liệu bị bỏ qua hoặc mất liên kết. Dừng để bảo toàn tệp cũ.");
        }
    }
}
