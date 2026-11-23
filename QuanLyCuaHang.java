import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
        System.out.println("Dữ liệu: " + thuMucDuLieu.toAbsolutePath());
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
        DuLieuCuaHang moi = new DuLieuCuaHang(sc, thuMucDuLieu);
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
            String matKhau = chuoi("Mật khẩu: ");
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
            if (!tk.kiemTraMatKhau(chuoi("Mật khẩu hiện tại: "))) {
                System.out.println("Mật khẩu hiện tại không đúng.");
                return false;
            }
            String moi = chuoi("Mật khẩu mới (tối thiểu 3 ký tự): ");
            if (!moi.equals(chuoi("Nhập lại mật khẩu mới: "))) {
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
        themMuc(ten, viec, muc(ChucNang.HOA_DON) >= QuyenHan.XEM, "Xem hóa đơn đã bán", this::menuHoaDon);
        themMuc(ten, viec, muc(ChucNang.NHAP_HANG) >= QuyenHan.XEM, "Nhập hàng", this::menuNhapHang);
        themMuc(ten, viec, muc(ChucNang.NHA_CUNG_CAP) >= QuyenHan.XEM, "Quản lý nhà cung cấp", this::menuNhaCungCap);
        themMuc(ten, viec, muc(ChucNang.NHAN_VIEN) >= QuyenHan.XEM, "Quản lý nhân viên", this::menuNhanVien);
        themMuc(ten, viec, muc(ChucNang.TAI_KHOAN) >= QuyenHan.XEM, "Quản lý tài khoản", this::menuTaiKhoan);
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
                nv.setTaiKhoan(null);
                return true;
            });
        }
    }

    /** In danh sách tài khoản: mã nhân viên | họ tên | tên đăng nhập | quyền. */
    private void hienThiTaiKhoan() {
        for (NhanVien nv : duLieu.nhanVien.layDanhSach()) {
            TaiKhoan tk = nv.getTaiKhoan();
            String thongTin = (tk == null)
                    ? "(chưa có tài khoản)"
                    : tk.getTenDangNhap() + " | " + tk.getQuyenHan().getTenQuyen();
            System.out.println(nv.getMa() + " | " + nv.getHoTen() + " | " + thongTin);
        }
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

    private void menuSanPham() {
        while (dangChay) {
            int chon = chonMenuTheoQuyen("SẢN PHẨM", "Quay lại", ChucNang.SAN_PHAM, "Thêm sản phẩm mới (tồn 0)",
                    "Sửa thông tin / giá bán", "Xóa sản phẩm chưa có giao dịch",
                    "Tìm theo mã / tên / loại", "Hiển thị danh sách", "Xem tồn kho");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> {
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
                duLieu.sanPham.themSanPham(sp);
                return true;
            });
            else if (chon == 2) thayDoi(() -> {
                SanPham sp = timSanPham();
                if (sp == null) return false;
                String ten = chuoi("Tên mới: ");
                double gia = soThuc("Giá bán mới: ", 0, 1e15);
                sp.setTenSP(ten); sp.setDonGia(gia);
                return true; // Không sửa mã/tồn kho trực tiếp.
            });
            else if (chon == 3) thayDoi(() -> {
                SanPham sp = timSanPham();
                if (sp == null) return false;
                if (coLichSu("SP", sp.getMaSP()) || sp.getSoLuong() != 0) {
                    System.out.println("Không xóa sản phẩm có giao dịch hoặc còn tồn kho."); return false;
                }
                return duLieu.sanPham.xoaSanPham(sp.getMaSP());
            });
            else if (chon == 4) {
                String tu = chuoi("Nhập mã / tên / Laptop / PC / LinhKien: ").toLowerCase(java.util.Locale.ROOT);
                boolean thay = false;
                for (SanPham sp : duLieu.sanPham.getDsSanPham()) {
                    if ((sp.getMaSP() + " " + sp.getTenSP() + " " + sp.getClass().getSimpleName())
                            .toLowerCase(java.util.Locale.ROOT).contains(tu)) { sp.hienthi(); thay = true; }
                }
                if (!thay) System.out.println("Không tìm thấy sản phẩm.");
            } else if (chon == 5) duLieu.sanPham.hienThiDanhSach();
            else for (SanPham sp : duLieu.sanPham.getDsSanPham())
                    System.out.println(sp.getMaSP() + " | " + sp.getTenSP() + " | Tồn: " + sp.getSoLuong());
        }
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
                String ten = chuoi("Họ tên mới: "); String sdt = dienThoai(); String diaChi = chuoi("Địa chỉ mới: ");
                KhachHang trung = duLieu.khachHang.timTheoSoDienThoai(sdt);
                if (trung != null && trung != kh) throw new IllegalArgumentException("Số điện thoại đã có hồ sơ.");
                kh.setHoTen(ten); kh.setSoDienThoai(sdt); kh.setDiaChi(diaChi); return true;
            });
            else if (chon == 3) thayDoi(() -> {
                String ma = chuoi("Mã khách: ");
                if (ma.equalsIgnoreCase("KHLE") || coLichSu("KH", ma)) {
                    System.out.println("Không xóa khách lẻ chung hoặc khách đã mua hàng."); return false;
                }
                return duLieu.khachHang.xoaKhachHang(ma);
            });
            else if (chon == 4) {
                String tu = chuoi("Mã / tên / số điện thoại: ").toLowerCase(java.util.Locale.ROOT);
                boolean thay = false;
                for (KhachHang kh : duLieu.khachHang.layDanhSach())
                    if ((kh.getMa()+" "+kh.getHoTen()+" "+kh.getSoDienThoai()).toLowerCase(java.util.Locale.ROOT).contains(tu)) {
                        kh.hienThi(); thay = true;
                    }
                if (!thay) System.out.println("Không tìm thấy khách.");
            } else if (chon == 5) duLieu.khachHang.hienThi();
            else duLieu.hoaDon.timKiemTheoMaKH(chuoi("Mã khách: "));
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
                String ten = chuoi("Họ tên mới: "); String sdt = dienThoai();
                String chucVu = chuoi("Chức vụ mới: "); double luong = soThuc("Lương mới: ",0,1e15);
                nv.setHoTen(ten); nv.setSoDienThoai(sdt); nv.setChucVu(chucVu); nv.setLuong(luong); return true;
            });
            else if (chon == 3) thayDoi(() -> {
                NhanVien nv = timNhanVien(); if (nv == null) return false;
                // Nhân viên còn tài khoản thì phải để Admin xóa tài khoản trước (tránh lỡ xóa mất Admin).
                if (nv.getTaiKhoan() != null) { System.out.println("Nhân viên đang có tài khoản, nhờ Admin xóa tài khoản trước."); return false; }
                if (coLichSu("NV", nv.getMa())) { System.out.println("Nhân viên đã có giao dịch, không xóa."); return false; }
                duLieu.nhanVien.xoaNhanVien(nv); return true;
            });
            else if (chon == 4) {
                String tu = chuoi("Mã hoặc tên nhân viên: ").toLowerCase(java.util.Locale.ROOT);
                boolean thay = false;
                for (NhanVien nv : duLieu.nhanVien.layDanhSach())
                    if ((nv.getMa()+" "+nv.getHoTen()).toLowerCase(java.util.Locale.ROOT).contains(tu)) {
                        nv.hienThi(); thay = true;
                    }
                if (!thay) System.out.println("Không tìm thấy nhân viên.");
            } else duLieu.nhanVien.hienThi();
        }
    }

    private void menuNhaCungCap() {
        while (dangChay) {
            int chon = chonMenuTheoQuyen("NHÀ CUNG CẤP", "Quay lại", ChucNang.NHA_CUNG_CAP, "Thêm", "Sửa", "Xóa nếu chưa có phiếu nhập", "Tìm", "Xem danh sách");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> duLieu.nhaCungCap.them(new NhaCungCap(
                    duLieu.nhaCungCap.sinhMa(), chuoi("Tên nhà cung cấp: "), chuoi("Địa chỉ: "), dienThoai())));
            else if (chon == 2) thayDoi(() -> {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                if (ncc == null) { System.out.println("Không tìm thấy nhà cung cấp."); return false; }
                String ten = chuoi("Tên mới: "); String diaChi = chuoi("Địa chỉ mới: "); String sdt = dienThoai();
                ncc.setTenNCC(ten); ncc.setDiaChi(diaChi); ncc.setSoDienThoai(sdt); return true;
            });
            else if (chon == 3) thayDoi(() -> duLieu.nhaCungCap.xoa(chuoi("Mã nhà cung cấp: ")));
            else if (chon == 4) {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                System.out.println(ncc == null ? "Không tìm thấy nhà cung cấp." : ncc);
            } else duLieu.nhaCungCap.hienThi();
        }
    }

    private void menuHoaDon() {
        while (dangChay) {
            int chon = chonMenu("HÓA ĐƠN ĐÃ BÁN", "Quay lại", "Xem danh sách", "Tìm và in lại theo mã", "Lịch sử theo khách hàng");
            if (chon == 0) return;
            if (chon == 1) duLieu.hoaDon.hienThi();
            else if (chon == 2) {
                HoaDon hd = duLieu.hoaDon.timHoaDon(chuoi("Mã hóa đơn: "));
                if (hd == null) System.out.println("Không tìm thấy hóa đơn."); else hd.inHoaDon();
            } else duLieu.hoaDon.timKiemTheoMaKH(chuoi("Mã khách: "));
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
        int luaChonKH = soNguyen("Khách: 1 Có hồ sơ, 2 Thêm mới, 3 Khách lẻ: ",1,3);
        KhachHang kh;
        if (luaChonKH == 1) {
            String tu = chuoi("Mã khách hoặc số điện thoại: ");
            kh = duLieu.khachHang.timTheoMa(tu);
            if (kh == null) kh = duLieu.khachHang.timTheoSoDienThoai(tu);
            if (kh == null) { System.out.println("Không tìm thấy khách."); return false; }
        } else if (luaChonKH == 2) kh = themKhach();
        else {
            kh = duLieu.khachHang.timTheoMa("KHLE");
            if (kh == null) {
                kh = new KhachHang("KHLE","Khách lẻ","0000000000","Tại quầy");
                duLieu.khachHang.themKhachHang(kh);
            }
        }
        // Chưa có cơ chế tích điểm đồng bộ: bán lẻ dùng 0 điểm để tránh giảm giá tùy ý.
        HoaDon hd = new HoaDonBanLe(maMoi("HD"), ngayHomNay(), kh.getMa(), nv.getMa(), 0);
        while (true) {
            int chon = chonMenu("HÓA ĐƠN TẠM " + hd.getMaHD(), "Hủy hóa đơn", "Thêm sản phẩm", "Đổi số lượng", "Bỏ sản phẩm", "Xem hóa đơn", "Xác nhận thanh toán");
            if (chon == 0) { System.out.println("Đã hủy, tồn kho không thay đổi."); return false; }
            if (chon == 1) {
                duLieu.sanPham.hienThiDanhSach();
                SanPham sp = timSanPham(); if (sp == null) continue;
                int daChon = 0;
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(sp.getMaSP())) daChon = ct.getSoLuong();
                int conLai = sp.getSoLuong() - daChon;
                if (conLai <= 0) { System.out.println("Không đủ tồn kho để thêm."); continue; }
                int sl = soNguyen("Số lượng thêm: ",1,conLai);
                hd.themChiTiet(new ChiTietHoaDon(hd.getMaHD(),sp.getMaSP(),sl,sp.tinhGiaBan()));
            } else if (chon == 2) {
                String ma = chuoi("Mã sản phẩm đã chọn: ");
                ChiTietHoaDon ct = chiTiet(hd, ma);
                if (ct == null) { System.out.println("Sản phẩm không có trong hóa đơn."); continue; }
                SanPham sp = duLieu.sanPham.searchSanPham(ma);
                if (sp.getSoLuong() == 0) { System.out.println("Sản phẩm hết hàng."); continue; }
                ct.setSoLuong(soNguyen("Số lượng mới: ",1,sp.getSoLuong()));
            } else if (chon == 3) {
                if (!hd.xoaChiTiet(chuoi("Mã sản phẩm cần bỏ: "))) System.out.println("Không có sản phẩm trong hóa đơn.");
            } else if (chon == 4) hd.inHoaDon();
            else {
                if (hd.getSoLuongChiTiet() == 0) { System.out.println("Hóa đơn phải có sản phẩm."); continue; }
                hd.inHoaDon();
                if (!xacNhan()) return false;
                // Kiểm tra toàn bộ trước khi giảm bất kỳ sản phẩm nào.
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getMaSP());
                    if (sp == null || sp.getSoLuong() < ct.getSoLuong()) throw new IllegalStateException("Không đủ hàng.");
                }
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getMaSP());
                    sp.setSoLuong(sp.getSoLuong()-ct.getSoLuong());
                }
                if (!duLieu.hoaDon.themHoaDon(hd)) throw new IllegalStateException("Không thêm được hóa đơn.");
                return true;
            }
        }
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
            else if (chon == 2) duLieu.phieuNhap.hienThi();
            else {
                PhieuNhap ph = duLieu.phieuNhap.timTheoMa(chuoi("Mã phiếu: "));
                if (ph == null) System.out.println("Không tìm thấy phiếu."); else ph.hienThi();
            }
        }
    }

    public void nhapHang() { thayDoi(this::lapPhieuNhap); }

    private boolean lapPhieuNhap() {
        if (!duocPhep(ChucNang.NHAP_HANG, QuyenHan.GHI)) return false;   // Nhân viên / Quản lý / Admin đều được lập phiếu
        if (duLieu.nhaCungCap.laRong() || duLieu.sanPham.getDsSanPham().isEmpty() || duLieu.nhanVien.layDanhSach().length == 0) {
            System.out.println("Cần có nhà cung cấp, sản phẩm và nhân viên trước khi nhập."); return false;
        }
        duLieu.nhaCungCap.hienThi();
        NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
        if (ncc == null) { System.out.println("Không tìm thấy nhà cung cấp."); return false; }
        // Nhân viên lập phiếu chính là người đang đăng nhập (quyền nhập hàng đã kiểm tra ngay đầu hàm).
        NhanVien nv = nguoiDung();
        PhieuNhap ph = new PhieuNhap(duLieu.phieuNhap.sinhMaPN(), ngayHomNay(), ncc.getMaNCC(),nv.getMa());
        while (true) {
            int chon = chonMenu("PHIẾU NHẬP TẠM " + ph.getMaPN(), "Hủy phiếu", "Thêm sản phẩm", "Xem phiếu", "Xác nhận nhập kho");
            if (chon == 0) { System.out.println("Đã hủy phiếu, tồn kho không thay đổi."); return false; }
            if (chon == 1) {
                SanPham sp = timSanPham(); if (sp == null) continue;
                boolean trung = false;
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) if (ct.getNhapLieu().getMaSP().equalsIgnoreCase(sp.getMaSP())) trung = true;
                if (trung) { System.out.println("Sản phẩm đã có trong phiếu. Hủy và lập lại nếu cần đổi số lượng."); continue; }
                if (sp.getSoLuong() == Integer.MAX_VALUE) { System.out.println("Tồn kho đã đạt giới hạn."); continue; }
                int sl = soNguyen("Số lượng nhập: ",1,Integer.MAX_VALUE-sp.getSoLuong());
                double gia = soThuc("Giá nhập: ",0,1e15);
                String maCT = ph.getMaPN()+"-CT"+(ph.getDsChiTiet().size()+1);
                ph.themChiTiet(new ChiTietPhieuNhap(maCT,ph.getMaPN(),new NhapLieu(maCT+"-NL",sp.getMaSP(),sl,gia)));
            } else if (chon == 2) ph.hienThi();
            else {
                if (ph.getDsChiTiet().isEmpty()) { System.out.println("Phiếu cần ít nhất một sản phẩm."); continue; }
                ph.hienThi(); if (!xacNhan()) return false;
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getNhapLieu().getMaSP());
                    Math.addExact(sp.getSoLuong(),ct.getNhapLieu().getSoLuongNhap());
                }
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getNhapLieu().getMaSP());
                    sp.setSoLuong(sp.getSoLuong()+ct.getNhapLieu().getSoLuongNhap());
                }
                if (!duLieu.phieuNhap.them(ph)) throw new IllegalStateException("Không thêm được phiếu nhập.");
                return true;
            }
        }
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
    private NhanVien timNhanVien() {
        duLieu.nhanVien.hienThi();
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
    private String ngayHomNay() { return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    private String lyDo(Throwable e) {
        while(e.getCause()!=null) e=e.getCause();
        return e.getMessage()==null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
