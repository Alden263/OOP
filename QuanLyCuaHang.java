import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Điều phối menu 1–7; dùng chung danh sách và lưu sau mỗi thay đổi thành công. */
public class QuanLyCuaHang {
    private final String tenCuaHang;
    private final Scanner sc;
    private final Path thuMucDuLieu;
    private DuLieuCuaHang duLieu;
    private boolean dangChay;
    // Mã nhân viên đang đăng nhập (null = chưa đăng nhập).
    // Chỉ lưu MÃ chứ không lưu đối tượng NhanVien, vì sau mỗi lần hủy/lỗi (xem thayDoi)
    // dữ liệu được nạp lại và đối tượng cũ không còn thuộc danh sách mới.
    private String maNguoiDung;

    public QuanLyCuaHang(String tenCuaHang) {
        this(tenCuaHang, new Scanner(System.in, java.nio.charset.StandardCharsets.UTF_8));
    }
    public QuanLyCuaHang(String tenCuaHang, Scanner sc) {
        this(tenCuaHang, sc, Paths.get("data"));
    }
    public QuanLyCuaHang(String tenCuaHang, Scanner sc, Path thuMucDuLieu) {
        if (tenCuaHang == null || tenCuaHang.isBlank() || sc == null || thuMucDuLieu == null)
            throw new IllegalArgumentException("Tên cửa hàng, Scanner và đường dẫn không được rỗng.");
        this.tenCuaHang = tenCuaHang;
        this.sc = sc;
        this.thuMucDuLieu = thuMucDuLieu;
    }

    public void chay() {
        try {
            napDuLieu();
            // Chưa có tài khoản nào (lần chạy đầu) thì tạo sẵn 3 tài khoản mẫu để có thể đăng nhập.
            boolean vuaTaoTaiKhoanMau = taoTaiKhoanMau();
            // Lần đầu nhập các tệp cũ vào một phiên bản đồng bộ, không ghi đè tệp gốc.
            if (vuaTaoTaiKhoanMau || !Files.exists(thuMucDuLieu.resolve("CURRENT"))) duLieu.luu();
            if (vuaTaoTaiKhoanMau) {
                System.out.println("Đã tạo tài khoản mẫu: admin, quanly, nhanvien (mật khẩu: "
                        + TaiKhoan.MAT_KHAU_MAC_DINH + ")");
            }
        } catch (Exception e) {
            System.out.println("Không thể mở dữ liệu. Chưa thay đổi tệp gốc: " + lyDo(e));
            return;
        }
        dangChay = true;
        System.out.println("\n===== " + tenCuaHang + " =====");
        System.out.println("Bán trực tiếp tại quầy. Mỗi giao dịch được ghi theo nhân viên đang đăng nhập.");
        // [UX] Đã bỏ dòng in đường dẫn thư mục dữ liệu (người dùng không cần xem).
        while (dangChay) {
            try {
                // Chưa đăng nhập thì bắt đăng nhập trước; đăng nhập xong mới vào menu chính.
                // Đăng nhập thất bại (sai quá 3 lần hoặc chọn thoát) thì kết thúc chương trình.
                if (maNguoiDung == null && !dangNhap()) dangChay = false;
                else menuChinh();
            }
            catch (NoSuchElementException e) { dangChay = false; }
            catch (Exception e) { System.out.println("Không thể thực hiện: " + lyDo(e)); }
        }
        System.out.println("Đã thoát chương trình.");
    }

    private void napDuLieu() throws Exception {
        DuLieuCuaHang moi = new DuLieuCuaHang(thuMucDuLieu);
        moi.doc();
        duLieu = moi; // Chỉ thay dữ liệu đang dùng khi đọc xong toàn bộ.
    }

    // ===================== ĐĂNG NHẬP / PHÂN QUYỀN =====================

    /** Nhân viên đang đăng nhập. Tìm lại theo mã mỗi lần vì dữ liệu có thể vừa được nạp lại (xem thayDoi). */
    private NhanVien nguoiDung() {
        return maNguoiDung == null ? null : duLieu.nhanVien.timKiemTheoMa(maNguoiDung);
    }

    /**
     * Người đang đăng nhập có đủ quyền "mucCan" (QuyenHan.XEM / GHI / TOAN) trên chức năng cn không?
     * Không đủ quyền thì in thông báo và trả về false để nơi gọi dừng lại.
     */
    private boolean duocPhep(ChucNang cn, int mucCan) {
        if (muc(cn) >= mucCan) return true;
        System.out.println("Bạn không có quyền thực hiện chức năng này.");
        return false;
    }

    /** Mức quyền (QuyenHan.KHONG / XEM / GHI / TOAN) của người đang đăng nhập trên chức năng cn. */
    private int muc(ChucNang cn) {
        NhanVien nv = nguoiDung();
        if (nv == null || nv.getTaiKhoan() == null) return QuyenHan.KHONG;
        return nv.getTaiKhoan().getQuyenHan().mucQuyen(cn);
    }

    /**
     * Mức quyền cần có cho một lựa chọn trong các menu quản lý (sản phẩm, khách hàng, nhà cung cấp, nhân viên).
     * Các menu này cùng quy ước: 1 = thêm, 2 = sửa, 3 = xóa, còn lại (tìm, xem danh sách...) = chỉ xem.
     */
    private int mucCanCua(int luaChon) {
        if (luaChon == 1 || luaChon == 2) return QuyenHan.GHI;   // thêm, sửa
        if (luaChon == 3) return QuyenHan.TOAN;                  // xóa
        return QuyenHan.XEM;                                     // tìm / xem
    }

    /**
     * Giống chonMenu nhưng CHỈ HIỆN những mục mà người đang đăng nhập đủ quyền (theo quy ước của mucCanCua).
     * Số hiển thị được đánh lại từ 1 cho liền nhau, nhưng giá trị trả về vẫn là số thứ tự GỐC của mục
     * trong danh sách "muc", để code xử lý lựa chọn ở nơi gọi không phải sửa.
     * Ví dụ nhân viên chỉ được xem sản phẩm: mục 1-3 (thêm/sửa/xóa) bị ẩn, mục "Tìm" hiện là số 1 nhưng trả về 4.
     */
    private int chonMenuTheoQuyen(String tieuDe, String mucSoKhong, ChucNang cn, String... muc) {
        int quyen = muc(cn);
        System.out.println("\n===== " + tieuDe + " =====");
        int[] soGoc = new int[muc.length];   // soGoc[k] = số thứ tự gốc của mục đang hiển thị ở vị trí k + 1
        int soMucHien = 0;
        for (int i = 1; i <= muc.length; i++) {
            if (quyen >= mucCanCua(i)) {          // đủ quyền thì mới hiện
                soGoc[soMucHien++] = i;
                System.out.println(soMucHien + ". " + muc[i - 1]);
            }
        }
        System.out.println("0. " + mucSoKhong);
        int chon = soNguyen("Chọn: ", 0, soMucHien);
        return chon == 0 ? 0 : soGoc[chon - 1];
    }

    /** Màn hình đăng nhập. Cho nhập sai tối đa 3 lần. Trả về true nếu đăng nhập thành công. */
    private boolean dangNhap() {
        for (int lan = 1; lan <= 3; lan++) {
            System.out.println("\n===== ĐĂNG NHẬP =====");
            String ten = chuoi("Tên đăng nhập (nhập 0 để thoát): ");
            if (ten.equals("0")) return false;
            String matKhau = nhapMatKhau("Mật khẩu: ");   // [UX] ẩn ký tự khi gõ (xem nhapMatKhau)
            NhanVien nv = duLieu.nhanVien.dangNhap(ten, matKhau);
            if (nv != null) {
                maNguoiDung = nv.getMa();   // ghi nhớ ai đang dùng chương trình
                System.out.println("Xin chào " + nv.getHoTen() + "!");
                return true;
            }
            // Cố ý không nói sai tên hay sai mật khẩu, để người lạ không dò được tên đăng nhập.
            System.out.println("Sai tên đăng nhập hoặc mật khẩu (lần " + lan + "/3).");
        }
        System.out.println("Nhập sai quá 3 lần.");
        return false;
    }

    /** Người đang đăng nhập tự đổi mật khẩu của mình. */
    private void doiMatKhau() {
        thayDoi(() -> {
            TaiKhoan tk = nguoiDung().getTaiKhoan();
            if (!tk.kiemTraMatKhau(nhapMatKhau("Mật khẩu hiện tại: "))) {   // [UX] cả 3 lần nhập mật khẩu đều ẩn ký tự
                System.out.println("Mật khẩu hiện tại không đúng.");
                return false;
            }
            String moi = nhapMatKhau("Mật khẩu mới (tối thiểu 3 ký tự): ");
            if (!moi.equals(nhapMatKhau("Nhập lại mật khẩu mới: "))) {
                System.out.println("Hai lần nhập không giống nhau.");
                return false;
            }
            tk.doiMatKhau(moi);
            return true;
        });
    }

    /**
     * Lần chạy đầu tiên (chưa nhân viên nào có tài khoản): tạo 3 nhân viên mẫu, mỗi người một quyền.
     * Trả về true nếu đã tạo (để nơi gọi lưu lại dữ liệu).
     */
    private boolean taoTaiKhoanMau() throws Exception {
        if (duLieu.nhanVien.coTaiKhoan()) return false;
        themNhanVienMau("Phát", "0900000001", "Quản trị hệ thống", 15000000, "admin", QuyenHan.Admin);
        themNhanVienMau("Tùng", "0900000002", "Quản lý", 12000000, "quanly", QuyenHan.Quan_ly);
        themNhanVienMau("Phú", "0900000003", "Nhân viên bán hàng", 8000000, "nhanvien", QuyenHan.Nhan_vien); // bán hàng và nhập hàng
        return true;
    }

    /** Thêm một nhân viên kèm tài khoản (mật khẩu mặc định) vào danh sách. Mã nhân viên tự sinh. */
    private void themNhanVienMau(String hoTen, String sdt, String chucVu, double luong,
                                 String tenDangNhap, QuyenHan quyen) throws Exception {
        TaiKhoan tk = new TaiKhoan(tenDangNhap, TaiKhoan.MAT_KHAU_MAC_DINH, quyen);
        duLieu.nhanVien.themNhanVien(new NhanVien(maMoi("NV"), hoTen, sdt, chucVu, luong, tk));
    }

    // ===================== MENU CHÍNH =====================

    private void menuChinh() {
        // Phòng trường hợp tài khoản đang dùng không còn nữa: quay lại màn hình đăng nhập.
        NhanVien nv = nguoiDung();
        if (nv == null || nv.getTaiKhoan() == null) { maNguoiDung = null; return; }

        System.out.println("\nĐang đăng nhập: " + nv.getHoTen()
                + " (" + nv.getTaiKhoan().getQuyenHan().getTenQuyen() + ")");
        // Menu chỉ chứa những chức năng người này được dùng, nên mỗi quyền thấy một menu khác nhau.
        // Hai danh sách dưới đây đi cặp với nhau: tên[i] là chữ hiển thị, viec[i] là việc làm khi chọn mục đó.
        List<String> ten = new ArrayList<>();
        List<Runnable> viec = new ArrayList<>();
        int sanPham = muc(ChucNang.SAN_PHAM);
        themMuc(ten, viec, muc(ChucNang.BAN_HANG) >= QuyenHan.GHI, "Bán hàng tại quầy", this::banHang);
        // Chỉ được xem thì đặt tên "Xem ..." cho đúng với những gì làm được.
        themMuc(ten, viec, sanPham >= QuyenHan.XEM,
                sanPham >= QuyenHan.GHI ? "Quản lý sản phẩm" : "Xem sản phẩm", this::menuSanPham);
        themMuc(ten, viec, muc(ChucNang.KHACH_HANG) >= QuyenHan.XEM, "Quản lý khách hàng", this::menuKhachHang);
        themMuc(ten, viec, muc(ChucNang.HOA_DON) >= QuyenHan.XEM, "Quản lý hóa đơn", this::menuHoaDon);
        themMuc(ten, viec, muc(ChucNang.NHAP_HANG) >= QuyenHan.XEM, "Nhập hàng", this::menuNhapHang);
        themMuc(ten, viec, muc(ChucNang.NHA_CUNG_CAP) >= QuyenHan.XEM, "Quản lý nhà cung cấp", this::menuNhaCungCap);
        themMuc(ten, viec, muc(ChucNang.NHAN_VIEN) >= QuyenHan.XEM, "Quản lý nhân viên", this::menuNhanVien);
        themMuc(ten, viec, muc(ChucNang.TAI_KHOAN) >= QuyenHan.XEM, "Quản lý tài khoản", this::menuTaiKhoan);
        // [UX] Thống kê: chỉ Quản lý và Admin thấy (Nhân viên có mức KHONG nên mục này bị ẩn).
        themMuc(ten, viec, muc(ChucNang.THONG_KE) >= QuyenHan.XEM, "Thống kê", this::menuThongKe);
        themMuc(ten, viec, true, "Đổi mật khẩu", this::doiMatKhau);   // ai cũng được đổi mật khẩu của mình

        int chon = chonMenu("MENU CHÍNH", "Đăng xuất", ten.toArray(new String[0]));
        if (chon == 0) {
            maNguoiDung = null;
            System.out.println("Đã đăng xuất.");
        } else {
            viec.get(chon - 1).run();   // chạy việc ứng với mục vừa chọn
        }
    }

    /** Thêm một mục vào menu chính, nhưng chỉ khi coQuyen = true (người dùng được phép dùng chức năng đó). */
    private void themMuc(List<String> ten, List<Runnable> viec, boolean coQuyen, String nhan, Runnable viecCanLam) {
        if (!coQuyen) return;
        ten.add(nhan);
        viec.add(viecCanLam);
    }

    // ===================== QUẢN LÝ TÀI KHOẢN (chỉ Admin) =====================

    private void menuTaiKhoan() {
        while (dangChay) {
            int chon = chonMenu("TÀI KHOẢN", "Quay lại", "Xem danh sách tài khoản",
                    "Cấp tài khoản cho nhân viên", "Đổi quyền tài khoản",
                    "Đặt lại mật khẩu về " + TaiKhoan.MAT_KHAU_MAC_DINH, "Xóa tài khoản");
            if (chon == 0) return;
            if (chon == 1) hienThiTaiKhoan();
            else if (chon == 2) thayDoi(() -> {
                // Cấp tài khoản mới cho nhân viên chưa có.
                NhanVien nv = timNhanVien(); if (nv == null) return false;
                if (nv.getTaiKhoan() != null) { System.out.println("Nhân viên này đã có tài khoản."); return false; }
                String ten = tenDangNhapMoi();
                nv.setTaiKhoan(new TaiKhoan(ten, TaiKhoan.MAT_KHAU_MAC_DINH, chonQuyen()));
                System.out.println("Đã cấp tài khoản. Mật khẩu mặc định: " + TaiKhoan.MAT_KHAU_MAC_DINH);
                return true;
            });
            else if (chon == 3) thayDoi(() -> {
                // Đổi quyền: không cho đổi quyền của chính mình (tránh tự hạ quyền rồi hệ thống không còn Admin).
                NhanVien nv = timNhanVienCoTaiKhoan(true); if (nv == null) return false;
                nv.getTaiKhoan().setQuyenHan(chonQuyen());
                return true;
            });
            else if (chon == 4) thayDoi(() -> {
                // Đặt lại mật khẩu (khi nhân viên quên). Cho phép cả với chính mình.
                NhanVien nv = timNhanVienCoTaiKhoan(false); if (nv == null) return false;
                nv.getTaiKhoan().doiMatKhau(TaiKhoan.MAT_KHAU_MAC_DINH);
                System.out.println("Đã đặt lại mật khẩu về " + TaiKhoan.MAT_KHAU_MAC_DINH);
                return true;
            });
            else thayDoi(() -> {
                // Xóa tài khoản: nhân viên vẫn còn trong danh sách, chỉ mất khả năng đăng nhập.
                // Không cho tự xóa mình để luôn còn ít nhất một Admin.
                NhanVien nv = timNhanVienCoTaiKhoan(true); if (nv == null) return false;
                // [UX] Hỏi xác nhận, in rõ tài khoản sắp xóa
                if (!xacNhanXoa("tài khoản " + nv.getTaiKhoan().getTenDangNhap()
                        + " của nhân viên " + nv.getMa() + " - " + nv.getHoTen())) return false;
                nv.setTaiKhoan(null);
                System.out.println("Đã xóa tài khoản. Nhân viên " + nv.getMa() + " vẫn còn trong danh sách.");
                return true;
            });
        }
    }

    /** In danh sách tài khoản dạng bảng: mã nhân viên | họ tên | tên đăng nhập | quyền. */
    private void hienThiTaiKhoan() {
        GiaoDien.taiKhoan(duLieu.nhanVien.layDanhSach());
    }

    /**
     * Chọn một nhân viên đã có tài khoản (nhập mã nhân viên).
     * khongChoChinhMinh = true: không cho chọn chính người đang đăng nhập.
     * Trả về null (kèm thông báo) nếu không chọn được.
     */
    private NhanVien timNhanVienCoTaiKhoan(boolean khongChoChinhMinh) {
        NhanVien nv = timNhanVien();
        if (nv == null) return null;
        if (nv.getTaiKhoan() == null) {
            System.out.println("Nhân viên này chưa có tài khoản.");
            return null;
        }
        if (khongChoChinhMinh && nv.getMa().equalsIgnoreCase(maNguoiDung)) {
            System.out.println("Không thể thực hiện với chính tài khoản đang đăng nhập.");
            return null;
        }
        return nv;
    }

    /** Hỏi tên đăng nhập mới, hỏi lại cho đến khi hợp lệ và chưa ai dùng. */
    private String tenDangNhapMoi() {
        while (true) {
            String ten = chuoi("Tên đăng nhập mới (3-20 ký tự: chữ không dấu, số, _ hoặc .): ");
            if (!TaiKhoan.hopLeTenDangNhap(ten)) System.out.println("Tên đăng nhập không hợp lệ.");
            else if (duLieu.nhanVien.timTheoTenDangNhap(ten) != null) System.out.println("Tên đăng nhập đã có người dùng.");
            else return ten;
        }
    }

    /** Cho người dùng chọn 1 trong 3 quyền. */
    private QuyenHan chonQuyen() {
        int so = soNguyen("Quyền: 1 Nhân viên, 2 Quản lý, 3 Admin: ", 1, 3);
        return QuyenHan.TAT_CA[so - 1];   // TAT_CA xếp đúng thứ tự: Nhân viên, Quản lý, Admin
    }

    // ===================== [UX] THỐNG KÊ (Quản lý và Admin) =====================

    /** [UX] Menu thống kê: mỗi mục in một bảng (việc tính toán nằm trong lớp ThongKe). */
    private void menuThongKe() {
        if (!duocPhep(ChucNang.THONG_KE, QuyenHan.XEM)) return;   // phòng hờ: Nhân viên không vào được
        while (dangChay) {
            int chon = chonMenu("THỐNG KÊ", "Quay lại", "Doanh thu theo ngày", "Doanh thu theo nhân viên",
                    "Sản phẩm bán chạy", "Cảnh báo sắp hết hàng (tồn ≤ " + ThongKe.NGUONG_SAP_HET + ")");
            if (chon == 0) return;
            if (chon == 1) ThongKe.doanhThuTheoNgay(duLieu);
            else if (chon == 2) ThongKe.doanhThuTheoNhanVien(duLieu);
            else if (chon == 3) ThongKe.sanPhamBanChay(duLieu);
            else ThongKe.canhBaoSapHetHang(duLieu);
        }
    }

    private void menuSanPham() {
        while (dangChay) {
            int chon = chonMenuTheoQuyen("SẢN PHẨM", "Quay lại", ChucNang.SAN_PHAM, "Thêm sản phẩm mới (tồn 0)",
                    "Sửa thông tin / giá bán", "Xóa sản phẩm chưa có giao dịch",
                    "Tìm theo mã / tên / loại / seri", "Hiển thị danh sách", "Xem tồn kho",
                    "Xem seri theo sản phẩm");   // [SERI] mục 7: bảng seri riêng của từng sản phẩm
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> {
                // Phần hỏi thông tin được tách ra hàm taoSanPhamMoi() để dùng chung với chức năng nhập hàng.
                SanPham moi = taoSanPhamMoi();
                // [UX] themSanPham của lớp gốc tự in "✔ Thêm sản phẩm thành công..." nên chạy im lặng, rồi tự in thông báo rõ hơn.
                imLang(() -> { duLieu.sanPham.themSanPham(moi); return true; });
                System.out.println("Đã thêm sản phẩm mới: " + moi.getMaSP() + " - " + moi.getTenSP()
                        + " (tồn kho 0, dùng chức năng Nhập hàng để nhập máy và seri).");
                return true;
            });
            else if (chon == 2) thayDoi(() -> {
                SanPham sp = timSanPham();
                if (sp == null) return false;
                // [UX] In thông tin hiện tại, mỗi trường nhấn Enter để giữ nguyên
                GiaoDien.sanPham("THÔNG TIN HIỆN TẠI", java.util.List.of(sp));
                String ten = chuoiGiuNguyen("Tên", sp.getTenSP());
                double gia = soThucGiuNguyen("Giá bán", sp.getDonGia(), 0, 1e15);
                sp.setTenSP(ten); sp.setDonGia(gia);
                System.out.println("Đã cập nhật sản phẩm " + sp.getMaSP() + ": " + ten + ", giá bán " + Bang.tien(gia) + " đ.");
                return true; // Không sửa mã/tồn kho trực tiếp.
            });
            else if (chon == 3) thayDoi(() -> {
                SanPham sp = timSanPham();
                if (sp == null) return false;
                if (coLichSu("SP", sp.getMaSP()) || sp.getSoLuong() != 0) {
                    System.out.println("Không xóa sản phẩm có giao dịch hoặc còn tồn kho."); return false;
                }
                // [UX] Hỏi xác nhận, in rõ sản phẩm sắp xóa
                if (!xacNhanXoa("sản phẩm " + sp.getMaSP() + " - " + sp.getTenSP())) return false;
                imLang(() -> duLieu.sanPham.xoaSanPham(sp.getMaSP()));
                System.out.println("Đã xóa sản phẩm " + sp.getMaSP() + " - " + sp.getTenSP() + ".");
                return true;
            });
            else if (chon == 4) {
                String tu = chuoi("Nhập mã / tên / seri / Laptop / PC / LinhKien: ").toLowerCase(java.util.Locale.ROOT);
                SeriSanPham may = duLieu.seri.tim(tu);   // [SERI] gõ đúng một seri thì ra sản phẩm của máy đó
                ArrayList<SanPham> ketQua = new ArrayList<>();
                for (SanPham sp : duLieu.sanPham.getDsSanPham()) {
                    boolean khopSeri = may != null && may.getMaSP().equalsIgnoreCase(sp.getMaSP());
                    if (khopSeri || (sp.getMaSP() + " " + sp.getTenSP() + " " + sp.getClass().getSimpleName())
                            .toLowerCase(java.util.Locale.ROOT).contains(tu)) ketQua.add(sp);
                }
                if (ketQua.isEmpty()) System.out.println("Không tìm thấy sản phẩm.");
                else GiaoDien.sanPham("KẾT QUẢ TÌM SẢN PHẨM", ketQua);
            } else if (chon == 5) GiaoDien.sanPham("DANH SÁCH SẢN PHẨM", duLieu.sanPham.getDsSanPham());
            else if (chon == 6) GiaoDien.tonKho(duLieu.sanPham.getDsSanPham());
            else xemSeriSanPham();   // [SERI]
        }
    }

    /** [SERI] Bảng seri của MỘT sản phẩm: từng máy còn trong kho hay đã bán, nhập từ phiếu nào, bán ở hóa đơn nào. */
    private void xemSeriSanPham() {
        GiaoDien.tonKho(duLieu.sanPham.getDsSanPham());   // in danh sách để người dùng chọn mã
        SanPham sp = timSanPham();
        if (sp == null) return;
        ArrayList<SeriSanPham> ds = duLieu.seri.theoSanPham(sp.getMaSP());
        GiaoDien.seri("SERI CỦA " + sp.getMaSP() + " - " + sp.getTenSP(), ds);
        int trongKho = duLieu.seri.demTrongKho(sp.getMaSP());
        System.out.println("Trong kho: " + trongKho + " máy | Đã bán: " + (ds.size() - trongKho) + " máy");
    }

    /**
     * Hỏi thông tin và tạo một sản phẩm mới (tồn kho 0, chưa có máy nào nên chưa có seri).
     * Dùng chung cho: menu Sản phẩm > Thêm, và Nhập hàng > Sản phẩm mới.
     */
    private SanPham taoSanPhamMoi() {
        int loai = soNguyen("Loại: 1 Laptop, 2 PC, 3 Linh kiện: ", 1, 3);
        String ma = maMoi(loai == 1 ? "LT" : loai == 2 ? "PC" : "LK");
        String ten = chuoi("Tên sản phẩm: ");
        double gia = soThuc("Giá bán: ", 0, 1e15);
        SanPham sp;
        if (loai == 1) sp = new Laptop(ma, ten, gia, 0, chuoi("CPU: "),
                soNguyen("RAM (GB): ", 1, 100000), soNguyen("SSD (GB): ", 1, 10000000));
        else if (loai == 2) sp = new PC(ma, ten, gia, 0, chuoi("CPU: "), chuoi("GPU: "),
                soNguyen("RAM (GB): ", 1, 100000), soNguyen("SSD (GB): ", 1, 10000000));
        else sp = new LinhKien(ma, ten, gia, 0, chuoi("Loại linh kiện: "), chuoi("Hãng sản xuất: "));
        return sp;
    }

    private void menuKhachHang() {
        while (dangChay) {
            int chon = chonMenuTheoQuyen("KHÁCH HÀNG", "Quay lại", ChucNang.KHACH_HANG, "Thêm khách", "Sửa khách",
                    "Xóa khách chưa có giao dịch", "Tìm theo mã / tên / số điện thoại", "Xem danh sách", "Lịch sử mua hàng");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> { themKhach(); return true; });
            else if (chon == 2) thayDoi(() -> {
                KhachHang kh = duLieu.khachHang.timTheoMa(chuoi("Mã khách: "));
                if (kh == null) { System.out.println("Không tìm thấy khách."); return false; }
                if (kh.getMa().equalsIgnoreCase("KHLE")) { System.out.println("Không sửa khách lẻ chung."); return false; }
                // [UX] In thông tin hiện tại, mỗi trường nhấn Enter để giữ nguyên
                GiaoDien.khachHang("THÔNG TIN HIỆN TẠI", new KhachHang[] { kh }, duLieu);
                String ten = chuoiGiuNguyen("Họ tên", kh.getHoTen());
                String sdt;
                while (true) {   // số điện thoại của khách khác thì báo ngay và hỏi lại, không bắt gõ lại từ đầu
                    sdt = dienThoaiGiuNguyen(kh.getSoDienThoai());
                    KhachHang trung = duLieu.khachHang.timTheoSoDienThoai(sdt);
                    if (trung == null || trung == kh) break;
                    System.out.println("Số điện thoại đã có hồ sơ của khách " + trung.getMa() + " - " + trung.getHoTen() + ".");
                }
                String diaChi = chuoiGiuNguyen("Địa chỉ", kh.getDiaChi());
                kh.setHoTen(ten); kh.setSoDienThoai(sdt); kh.setDiaChi(diaChi);
                System.out.println("Đã cập nhật khách hàng " + kh.getMa() + ".");
                return true;
            });
            else if (chon == 3) thayDoi(() -> {
                String ma = chuoi("Mã khách: ");
                if (ma.equalsIgnoreCase("KHLE") || coLichSu("KH", ma)) {
                    System.out.println("Không xóa khách lẻ chung hoặc khách đã mua hàng."); return false;
                }
                KhachHang kh = duLieu.khachHang.timTheoMa(ma);
                if (kh == null) { System.out.println("Không tìm thấy khách."); return false; }
                // [UX] Hỏi xác nhận, in rõ khách sắp xóa
                if (!xacNhanXoa("khách hàng " + kh.getMa() + " - " + kh.getHoTen())) return false;
                imLang(() -> duLieu.khachHang.xoaKhachHang(kh.getMa()));   // lớp gốc tự in "Xóa khách hàng thành công!"
                System.out.println("Đã xóa khách hàng " + kh.getMa() + " - " + kh.getHoTen() + ".");
                return true;
            });
            else if (chon == 4) {
                String tu = chuoi("Mã / tên / số điện thoại: ").toLowerCase(java.util.Locale.ROOT);
                ArrayList<KhachHang> ketQua = new ArrayList<>();
                for (KhachHang kh : duLieu.khachHang.layDanhSach())
                    if ((kh.getMa()+" "+kh.getHoTen()+" "+kh.getSoDienThoai()).toLowerCase(java.util.Locale.ROOT).contains(tu)) {
                        ketQua.add(kh);
                    }
                if (ketQua.isEmpty()) System.out.println("Không tìm thấy khách.");
                else GiaoDien.khachHang("KẾT QUẢ TÌM KHÁCH HÀNG", ketQua.toArray(new KhachHang[0]), duLieu);
            } else if (chon == 5) GiaoDien.khachHang("DANH SÁCH KHÁCH HÀNG", duLieu.khachHang.layDanhSach(), duLieu);
            else lichSuKhach(chuoi("Mã khách: "));
        }
    }

    /** Bảng các hóa đơn của một khách hàng. */
    private void lichSuKhach(String maKH) {
        ArrayList<HoaDon> ds = new ArrayList<>();
        for (HoaDon hd : duLieu.hoaDon.getDs()) if (hd.getMaKH().equalsIgnoreCase(maKH)) ds.add(hd);
        if (ds.isEmpty()) System.out.println("Khách " + maKH + " chưa có hóa đơn nào.");
        else GiaoDien.danhSachHoaDon("LỊCH SỬ MUA HÀNG CỦA " + maKH, ds, duLieu);
        // [UX] Tích điểm: in điểm đang có của khách (khách có hồ sơ, không phải khách lẻ chung)
        if (DiemTichLuy.coTichDiem(maKH) && duLieu.khachHang.timTheoMa(maKH) != null) {
            System.out.println("Điểm tích lũy hiện có: " + DiemTichLuy.diemHienCo(maKH, duLieu.hoaDon) + " điểm.");
        }
    }

    private KhachHang themKhach() {
        String ten = chuoi("Họ tên khách: "); String sdt = dienThoai();
        if (duLieu.khachHang.timTheoSoDienThoai(sdt) != null)
            throw new IllegalArgumentException("Số điện thoại đã có hồ sơ khách hàng.");
        KhachHang kh = new KhachHang(maMoi("KH"), ten, sdt, chuoi("Địa chỉ: "));
        duLieu.khachHang.themKhachHang(kh); System.out.println("Mã khách: " + kh.getMa()); return kh;
    }

    private void menuNhanVien() {
        while (dangChay) {
            int chon = chonMenuTheoQuyen("NHÂN VIÊN", "Quay lại", ChucNang.NHAN_VIEN, "Thêm nhân viên", "Sửa nhân viên",
                    "Xóa nhân viên chưa có giao dịch", "Tìm nhân viên", "Xem danh sách");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> {
                NhanVien nv = new NhanVien(maMoi("NV"), chuoi("Họ tên: "), dienThoai(),
                        chuoi("Chức vụ: "), soThuc("Lương: ",0,1e15), null);
                duLieu.nhanVien.themNhanVien(nv); System.out.println("Mã nhân viên: " + nv.getMa());
                System.out.println("Nhân viên mới chưa có tài khoản, nhờ Admin cấp ở mục Quản lý tài khoản."); return true;
            });
            else if (chon == 2) thayDoi(() -> {
                NhanVien nv = timNhanVien(); if (nv == null) return false;
                // [UX] In thông tin hiện tại, mỗi trường nhấn Enter để giữ nguyên
                GiaoDien.nhanVien("THÔNG TIN HIỆN TẠI", new NhanVien[] { nv });
                String ten = chuoiGiuNguyen("Họ tên", nv.getHoTen());
                String sdt = dienThoaiGiuNguyen(nv.getSoDienThoai());
                String chucVu = chuoiGiuNguyen("Chức vụ", nv.getChucVu());
                // [UX] Không ai được TỰ sửa lương của chính mình, trừ Admin (người có toàn quyền trên tài khoản).
                //      Quản lý muốn đổi lương của mình phải nhờ Admin. Các thông tin khác của mình vẫn sửa được.
                double luong = nv.getLuong();
                boolean tuSuaLuong = nv.getMa().equalsIgnoreCase(maNguoiDung) && muc(ChucNang.TAI_KHOAN) < QuyenHan.TOAN;
                if (tuSuaLuong) System.out.println("Lương: bạn không được tự sửa lương của chính mình, hãy nhờ Admin (giữ nguyên "
                        + Bang.tien(luong) + " đ).");
                else luong = soThucGiuNguyen("Lương", luong, 0, 1e15);
                nv.setHoTen(ten); nv.setSoDienThoai(sdt); nv.setChucVu(chucVu); nv.setLuong(luong);
                System.out.println("Đã cập nhật nhân viên " + nv.getMa() + ".");
                return true;
            });
            else if (chon == 3) thayDoi(() -> {
                NhanVien nv = timNhanVien(); if (nv == null) return false;
                // Nhân viên còn tài khoản thì phải để Admin xóa tài khoản trước (tránh lỡ xóa mất Admin).
                if (nv.getTaiKhoan() != null) { System.out.println("Nhân viên đang có tài khoản, nhờ Admin xóa tài khoản trước."); return false; }
                if (coLichSu("NV", nv.getMa())) { System.out.println("Nhân viên đã có giao dịch, không xóa."); return false; }
                // [UX] Hỏi xác nhận, in rõ nhân viên sắp xóa
                if (!xacNhanXoa("nhân viên " + nv.getMa() + " - " + nv.getHoTen())) return false;
                duLieu.nhanVien.xoaNhanVien(nv);
                System.out.println("Đã xóa nhân viên " + nv.getMa() + " - " + nv.getHoTen() + ".");
                return true;
            });
            else if (chon == 4) {
                String tu = chuoi("Mã hoặc tên nhân viên: ").toLowerCase(java.util.Locale.ROOT);
                ArrayList<NhanVien> ketQua = new ArrayList<>();
                for (NhanVien nv : duLieu.nhanVien.layDanhSach())
                    if ((nv.getMa()+" "+nv.getHoTen()).toLowerCase(java.util.Locale.ROOT).contains(tu)) {
                        ketQua.add(nv);
                    }
                if (ketQua.isEmpty()) System.out.println("Không tìm thấy nhân viên.");
                else GiaoDien.nhanVien("KẾT QUẢ TÌM NHÂN VIÊN", ketQua.toArray(new NhanVien[0]));
            } else GiaoDien.nhanVien("DANH SÁCH NHÂN VIÊN", duLieu.nhanVien.layDanhSach());
        }
    }

    private void menuNhaCungCap() {
        while (dangChay) {
            int chon = chonMenuTheoQuyen("NHÀ CUNG CẤP", "Quay lại", ChucNang.NHA_CUNG_CAP, "Thêm", "Sửa", "Xóa nếu chưa có phiếu nhập", "Tìm", "Xem danh sách");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> {
                NhaCungCap moi = new NhaCungCap(duLieu.nhaCungCap.sinhMa(), chuoi("Tên nhà cung cấp: "),
                        chuoi("Địa chỉ: "), dienThoai());
                if (!duLieu.nhaCungCap.them(moi)) return false;
                System.out.println("Đã thêm nhà cung cấp " + moi.getMaNCC() + " - " + moi.getTenNCC() + ".");   // [UX]
                return true;
            });
            else if (chon == 2) thayDoi(() -> {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                if (ncc == null) { System.out.println("Không tìm thấy nhà cung cấp."); return false; }
                // [UX] In thông tin hiện tại, mỗi trường nhấn Enter để giữ nguyên
                GiaoDien.nhaCungCap("THÔNG TIN HIỆN TẠI", new NhaCungCap[] { ncc });
                String ten = chuoiGiuNguyen("Tên", ncc.getTenNCC());
                String diaChi = chuoiGiuNguyen("Địa chỉ", ncc.getDiaChi());
                String sdt = dienThoaiGiuNguyen(ncc.getSoDienThoai());
                ncc.setTenNCC(ten); ncc.setDiaChi(diaChi); ncc.setSoDienThoai(sdt);
                System.out.println("Đã cập nhật nhà cung cấp " + ncc.getMaNCC() + ".");
                return true;
            });
            else if (chon == 3) thayDoi(() -> {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                if (ncc == null) { System.out.println("Không tìm thấy nhà cung cấp."); return false; }
                // [UX] Tự kiểm tra phiếu nhập ở đây để báo lỗi tiếng Việt có dấu (lớp gốc in chữ không dấu)
                if (duLieu.phieuNhap.coPhieuCuaNCC(ncc.getMaNCC())) {
                    System.out.println("Không thể xóa nhà cung cấp " + ncc.getMaNCC() + " vì đã có phiếu nhập.");
                    return false;
                }
                if (!xacNhanXoa("nhà cung cấp " + ncc.getMaNCC() + " - " + ncc.getTenNCC())) return false;
                duLieu.nhaCungCap.xoa(ncc.getMaNCC());
                System.out.println("Đã xóa nhà cung cấp " + ncc.getMaNCC() + " - " + ncc.getTenNCC() + ".");
                return true;
            });
            else if (chon == 4) {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                if (ncc == null) System.out.println("Không tìm thấy nhà cung cấp.");
                else GiaoDien.nhaCungCap("NHÀ CUNG CẤP " + ncc.getMaNCC(), new NhaCungCap[] { ncc });
            } else GiaoDien.nhaCungCap("DANH SÁCH NHÀ CUNG CẤP", duLieu.nhaCungCap.layDanhSach());
        }
    }

    private void menuHoaDon() {
        while (dangChay) {
            int chon = chonMenu("HÓA ĐƠN ĐÃ BÁN", "Quay lại", "Xem danh sách", "Tìm và in lại theo mã", "Lịch sử theo khách hàng",
                    "Tra cứu máy theo seri (bảo hành)");   // [SERI] thêm mục 4
            if (chon == 0) return;
            if (chon == 1) GiaoDien.danhSachHoaDon("DANH SÁCH HÓA ĐƠN",
                    java.util.Arrays.asList(duLieu.hoaDon.getDs()), duLieu);
            else if (chon == 2) {
                HoaDon hd = duLieu.hoaDon.timHoaDon(chuoi("Mã hóa đơn: "));
                if (hd == null) System.out.println("Không tìm thấy hóa đơn."); else GiaoDien.hoaDon(hd, duLieu);
            } else if (chon == 3) lichSuKhach(chuoi("Mã khách: "));
            else traCuuSeri();   // [SERI]
        }
    }

    public void banHang() { thayDoi(this::lapHoaDon); }

    private boolean lapHoaDon() throws Exception {
        if (duLieu.sanPham.getDsSanPham().isEmpty() || duLieu.nhanVien.layDanhSach().length == 0) {
            System.out.println("Cần có sản phẩm và nhân viên trước khi bán."); return false;
        }
        // Kiểm tra quyền bán hàng, rồi lấy luôn người đang đăng nhập làm nhân viên lập hóa đơn.
        if (!duocPhep(ChucNang.BAN_HANG, QuyenHan.GHI)) return false;
        NhanVien nv = nguoiDung();
        System.out.println("Nhân viên lập: " + nv.getHoTen() + " (" + nv.getMa() + ")");
        // [UX] Chọn khách: gõ sai thì HỎI LẠI chứ không hủy cả hóa đơn (xem chonKhachHang)
        KhachHang kh = chonKhachHang();
        if (kh == null) { System.out.println("Đã hủy, tồn kho không thay đổi."); return false; }
        // [UX] Chọn bán lẻ hoặc bán sỉ (xem taoHoaDon)
        HoaDon hd = taoHoaDon(kh, nv);
        if (hd == null) { System.out.println("Đã hủy, tồn kho không thay đổi."); return false; }
        // [SERI] Seri các máy chọn bán, theo từng mã sản phẩm. Seri KHÔNG in trên hóa đơn,
        //        chỉ lưu vào file seri riêng (SeriSanPham.txt) khi xác nhận thanh toán.
        HashMap<String, ArrayList<String>> seriBan = new HashMap<>();
        while (true) {
            int chon = chonMenu("HÓA ĐƠN TẠM " + hd.getMaHD(), "Hủy hóa đơn", "Thêm sản phẩm", "Đổi số lượng", "Bỏ sản phẩm", "Xem hóa đơn", "Xác nhận thanh toán");
            if (chon == 0) { System.out.println("Đã hủy, tồn kho không thay đổi."); return false; }
            if (chon == 1) {
                GiaoDien.sanPhamDeBan(duLieu.sanPham.getDsSanPham());   // [UX] bảng gọn, ẩn sản phẩm hết hàng
                SanPham sp = timSanPham(); if (sp == null) continue;
                int daChon = 0;
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(sp.getMaSP())) daChon = ct.getSoLuong();
                int conLai = sp.getSoLuong() - daChon;
                if (conLai <= 0) { System.out.println("Không đủ tồn kho để thêm."); continue; }
                int sl = soNguyen("Số lượng thêm: ",1,conLai);
                // [SERI] Bán bao nhiêu máy thì chọn bấy nhiêu seri, để biết chính xác máy nào đã bán cho khách.
                ArrayList<String> seriChon = chonSeriDeBan(sp, sl, seriBan);
                if (seriChon == null) continue;   // người dùng gõ 0 để hủy
                if (!seriBan.containsKey(sp.getMaSP())) seriBan.put(sp.getMaSP(), new ArrayList<>());
                seriBan.get(sp.getMaSP()).addAll(seriChon);
                hd.themChiTiet(new ChiTietHoaDon(hd.getMaHD(),sp.getMaSP(),sl,sp.tinhGiaBan()));
                GiaoDien.gioHang(hd, duLieu);   // [UX] tự in giỏ hàng + tổng tiền
            } else if (chon == 2) {
                String ma = chuoi("Mã sản phẩm đã chọn: ");
                ChiTietHoaDon ct = chiTiet(hd, ma);
                if (ct == null) { System.out.println("Sản phẩm không có trong hóa đơn."); continue; }
                SanPham sp = duLieu.sanPham.searchSanPham(ma);
                if (sp.getSoLuong() == 0) { System.out.println("Sản phẩm hết hàng."); continue; }
                int slMoi = soNguyen("Số lượng mới: ",1,sp.getSoLuong());
                // [SERI] Đổi số lượng thì chọn lại seri cho đủ số máy mới. Hủy giữa chừng thì giữ lại seri cũ.
                ArrayList<String> seriCu = seriBan.remove(sp.getMaSP());
                ArrayList<String> seriMoi = chonSeriDeBan(sp, slMoi, seriBan);
                if (seriMoi == null) { seriBan.put(sp.getMaSP(), seriCu); continue; }
                seriBan.put(sp.getMaSP(), seriMoi);
                ct.setSoLuong(slMoi);
                GiaoDien.gioHang(hd, duLieu);   // [UX] tự in giỏ hàng + tổng tiền
            } else if (chon == 3) {
                String ma = chuoi("Mã sản phẩm cần bỏ: ");
                if (!hd.xoaChiTiet(ma)) System.out.println("Không có sản phẩm trong hóa đơn.");
                else {
                    SanPham sp = duLieu.sanPham.searchSanPham(ma);
                    if (sp != null) seriBan.remove(sp.getMaSP());   // [SERI] bỏ luôn các máy đã chọn
                    GiaoDien.gioHang(hd, duLieu);   // [UX] tự in giỏ hàng + tổng tiền
                }
            } else if (chon == 4) GiaoDien.hoaDon(hd, duLieu);
            else {
                if (hd.getSoLuongChiTiet() == 0) { System.out.println("Hóa đơn phải có sản phẩm."); continue; }
                hoiDungDiem(hd, kh);   // [UX] tích điểm: khách có điểm thì hỏi có dùng để giảm giá không
                GiaoDien.hoaDon(hd, duLieu);
                if (!xacNhan()) return false;
                // Kiểm tra toàn bộ trước khi giảm bất kỳ sản phẩm nào.
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getMaSP());
                    if (sp == null || sp.getSoLuong() < ct.getSoLuong()) throw new IllegalStateException("Không đủ hàng.");
                    // [SERI] từng máy đã chọn phải vẫn còn trong kho
                    for (String seri : seriBan.get(sp.getMaSP())) {
                        if (!duLieu.seri.tim(seri).conTrongKho()) throw new IllegalStateException("Máy " + seri + " không còn trong kho.");
                    }
                }
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getMaSP());
                    sp.setSoLuong(sp.getSoLuong()-ct.getSoLuong());
                    // [SERI] đánh dấu từng máy đã bán ở hóa đơn này (lưu vào file seri riêng)
                    for (String seri : seriBan.get(sp.getMaSP())) duLieu.seri.tim(seri).ban(hd.getMaHD());
                }
                // [UX] themHoaDon của lớp gốc tự in "Them hoa don ... thanh cong!" nên chạy im lặng, rồi tự in thông báo có dấu
                if (!imLang(() -> duLieu.hoaDon.themHoaDon(hd))) throw new IllegalStateException("Không thêm được hóa đơn.");
                System.out.println("Đã thanh toán hóa đơn " + hd.getMaHD() + ": " + Bang.tien(hd.getTongTien()) + " đ.");
                // [UX] Tích điểm: báo điểm vừa được cộng và tổng điểm hiện có (đã tính cả hóa đơn này)
                int diemCong = DiemTichLuy.diemKiemDuoc(hd);
                if (diemCong > 0) {
                    System.out.println("Khách " + kh.getHoTen() + " được cộng " + diemCong + " điểm, hiện có "
                            + DiemTichLuy.diemHienCo(kh.getMa(), duLieu.hoaDon) + " điểm.");
                }
                return true;
            }
        }
    }

    // ===================== [UX] CHỌN KHÁCH VÀ LOẠI HÓA ĐƠN LÚC BÁN =====================

    /**
     * [UX] Chọn khách khi bán hàng: hỏi thẳng SỐ ĐIỆN THOẠI khách (gõ mã khách như KH001 cũng được).
     *   - Khách đã có hồ sơ        -> chọn luôn, sang bước tiếp theo.
     *   - Số điện thoại chưa có    -> nhập thêm khách mới (họ tên, địa chỉ), lưu ngay rồi sang bước tiếp theo.
     *   - Enter (bỏ trống)         -> khách lẻ, không cần hồ sơ.
     *   - 0                        -> hủy hóa đơn (trả về null).
     * Gõ sai thì hỏi lại chứ không hủy cả hóa đơn.
     */
    private KhachHang chonKhachHang() throws Exception {
        while (true) {
            String tu = docDong("Số điện thoại khách (Enter = khách lẻ, 0 = hủy hóa đơn): ");
            if (tu.equals("0")) return null;
            if (tu.isEmpty()) return khachLe();
            // Tìm khách đã có hồ sơ theo số điện thoại (hoặc theo mã khách)
            KhachHang kh = duLieu.khachHang.timTheoSoDienThoai(tu);
            if (kh == null) kh = duLieu.khachHang.timTheoMa(tu);
            if (kh != null) {
                String diem = DiemTichLuy.coTichDiem(kh.getMa())
                        ? ", điểm tích lũy: " + DiemTichLuy.diemHienCo(kh.getMa(), duLieu.hoaDon) : "";
                System.out.println("Khách hàng: " + kh.getMa() + " - " + kh.getHoTen() + " (" + kh.getSoDienThoai() + ")" + diem);
                return kh;
            }
            // Chưa có hồ sơ: số điện thoại đúng định dạng thì thêm khách mới ngay
            if (tu.matches("0[0-9]{9}")) {
                KhachHang moi = themKhachMoi(tu);
                if (moi != null) return moi;
                continue;   // người dùng chọn gõ lại số điện thoại
            }
            System.out.println("Không tìm thấy khách \"" + tu + "\". Số điện thoại gồm 10 chữ số, bắt đầu bằng 0.");
            goiYKhach(tu);   // có khách nào tên gần giống thì gợi ý
        }
    }

    /**
     * [UX] Tích điểm: hóa đơn BÁN LẺ của khách có hồ sơ mà khách đang có điểm thì hỏi dùng bao nhiêu điểm để giảm giá.
     * Gọi lúc xác nhận thanh toán (khi đã biết tổng tiền). Enter hoặc 0 = không dùng.
     * Bán sỉ và khách lẻ chung không dùng điểm nên hàm này không làm gì.
     */
    private void hoiDungDiem(HoaDon hd, KhachHang kh) {
        if (!(hd instanceof HoaDonBanLe) || !DiemTichLuy.coTichDiem(kh.getMa())) return;
        int coSan = DiemTichLuy.diemHienCo(kh.getMa(), duLieu.hoaDon);
        int toiDa = DiemTichLuy.diemToiDaDung(hd, coSan);
        if (toiDa <= 0) return;   // khách chưa có điểm (hoặc hóa đơn quá nhỏ) thì khỏi hỏi
        System.out.println("Khách " + kh.getHoTen() + " đang có " + coSan + " điểm (1 điểm = "
                + Bang.tien(DiemTichLuy.TIEN_MOI_DIEM_DUNG) + " đ, dùng tối đa 20% hóa đơn nên được dùng tối đa " + toiDa + " điểm).");
        int dung;
        while (true) {
            String s = docDong("Dùng bao nhiêu điểm? (Enter hoặc 0 = không dùng): ");
            if (s.isEmpty()) { dung = 0; break; }
            try {
                dung = Integer.parseInt(s);
                if (dung >= 0 && dung <= toiDa) break;
            } catch (NumberFormatException e) { /* nhập lại */ }
            System.out.println("Vui lòng nhập số nguyên từ 0 đến " + toiDa + ".");
        }
        ((HoaDonBanLe) hd).setDiemTichLuySuDung(dung);
    }

    /** Khách lẻ chung (mã KHLE): chưa có thì tạo (code gốc, chỉ tách thành hàm riêng). */
    private KhachHang khachLe() {
        KhachHang kh = duLieu.khachHang.timTheoMa("KHLE");
        if (kh == null) {
            kh = new KhachHang("KHLE","Khách lẻ","0000000000","Tại quầy");
            duLieu.khachHang.themKhachHang(kh);
        }
        return kh;
    }

    /** [UX] Gợi ý: in các khách có mã / tên / số điện thoại CHỨA chữ vừa gõ (nếu có) để người bán nhìn mà chọn. */
    private void goiYKhach(String tu) {
        if (tu.length() < 2) return;   // gõ có 1 ký tự thì khớp với gần như mọi khách, gợi ý vô ích
        String tuThuong = tu.toLowerCase(java.util.Locale.ROOT);
        ArrayList<KhachHang> goiY = new ArrayList<>();
        for (KhachHang k : duLieu.khachHang.layDanhSach()) {
            String chu = (k.getMa() + " " + k.getHoTen() + " " + k.getSoDienThoai()).toLowerCase(java.util.Locale.ROOT);
            if (chu.contains(tuThuong)) goiY.add(k);
        }
        if (!goiY.isEmpty()) GiaoDien.khachHang("CÓ PHẢI KHÁCH NÀY?", goiY.toArray(new KhachHang[0]), duLieu);
    }

    /**
     * [UX] Thêm khách mới ngay lúc bán, với số điện thoại vừa gõ (đã biết là chưa có hồ sơ).
     * Khách được LƯU NGAY, nên dù sau đó hủy hóa đơn (thayDoi nạp lại dữ liệu từ file) khách mới vẫn còn.
     * Gõ 0 ở ô họ tên để quay lại gõ lại số điện thoại (trả về null).
     */
    private KhachHang themKhachMoi(String sdt) throws Exception {
        System.out.println("Số " + sdt + " chưa có hồ sơ, nhập thêm khách mới.");
        String ten = chuoi("Họ tên khách (0 = gõ lại số điện thoại): ");
        if (ten.equals("0")) return null;
        KhachHang kh = new KhachHang(maMoi("KH"), ten, sdt, chuoi("Địa chỉ: "));
        duLieu.khachHang.themKhachHang(kh);
        System.out.println("Mã khách: " + kh.getMa());
        duLieu.luu();   // lưu ngay, không chờ thanh toán xong
        System.out.println("Đã lưu khách mới " + kh.getMa() + " (vẫn được giữ lại nếu hủy hóa đơn).");
        return kh;
    }

    /**
     * [UX] Hỏi loại hóa đơn rồi tạo hóa đơn. ĐA HÌNH: kiểu trả về là HoaDon chung, bên trong là
     * HoaDonBanLe hoặc HoaDonBanSi; từ đây trở đi code bán hàng không cần biết là loại nào,
     * mỗi loại tự tính giảm giá của mình qua tinhGiamGia().
     * Trả về null nếu người dùng chọn hủy.
     */
    private HoaDon taoHoaDon(KhachHang kh, NhanVien nv) {
        int loai = soNguyen("Loại hóa đơn: 1 Bán lẻ, 2 Bán sỉ (đại lý, có chiết khấu), 0 Hủy: ", 0, 2);
        if (loai == 0) return null;
        String maHD = maMoi("HD");
        if (loai == 1) {
            // Chưa có cơ chế tích điểm đồng bộ: bán lẻ dùng 0 điểm để tránh giảm giá tùy ý.
            return new HoaDonBanLe(maHD, ngayHomNay(), kh.getMa(), nv.getMa(), 0);
        }
        String tenCongTy = chuoi("Tên công ty / đại lý: ");
        double chietKhau = soThuc("Chiết khấu (%) từ 0 đến 100: ", 0, 100);
        return new HoaDonBanSi(maHD, ngayHomNay(), kh.getMa(), nv.getMa(), chietKhau, tenCongTy);
    }

    private ChiTietHoaDon chiTiet(HoaDon hd, String ma) {
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(ma)) return ct;
        return null;
    }

    private void menuNhapHang() {
        while (dangChay) {
            int chon = chonMenu("NHẬP HÀNG", "Quay lại", "Lập phiếu nhập và tăng tồn", "Xem phiếu đã nhập", "Tìm phiếu theo mã");
            if (chon == 0) return;
            if (chon == 1) nhapHang();
            else if (chon == 2) GiaoDien.danhSachPhieuNhap(duLieu.phieuNhap.layDanhSach(), duLieu);
            else {
                PhieuNhap ph = duLieu.phieuNhap.timTheoMa(chuoi("Mã phiếu: "));
                if (ph == null) System.out.println("Không tìm thấy phiếu."); else GiaoDien.phieuNhap(ph, duLieu);
            }
        }
    }

    public void nhapHang() { thayDoi(this::lapPhieuNhap); }

    private boolean lapPhieuNhap() throws Exception {
        if (!duocPhep(ChucNang.NHAP_HANG, QuyenHan.GHI)) return false;   // Nhân viên / Quản lý / Admin đều được lập phiếu
        // [NHẬP] Không bắt buộc phải có sản phẩm trước: có thể khai báo sản phẩm mới ngay trong phiếu nhập.
        if (duLieu.nhaCungCap.laRong() || duLieu.nhanVien.layDanhSach().length == 0) {
            System.out.println("Cần có nhà cung cấp và nhân viên trước khi nhập."); return false;
        }
        // [UX] Gõ sai mã nhà cung cấp thì hỏi lại chứ không hủy cả phiếu (xem chonNhaCungCap)
        NhaCungCap ncc = chonNhaCungCap();
        if (ncc == null) { System.out.println("Đã hủy phiếu, tồn kho không thay đổi."); return false; }
        // Nhân viên lập phiếu chính là người đang đăng nhập (quyền nhập hàng đã kiểm tra ngay đầu hàm).
        NhanVien nv = nguoiDung();
        PhieuNhap ph = new PhieuNhap(duLieu.phieuNhap.sinhMaPN(), ngayHomNay(), ncc.getMaNCC(),nv.getMa());
        // [SERI] Seri các máy sẽ nhập, theo từng mã sản phẩm. Chỉ đưa vào kho khi xác nhận phiếu.
        HashMap<String, ArrayList<String>> seriNhap = new HashMap<>();
        while (true) {
            int chon = chonMenu("PHIẾU NHẬP TẠM " + ph.getMaPN(), "Hủy phiếu", "Thêm sản phẩm", "Xem phiếu", "Xác nhận nhập kho");
            if (chon == 0) { System.out.println("Đã hủy phiếu, tồn kho không thay đổi."); return false; }
            if (chon == 1) {
                // [NHẬP] Nhập thêm cho sản phẩm đã có, hoặc khai báo sản phẩm mới ngay trong phiếu.
                // [UX] Chỉ người có quyền GHI trên Sản phẩm (Quản lý, Admin) mới được khai báo sản phẩm mới.
                //      Nhân viên (chỉ XEM sản phẩm) chỉ được nhập thêm cho sản phẩm đã có.
                int kieu = 1;
                if (muc(ChucNang.SAN_PHAM) >= QuyenHan.GHI) kieu = soNguyen("Sản phẩm: 1 Đã có (nhập thêm), 2 Sản phẩm mới: ", 1, 2);
                else System.out.println("Bạn chỉ được nhập thêm cho sản phẩm đã có (muốn thêm sản phẩm mới, nhờ Quản lý hoặc Admin).");
                SanPham sp;
                if (kieu == 1) {
                    GiaoDien.tonKho(duLieu.sanPham.getDsSanPham());
                    sp = timSanPham(); if (sp == null) continue;
                } else {
                    // Sản phẩm mới chỉ được lưu khi xác nhận phiếu; hủy phiếu thì sản phẩm cũng bị bỏ.
                    SanPham moi = taoSanPhamMoi();
                    sp = moi;
                    imLang(() -> { duLieu.sanPham.themSanPham(moi); return true; });   // [UX] không in "✔ Thêm sản phẩm thành công" (chưa xác nhận phiếu)
                    System.out.println("Mã sản phẩm mới: " + sp.getMaSP());
                }
                boolean trung = false;
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) if (ct.getNhapLieu().getMaSP().equalsIgnoreCase(sp.getMaSP())) trung = true;
                if (trung) { System.out.println("Sản phẩm đã có trong phiếu. Hủy và lập lại nếu cần đổi số lượng."); continue; }
                if (sp.getSoLuong() == Integer.MAX_VALUE) { System.out.println("Tồn kho đã đạt giới hạn."); continue; }
                int sl = soNguyen("Số lượng nhập: ",1,Integer.MAX_VALUE-sp.getSoLuong());
                double gia = soThuc("Giá nhập: ",0,1e15);
                // [UX] Giá nhập không thấp hơn giá bán thì cảnh báo và hỏi có cập nhật giá bán không.
                double giaBanCu = sp.getDonGia();   // nhớ giá bán cũ để trả lại nếu người dùng hủy dòng này ở bước seri
                hoiCapNhatGiaBan(sp, gia);
                // [SERI] Nhập bao nhiêu máy thì người dùng gõ bấy nhiêu seri
                ArrayList<String> daCoTrongPhieu = new ArrayList<>();
                for (ArrayList<String> ds : seriNhap.values()) daCoTrongPhieu.addAll(ds);
                ArrayList<String> seriMoi = nhapSeriNhapHang(sp, sl, daCoTrongPhieu);
                if (seriMoi == null) {   // người dùng gõ 0 để hủy
                    sp.setDonGia(giaBanCu);   // [UX] hủy dòng này thì giá bán cũng trở về như cũ
                    if (kieu == 2) duLieu.sanPham.xoaSanPham(sp.getMaSP());   // bỏ luôn sản phẩm mới vừa khai báo
                    continue;
                }
                seriNhap.put(sp.getMaSP(), seriMoi);
                String maCT = ph.getMaPN()+"-CT"+(ph.getDsChiTiet().size()+1);
                ph.themChiTiet(new ChiTietPhieuNhap(maCT,ph.getMaPN(),new NhapLieu(maCT+"-NL",sp.getMaSP(),sl,gia)));
            } else if (chon == 2) GiaoDien.phieuNhap(ph, duLieu);
            else {
                if (ph.getDsChiTiet().isEmpty()) { System.out.println("Phiếu cần ít nhất một sản phẩm."); continue; }
                GiaoDien.phieuNhap(ph, duLieu); if (!xacNhan()) return false;
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getNhapLieu().getMaSP());
                    Math.addExact(sp.getSoLuong(),ct.getNhapLieu().getSoLuongNhap());
                }
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getNhapLieu().getMaSP());
                    sp.setSoLuong(sp.getSoLuong()+ct.getNhapLieu().getSoLuongNhap());
                    // [SERI] ghi từng máy vào file seri riêng: còn trong kho, nhập từ phiếu này
                    for (String seri : seriNhap.get(sp.getMaSP())) duLieu.seri.them(new SeriSanPham(seri, sp.getMaSP(), ph.getMaPN()));
                }
                if (!duLieu.phieuNhap.them(ph)) throw new IllegalStateException("Không thêm được phiếu nhập.");
                return true;
            }
        }
    }

    /**
     * [UX] Chọn nhà cung cấp khi nhập hàng. In danh sách, gõ sai mã thì hỏi lại (1 Gõ lại, 0 Hủy phiếu)
     * chứ không hủy cả phiếu như trước. Trả về null nếu người dùng chọn hủy.
     */
    private NhaCungCap chonNhaCungCap() {
        GiaoDien.nhaCungCap("NHÀ CUNG CẤP", duLieu.nhaCungCap.layDanhSach());
        while (true) {
            NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
            if (ncc != null) return ncc;
            System.out.println("Không tìm thấy nhà cung cấp.");
            if (soNguyen("1 Gõ lại, 0 Hủy phiếu: ", 0, 1) == 0) return null;
        }
    }

    /**
     * [UX] Giá nhập từ mức giá bán trở lên nghĩa là bán ra không có lãi: cảnh báo và hỏi có cập nhật giá bán không.
     * Giá bán mới chỉ có hiệu lực khi xác nhận phiếu nhập (hủy phiếu thì giá bán giữ như cũ).
     */
    private void hoiCapNhatGiaBan(SanPham sp, double giaNhap) {
        if (giaNhap < sp.tinhGiaBan()) return;   // bình thường: giá nhập thấp hơn giá bán
        System.out.println("CẢNH BÁO: giá nhập " + Bang.tien(giaNhap) + " đ không thấp hơn giá bán hiện tại "
                + Bang.tien(sp.tinhGiaBan()) + " đ, bán ra sẽ không có lãi.");
        if (soNguyen("Cập nhật giá bán? 1 Có, 0 Giữ nguyên: ", 0, 1) == 0) return;
        double giaMoi = soThuc("Giá bán mới (phải lớn hơn giá nhập): ", 0, 1e15);
        while (giaMoi <= giaNhap) {
            System.out.println("Giá bán mới phải lớn hơn giá nhập " + Bang.tien(giaNhap) + " đ.");
            giaMoi = soThuc("Giá bán mới (phải lớn hơn giá nhập): ", 0, 1e15);
        }
        sp.setDonGia(giaMoi);
        System.out.println("Đã đổi giá bán " + sp.getMaSP() + " thành " + Bang.tien(giaMoi) + " đ (có hiệu lực khi xác nhận phiếu).");
    }

    /** Không cho xóa danh mục mà hóa đơn/phiếu nhập đang tham chiếu. */
    private boolean coLichSu(String loai, String ma) {
        for (HoaDon hd : duLieu.hoaDon.getDs()) {
            if (loai.equals("KH") && hd.getMaKH().equalsIgnoreCase(ma)) return true;
            if (loai.equals("NV") && hd.getMaNV().equalsIgnoreCase(ma)) return true;
            if (loai.equals("SP")) for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(ma)) return true;
        }
        for (PhieuNhap ph : duLieu.phieuNhap.layDanhSach()) {
            if (loai.equals("NV") && ph.getMaNV().equalsIgnoreCase(ma)) return true;
            if (loai.equals("SP")) for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) if (ct.getNhapLieu().getMaSP().equalsIgnoreCase(ma)) return true;
        }
        return false;
    }

    private SanPham timSanPham() {
        SanPham sp = duLieu.sanPham.searchSanPham(chuoi("Mã sản phẩm: "));
        if (sp == null) System.out.println("Không tìm thấy sản phẩm."); return sp;
    }

    // ===================== [SERI] SERI TỪNG MÁY =====================

    /**
     * [SERI] Khi BÁN: chọn seri cho từng máy. Seri phải là máy CÒN TRONG KHO của sản phẩm sp
     * và chưa được chọn ở hóa đơn tạm này (seriBan). Gõ 0 để hủy (trả về null).
     */
    private ArrayList<String> chonSeriDeBan(SanPham sp, int soMay, HashMap<String, ArrayList<String>> seriBan) {
        ArrayList<String> daChonTruoc = seriBan.get(sp.getMaSP());   // máy của SP này đã chọn ở lần thêm trước
        if (daChonTruoc == null) daChonTruoc = new ArrayList<>();
        // In bảng các máy còn trong kho mà chưa được chọn, để người bán nhìn seri mà gõ
        ArrayList<SeriSanPham> conChon = new ArrayList<>();
        for (SeriSanPham s : duLieu.seri.trongKho(sp.getMaSP())) {
            if (!daChonTruoc.contains(s.getMaSeri())) conChon.add(s);
        }
        GiaoDien.seri("MÁY CÒN TRONG KHO CỦA " + sp.getMaSP(), conChon);

        // [UX] Ba cách chọn máy: gõ STT trong bảng (vd "1" hoặc "1,2"), gõ seri đầy đủ, hoặc nhấn Enter để lấy máy nhập trước.
        ArrayList<String> daChon = new ArrayList<>();
        while (daChon.size() < soMay) {
            int conThieu = soMay - daChon.size();
            String nhap = docDong("Chọn " + conThieu + " máy - gõ STT (vd 1 hoặc 1,2) hoặc seri, Enter = lấy máy nhập trước, 0 = hủy: ");
            if (nhap.equals("0")) return null;
            if (nhap.isEmpty()) {
                // Enter: tự lấy các máy đứng đầu bảng (nhập trước), bỏ qua máy đã chọn
                for (SeriSanPham s : conChon) {
                    if (daChon.size() >= soMay) break;
                    if (!daChon.contains(s.getMaSeri())) daChon.add(s.getMaSeri());
                }
                continue;
            }
            // Gõ một hoặc nhiều mục cách nhau bằng dấu phẩy. Gom vào "dot", chỉ nhận cả đợt khi MỌI mục đều hợp lệ.
            ArrayList<String> dot = new ArrayList<>(daChon);
            boolean hopLe = true;
            for (String muc : nhap.split(",")) {
                String seri = doiSangSeri(muc.trim(), sp, conChon, dot, daChonTruoc);
                if (seri == null) { hopLe = false; break; }   // doiSangSeri đã in lý do
                dot.add(seri);
            }
            if (!hopLe) continue;
            if (dot.size() > soMay) {
                System.out.println("Chỉ cần chọn thêm " + conThieu + " máy, bạn gõ " + (dot.size() - daChon.size()) + " máy.");
                continue;
            }
            daChon = dot;
        }
        System.out.println("Đã chọn " + soMay + " máy: " + String.join(", ", daChon));
        return daChon;
    }

    /**
     * [UX] Đổi một mục người dùng gõ (số seri đầy đủ HOẶC STT trong bảng) thành số seri thật.
     * Mục không hợp lệ thì in lý do và trả về null.
     * daChon = các máy đã chọn trong đợt này; daChonTruoc = các máy đã chọn ở lần thêm trước của cùng sản phẩm.
     */
    private String doiSangSeri(String muc, SanPham sp, ArrayList<SeriSanPham> conChon,
                               ArrayList<String> daChon, ArrayList<String> daChonTruoc) {
        String seri;
        SeriSanPham s = duLieu.seri.tim(muc);
        if (s != null && s.conTrongKho() && s.getMaSP().equalsIgnoreCase(sp.getMaSP())) {
            seri = s.getMaSeri();   // gõ đúng seri: lấy đúng chữ như trong file (không phân biệt hoa thường khi gõ)
        } else if (muc.matches("[1-9][0-9]{0,5}")) {
            int stt = Integer.parseInt(muc);   // gõ số: hiểu là STT trong bảng "MÁY CÒN TRONG KHO"
            if (stt > conChon.size()) {
                System.out.println("Không có máy số " + stt + " trong bảng (chọn từ 1 đến " + conChon.size() + ").");
                return null;
            }
            seri = conChon.get(stt - 1).getMaSeri();
        } else {
            System.out.println("Seri này không có trong kho của " + sp.getMaSP() + ".");
            return null;
        }
        if (daChon.contains(seri) || daChonTruoc.contains(seri)) {
            System.out.println("Máy này đã được chọn rồi.");
            return null;
        }
        return seri;
    }

    /**
     * [SERI] Khi NHẬP HÀNG: người dùng tự gõ seri cho từng máy (đọc trên hộp / tem máy).
     * Seri không được trùng với máy đang trong kho, máy đã bán, hay máy khác trong cùng phiếu.
     * Gõ 0 để hủy (trả về null).
     */
    private ArrayList<String> nhapSeriNhapHang(SanPham sp, int soMay, ArrayList<String> daCoTrongPhieu) {
        System.out.println("Nhập seri cho " + soMay + " máy " + sp.getMaSP() + " - " + sp.getTenSP() + ":");
        ArrayList<String> ds = new ArrayList<>();
        while (ds.size() < soMay) {
            String seri = chuoi("Seri máy thứ " + (ds.size() + 1) + "/" + soMay + " (0 để hủy): ");
            if (seri.equals("0")) return null;
            if (!SeriSanPham.hopLe(seri)) {
                System.out.println("Seri gồm 3-30 ký tự: chữ không dấu, số, dấu - _ hoặc .");
            } else if (seriDaDung(seri, ds) || seriDaDung(seri, daCoTrongPhieu)) {
                System.out.println("Seri " + seri + " đã tồn tại, nhập seri khác.");
            } else {
                ds.add(seri);
            }
        }
        return ds;
    }

    /** [SERI] Seri đã được dùng chưa: có trong danh sách "dangNhap", hoặc đã có trong file seri (còn trong kho hay đã bán). */
    private boolean seriDaDung(String seri, ArrayList<String> dangNhap) {
        for (String s : dangNhap) {
            if (s.equalsIgnoreCase(seri)) return true;
        }
        return duLieu.seri.tim(seri) != null;
    }

    /** [SERI] Tra cứu bảo hành: máy có seri này thuộc sản phẩm nào, còn trong kho hay đã bán cho ai, ngày nào. */
    private void traCuuSeri() {
        SeriSanPham s = duLieu.seri.tim(chuoi("Seri máy cần tra: "));
        if (s == null) System.out.println("Không tìm thấy máy có seri này.");
        else GiaoDien.traCuuSeri(s, duLieu);
    }

    private NhanVien timNhanVien() {
        GiaoDien.nhanVien("DANH SÁCH NHÂN VIÊN", duLieu.nhanVien.layDanhSach());
        String ma = chuoi("Mã nhân viên: ");
        for (NhanVien nv : duLieu.nhanVien.layDanhSach()) if (nv.getMa().equalsIgnoreCase(ma)) return nv;
        System.out.println("Không tìm thấy nhân viên."); return null;
    }

    /** Sinh mã dựa trên dữ liệu đã nạp, không phụ thuộc bộ đếm static khi mở lại. */
    private String maMoi(String tienTo) {
        long i = 1;
        while (true) {
            String ma = String.format(java.util.Locale.ROOT,"%s%03d",tienTo,i++);
            boolean tonTai = switch (tienTo) {
                case "KH" -> duLieu.khachHang.timTheoMa(ma) != null;
                case "NV" -> java.util.Arrays.stream(duLieu.nhanVien.layDanhSach()).anyMatch(nv -> nv.getMa().equalsIgnoreCase(ma));
                case "HD" -> duLieu.hoaDon.timHoaDon(ma) != null;
                default -> duLieu.sanPham.searchSanPham(ma) != null;
            };
            if (!tonTai) return ma;
        }
    }

    @FunctionalInterface
    private interface ThaoTac { boolean thucHien() throws Exception; }

    private void thayDoi(ThaoTac thaoTac) {
        try {
            if (!thaoTac.thucHien()) { napDuLieu(); return; }
            duLieu.luu();
            System.out.println("ĐÃ HOÀN TẤT VÀ LƯU DỮ LIỆU.");
        } catch (Exception e) {
            System.out.println("Thao tác chưa hoàn tất: " + lyDo(e));
            try { napDuLieu(); }
            catch (Exception docLoi) {
                System.out.println("Không thể nạp lại phiên bản đã lưu. Dừng chương trình: " + lyDo(docLoi));
                dangChay = false;
            }
            if (e instanceof NoSuchElementException) dangChay = false;
        }
    }

    private int chonMenu(String tieuDe, String mucSoKhong, String... muc) {
        System.out.println("\n===== " + tieuDe + " =====");
        for (int i=0;i<muc.length;i++) System.out.println((i+1)+". "+muc[i]);
        System.out.println("0. "+mucSoKhong);
        return soNguyen("Chọn: ",0,muc.length);
    }
    public static int nhapSoNguyen(Scanner sc, String thongBao, int min, int max) {
        if (sc == null || min < 0 || max < min) throw new IllegalArgumentException("Khoảng nhập không hợp lệ.");
        while (true) {
            System.out.print(thongBao);
            if (!sc.hasNextLine()) return -1;
            try { int n = Integer.parseInt(sc.nextLine().trim()); if(n>=min && n<=max) return n; }
            catch (NumberFormatException e) { /* Nhập lại nếu không phải số nguyên. */ }
            System.out.println("Vui lòng nhập số nguyên từ "+min+" đến "+max+".");
        }
    }
    private int soNguyen(String thongBao, int min, int max) {
        int n=nhapSoNguyen(sc,thongBao,min,max);
        if(n==-1) throw new NoSuchElementException("Hết dữ liệu nhập."); return n;
    }
    private double soThuc(String thongBao, double min, double max) {
        while(true) {
            String s=chuoi(thongBao);
            try { double n=Double.parseDouble(s); if(Double.isFinite(n)&&n>=min&&n<=max) return n; }
            catch(NumberFormatException e) { /* Tiếp tục nhập. */ }
            System.out.println("Nhập số hữu hạn từ "+min+" đến "+max+".");
        }
    }
    private String chuoi(String thongBao) {
        while(true) {
            System.out.print(thongBao);
            if(!sc.hasNextLine()) throw new NoSuchElementException("Hết dữ liệu nhập.");
            String s=sc.nextLine().trim();
            if(!s.isEmpty()&&!s.contains(";")) return s;
            System.out.println("Không để trống hoặc chứa dấu chấm phẩy (;).");
        }
    }
    private String dienThoai() {
        while(true) {
            String s=chuoi("Số điện thoại (10 chữ số, bắt đầu bằng 0): ");
            if(s.matches("0[0-9]{9}")) return s;
            System.out.println("Số điện thoại không đúng định dạng.");
        }
    }
    private boolean xacNhan() { return soNguyen("Xác nhận: 1 Đồng ý, 0 Hủy: ",0,1)==1; }

    // ===================== [UX] HÀM NHẬP / HỎI DÙNG CHUNG =====================

    /** [UX] Đọc một dòng người dùng gõ, được phép để TRỐNG (chỉ nhấn Enter). Khác chuoi() là chuoi() không nhận chuỗi rỗng. */
    private String docDong(String thongBao) {
        System.out.print(thongBao);
        if (!sc.hasNextLine()) throw new NoSuchElementException("Hết dữ liệu nhập.");
        return sc.nextLine().trim();
    }

    /**
     * [UX] Nhập mật khẩu có ẨN ký tự khi gõ (người đứng cạnh không nhìn thấy).
     * Chạy trong cửa sổ dòng lệnh thật (cmd, PowerShell, terminal) thì dùng Console.readPassword: gõ không hiện gì.
     * Không có cửa sổ thật (chạy trong IDE, hoặc test đọc input từ file) thì System.console() là null,
     * lúc đó đọc như bình thường bằng chuoi() (mật khẩu sẽ hiện ra).
     */
    private String nhapMatKhau(String thongBao) {
        java.io.Console console = System.console();
        if (console == null) return chuoi(thongBao);
        while (true) {
            System.out.print(thongBao);
            System.out.flush();
            char[] mk = console.readPassword();          // gõ không hiện ký tự nào
            if (mk == null) throw new NoSuchElementException("Hết dữ liệu nhập.");
            String s = new String(mk).trim();
            java.util.Arrays.fill(mk, ' ');              // xóa mật khẩu khỏi bộ nhớ tạm
            if (!s.isEmpty() && !s.contains(";")) return s;
            System.out.println("Không để trống hoặc chứa dấu chấm phẩy (;).");   // cùng luật với chuoi()
        }
    }

    /** [UX] Sửa một chữ: gõ giá trị mới, hoặc nhấn Enter để GIỮ NGUYÊN giá trị cũ (cu). */
    private String chuoiGiuNguyen(String tenTruong, String cu) {
        while (true) {
            String s = docDong(tenTruong + " mới (Enter = giữ \"" + cu + "\"): ");
            if (s.isEmpty()) return cu;
            if (!s.contains(";")) return s;
            System.out.println("Không chứa dấu chấm phẩy (;).");
        }
    }

    /** [UX] Sửa một số tiền: gõ số mới, hoặc nhấn Enter để giữ nguyên số cũ (cu). */
    private double soThucGiuNguyen(String tenTruong, double cu, double min, double max) {
        while (true) {
            String s = docDong(tenTruong + " mới (Enter = giữ " + Bang.tien(cu) + "): ");
            if (s.isEmpty()) return cu;
            try { double n = Double.parseDouble(s); if (Double.isFinite(n) && n >= min && n <= max) return n; }
            catch (NumberFormatException e) { /* nhập lại */ }
            System.out.println("Nhập số hữu hạn từ " + min + " đến " + max + ".");
        }
    }

    /** [UX] Sửa số điện thoại: gõ số mới (đúng định dạng) hoặc nhấn Enter để giữ nguyên. */
    private String dienThoaiGiuNguyen(String cu) {
        while (true) {
            String s = docDong("Số điện thoại mới (Enter = giữ " + cu + "): ");
            if (s.isEmpty()) return cu;
            if (s.matches("0[0-9]{9}")) return s;
            System.out.println("Số điện thoại không đúng định dạng.");
        }
    }

    /**
     * [UX] Hỏi trước khi XÓA: in rõ đối tượng sắp xóa (loại + mã + tên) rồi hỏi 1 Đồng ý / 0 Hủy.
     * Trả về true nếu người dùng đồng ý xóa.
     */
    private boolean xacNhanXoa(String doiTuong) {
        System.out.println("Sắp xóa " + doiTuong + ".");
        if (xacNhan()) return true;
        System.out.println("Đã hủy, không xóa gì.");
        return false;
    }

    /**
     * [UX] Chạy một việc nhưng KHÔNG cho các lớp gốc in thông báo kỹ thuật ra màn hình
     * (ví dụ DanhSachHoaDon.themHoaDon tự in "Them hoa don HD007 thanh cong!").
     * Thông báo cho người dùng do QuanLyCuaHang tự in lại bằng tiếng Việt có dấu.
     */
    private boolean imLang(ThaoTac viec) throws Exception {
        java.io.PrintStream manHinhCu = DuLieuCuaHang.tatIn();
        try {
            return viec.thucHien();
        } finally {
            DuLieuCuaHang.batIn(manHinhCu);   // luôn trả lại màn hình, kể cả khi lỗi
        }
    }
    private String ngayHomNay() { return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    private String lyDo(Throwable e) {
        while(e.getCause()!=null) e=e.getCause();
        return e.getMessage()==null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
