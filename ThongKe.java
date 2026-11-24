import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * [UX] Các màn hình THỐNG KÊ (chỉ Quản lý và Admin xem): doanh thu theo ngày, theo nhân viên,
 * sản phẩm bán chạy, cảnh báo sắp hết hàng.
 * Lớp này chỉ ĐỌC dữ liệu rồi in ra bảng (lớp Bang), không sửa gì. Mọi hàm đều static.
 */
public class ThongKe {
    /** Tồn kho từ mức này trở xuống thì cảnh báo "sắp hết hàng". */
    public static final int NGUONG_SAP_HET = 3;

    /** Chỉ có hàm static nên không cho tạo đối tượng. */
    private ThongKe() {
    }

    /** Một dòng thống kê: gom số liệu theo "khóa" (ngày / mã nhân viên / mã sản phẩm). */
    private static class Dong {
        String khoa;      // ngày, mã nhân viên hoặc mã sản phẩm
        String ten = "";  // tên hiển thị (nhân viên / sản phẩm)
        int soHD = 0;     // số hóa đơn
        int soLuong = 0;  // số lượng sản phẩm đã bán
        double tien = 0;  // tổng tiền

        Dong(String khoa) {
            this.khoa = khoa;
        }
    }

    /** Tìm dòng có khóa này trong danh sách; chưa có thì tạo dòng mới rồi thêm vào. */
    private static Dong timHoacTao(ArrayList<Dong> ds, String khoa) {
        for (Dong d : ds) {
            if (d.khoa.equalsIgnoreCase(khoa)) return d;
        }
        Dong moi = new Dong(khoa);
        ds.add(moi);
        return moi;
    }

    // ===================== HÀM PHỤ VỀ NGÀY =====================

    /** Đọc ngày dạng d/M/yyyy (gõ "5/1/2026" hay "05/01/2026" đều được). Sai định dạng thì trả về null. */
    private static LocalDate docNgay(String s) {
        try {
            return LocalDate.parse(s, DateTimeFormatter.ofPattern("d/M/yyyy"));
        } catch (Exception e) {
            return null;
        }
    }

    /** Đưa ngày về dạng dd/MM/yyyy để "5/1/2026" và "05/01/2026" được tính là cùng một ngày. */
    private static String chuanHoaNgay(String s) {
        LocalDate d = docNgay(s);
        return d == null ? s : d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    // ===================== 1. DOANH THU THEO NGÀY =====================

    /** Bảng doanh thu từng ngày (ngày cũ trước), kèm tổng ở cuối. */
    public static void doanhThuTheoNgay(DuLieuCuaHang dl) {
        ArrayList<Dong> ds = new ArrayList<>();
        for (HoaDon hd : dl.hoaDon.getDs()) {
            Dong d = timHoacTao(ds, chuanHoaNgay(hd.getNgayLap()));
            d.soHD++;
            d.tien += hd.getTongTien();   // ĐA HÌNH: bán lẻ / bán sỉ mỗi loại tự tính giảm giá của mình
        }
        // Sắp xếp theo ngày tăng dần (ngày sai định dạng cho xuống cuối)
        ds.sort((a, b) -> {
            LocalDate x = docNgay(a.khoa), y = docNgay(b.khoa);
            if (x == null || y == null) return x == null ? (y == null ? 0 : 1) : -1;
            return x.compareTo(y);
        });
        Bang b = new Bang("Ngày", "Số hóa đơn", "Doanh thu");
        for (Dong d : ds) b.themDong(d.khoa, d.soHD, Bang.tien(d.tien));
        b.in("DOANH THU THEO NGÀY");
        System.out.println("Tổng cộng: " + dl.hoaDon.getSoLuong() + " hóa đơn, doanh thu "
                + Bang.tien(dl.hoaDon.tinhTongDoanhThu()) + " đ");
    }

    // ===================== 2. DOANH THU THEO NHÂN VIÊN =====================

    /** Bảng doanh thu của từng nhân viên (cao nhất lên đầu). Nhân viên chưa bán gì vẫn hiện với số 0. */
    public static void doanhThuTheoNhanVien(DuLieuCuaHang dl) {
        ArrayList<Dong> ds = new ArrayList<>();
        for (NhanVien nv : dl.nhanVien.layDanhSach()) {   // có tên trong danh sách thì có dòng, dù chưa bán gì
            Dong d = timHoacTao(ds, nv.getMa());
            d.ten = nv.getHoTen();
        }
        for (HoaDon hd : dl.hoaDon.getDs()) {
            Dong d = timHoacTao(ds, hd.getMaNV());
            d.soHD++;
            d.tien += hd.getTongTien();
        }
        ds.sort((a, b) -> Double.compare(b.tien, a.tien));   // doanh thu giảm dần
        Bang b = new Bang("STT", "Mã NV", "Họ tên", "Số hóa đơn", "Doanh thu");
        int stt = 1;
        for (Dong d : ds) b.themDong(stt++, d.khoa, d.ten, d.soHD, Bang.tien(d.tien));
        b.in("DOANH THU THEO NHÂN VIÊN");
    }

    // ===================== 3. SẢN PHẨM BÁN CHẠY =====================

    /** Bảng tối đa 10 sản phẩm bán được nhiều nhất (tính theo số lượng, bằng nhau thì ai nhiều tiền hơn xếp trước). */
    public static void sanPhamBanChay(DuLieuCuaHang dl) {
        ArrayList<Dong> ds = new ArrayList<>();
        for (HoaDon hd : dl.hoaDon.getDs()) {
            for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                Dong d = timHoacTao(ds, ct.getMaSP());
                d.soLuong += ct.getSoLuong();
                d.tien += ct.tinhThanhTien();   // tiền hàng, chưa trừ chiết khấu của hóa đơn sỉ
            }
        }
        ds.sort((a, b) -> a.soLuong != b.soLuong ? b.soLuong - a.soLuong : Double.compare(b.tien, a.tien));
        Bang b = new Bang("Hạng", "Mã SP", "Tên sản phẩm", "Đã bán (cái)", "Tiền hàng");
        int hang = 1;
        for (Dong d : ds) {
            if (hang > 10) break;   // chỉ lấy top 10
            SanPham sp = dl.sanPham.searchSanPham(d.khoa);
            b.themDong(hang++, d.khoa, sp == null ? "(đã xóa)" : sp.getTenSP(), d.soLuong, Bang.tien(d.tien));
        }
        b.in("SẢN PHẨM BÁN CHẠY");
    }

    // ===================== 4. CẢNH BÁO SẮP HẾT HÀNG =====================

    /** Bảng các sản phẩm tồn kho từ NGUONG_SAP_HET trở xuống (ít nhất lên đầu) để kịp nhập thêm. */
    public static void canhBaoSapHetHang(DuLieuCuaHang dl) {
        ArrayList<SanPham> ds = new ArrayList<>();
        for (SanPham sp : dl.sanPham.getDsSanPham()) {
            if (sp.getSoLuong() <= NGUONG_SAP_HET) ds.add(sp);
        }
        if (ds.isEmpty()) {
            System.out.println("\nKhông có sản phẩm nào tồn kho từ " + NGUONG_SAP_HET + " trở xuống.");
            return;
        }
        ds.sort((a, b) -> a.getSoLuong() - b.getSoLuong());   // tồn ít nhất lên đầu
        Bang b = new Bang("Mã SP", "Loại", "Tên sản phẩm", "Tồn", "Tình trạng");
        for (SanPham sp : ds) {
            b.themDong(sp.getMaSP(), GiaoDien.loai(sp), sp.getTenSP(), sp.getSoLuong(),
                    sp.getSoLuong() == 0 ? "HẾT HÀNG" : "Sắp hết");
        }
        b.in("CẢNH BÁO SẮP HẾT HÀNG (tồn ≤ " + NGUONG_SAP_HET + ")");
    }
}
