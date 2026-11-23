/**
 * Một quyền (vai trò) của tài khoản: Nhân viên, Quản lý hoặc Admin.
 * Chương trình chỉ có đúng 3 quyền, khai báo sẵn bên dưới và dùng chung cho mọi tài khoản.
 */
public class QuyenHan {
    // ===== Các mức quyền trên một chức năng (số càng lớn càng nhiều quyền) =====
    public static final int KHONG = 0; // không được vào chức năng
    public static final int XEM = 1;   // chỉ được xem, tìm kiếm
    public static final int GHI = 2;   // xem + thêm + sửa (lập hóa đơn, lập phiếu nhập...)
    public static final int TOAN = 3;  // xem + thêm + sửa + xóa

    private String maQuyen;  // mã lưu trong file NhanVien.txt: NV / QL / AD
    private String tenQuyen; // tên hiển thị cho người dùng
    private String moTa;     // mô tả ngắn quyền này làm được gì

    // ===== 3 quyền có sẵn =====
    public final static QuyenHan Nhan_vien = new QuyenHan("NV", "Nhân viên",
            "bán hàng, nhập hàng, quản lý khách hàng (không xóa), quản lý hóa đơn; chỉ xem sản phẩm");
    public final static QuyenHan Quan_ly = new QuyenHan("QL", "Quản lý",
            "bán hàng, nhập hàng, quản lý sản phẩm, khách hàng, nhà cung cấp, nhân viên; chỉ xem hóa đơn");
    public final static QuyenHan Admin = new QuyenHan("AD", "Admin",
            "toàn quyền, kể cả thêm/xóa/sửa tài khoản của nhân viên");

    /** Danh sách cả 3 quyền, theo thứ tự: Nhân viên, Quản lý, Admin (dùng khi cho người dùng chọn quyền). */
    public final static QuyenHan[] TAT_CA = { Nhan_vien, Quan_ly, Admin };

    public QuyenHan() {
        maQuyen = "";
        tenQuyen = "";
        moTa = "";
    }

    public QuyenHan(String maQuyen, String tenQuyen, String moTa) {
        this.maQuyen = maQuyen;
        this.tenQuyen = tenQuyen;
        this.moTa = moTa;
    }

    /** Tìm quyền theo mã đọc từ file (NV / QL / AD). Không có thì trả về null. */
    public static QuyenHan timTheoMa(String ma) {
        for (QuyenHan q : TAT_CA) {
            if (q.maQuyen.equalsIgnoreCase(ma)) return q;
        }
        return null;
    }

    /**
     * BẢNG PHÂN QUYỀN: quyền này được làm đến mức nào trên chức năng cn.
     * Trả về một trong các hằng KHONG / XEM / GHI / TOAN ở đầu file.
     *
     *   Chức năng        | Nhân viên | Quản lý | Admin
     *   -----------------+-----------+---------+------
     *   Bán hàng         |    GHI    |   GHI   | TOAN
     *   Sản phẩm         |    XEM    |  TOAN   | TOAN   (nhân viên chỉ xem để tra mã, tồn kho)
     *   Khách hàng       |    GHI    |  TOAN   | TOAN   (nhân viên không được xóa khách)
     *   Hóa đơn          |    XEM    |   XEM   | TOAN   (xem danh sách, tìm và in lại, lịch sử theo khách)
     *   Nhập hàng        |    GHI    |   GHI   | TOAN
     *   Nhà cung cấp     |   KHONG   |  TOAN   | TOAN   (nhân viên vẫn chọn được nhà cung cấp khi lập phiếu nhập)
     *   Nhân viên        |   KHONG   |  TOAN   | TOAN
     *   Tài khoản        |   KHONG   |  KHONG  | TOAN
     *
     * Menu chính chỉ hiện những chức năng có mức quyền từ XEM trở lên (xem QuanLyCuaHang.menuChinh).
     */
    public int mucQuyen(ChucNang cn) {
        // Admin: toàn quyền mọi chức năng.
        if (maQuyen.equals("AD")) return TOAN;

        // Quản lý: quản lý hầu hết mọi thứ, trừ tài khoản; hóa đơn chỉ được xem.
        if (maQuyen.equals("QL")) {
            if (cn == ChucNang.TAI_KHOAN) return KHONG;
            if (cn == ChucNang.HOA_DON) return XEM;
            if (cn == ChucNang.BAN_HANG || cn == ChucNang.NHAP_HANG) return GHI;
            return TOAN;
        }

        // Nhân viên: làm việc ở quầy = bán hàng, nhập hàng, làm việc với khách, tra cứu hóa đơn; chỉ xem sản phẩm.
        // Các chức năng còn lại (nhà cung cấp, nhân viên, tài khoản) không thuộc việc của nhân viên.
        if (maQuyen.equals("NV")) {
            if (cn == ChucNang.BAN_HANG || cn == ChucNang.NHAP_HANG || cn == ChucNang.KHACH_HANG) return GHI;
            if (cn == ChucNang.SAN_PHAM || cn == ChucNang.HOA_DON) return XEM;
            return KHONG;
        }

        // Mã quyền lạ: không cho làm gì cả.
        return KHONG;
    }

    public String getMaQuyen() {
        return maQuyen;
    }

    public void setMaQuyen(String maQuyen) {
        this.maQuyen = maQuyen;
    }

    public String getTenQuyen() {
        return tenQuyen;
    }

    public void setTenQuyen(String tenQuyen) {
        this.tenQuyen = tenQuyen;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public void hienThi() {
        System.out.println("Ma quyen: " + maQuyen);
        System.out.println("Ten quyen: " + tenQuyen);
        System.out.println("Mo ta: " + moTa);
    }
}
