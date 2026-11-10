import java.util.Scanner;

/**
 * Khung điều phối của Phú. Bản này chỉ chạy menu, chưa xử lý nghiệp vụ hoặc lưu dữ liệu.
 * Xem docs/QUY_UOC_TICH_HOP.md trước khi kết nối các lớp của thành viên.
 */
public class QuanLyCuaHang {
    private final String tenCuaHang;
    private final Scanner sc;
    private boolean dangChay;

    // CẦN LÀM KHI TÍCH HỢP: khởi tạo mỗi danh sách dùng chung đúng một lần trong hàm khởi tạo:
    // DanhSachSanPham dsSanPham; DanhSachHoaDon dsHoaDon;
    // DsKhachHang dsKhachHang; DsNhanVien dsNhanVien;
    // DanhSachPhieuNhap dsPhieuNhap; DanhSachNhaCungCap dsNhaCungCap;
    // NhanVien nhanVienDangNhap;
    // Chưa khai báo các thuộc tính này để khung chạy độc lập khi các phần khác chưa hoàn thiện.

    public QuanLyCuaHang(String tenCuaHang) {
        this(tenCuaHang, new Scanner(System.in));
    }

    public QuanLyCuaHang(String tenCuaHang, Scanner sc) {
        if (tenCuaHang == null || tenCuaHang.trim().isEmpty() || sc == null) {
            throw new IllegalArgumentException("Tên cửa hàng và Scanner không được rỗng.");
        }
        this.tenCuaHang = tenCuaHang;
        this.sc = sc;
    }

    public void chay() {
        dangChay = true;
        System.out.println("\n===== " + tenCuaHang + " =====");
        System.out.println("BẢN KHUNG: chỉ điều hướng menu; chưa đăng nhập, bán hàng hay lưu dữ liệu.");
        // CẦN LÀM: gọi docDuLieu(); sau đó yêu cầu dangNhap() thành công mới được vào menu.
        while (dangChay) {
            menuChinh();
        }
        System.out.println("Đã thoát bản khung. Không có dữ liệu nghiệp vụ được ghi.");
    }

    private void menuChinh() {
        String[] muc = {
                "Bán hàng tại quầy", "Quản lý sản phẩm", "Quản lý khách hàng",
                "Quản lý hóa đơn", "Nhập hàng", "Quản lý nhà cung cấp",
                "Quản lý nhân viên", "Thống kê", "Tài khoản", "Dữ liệu"
        };
        int chon = chonMenu("MENU CHÍNH", muc, "Thoát chương trình");
        switch (chon) {
            case 1: banHang(); break;
            case 2: menuSanPham(); break;
            case 3: menuKhachHang(); break;
            case 4: menuHoaDon(); break;
            case 5: nhapHang(); break;
            case 6: menuNhaCungCap(); break;
            case 7: menuNhanVien(); break;
            case 8: menuThongKe(); break;
            case 9: menuTaiKhoan(); break;
            case 10: menuDuLieu(); break;
            case 0: dangChay = false; break;
            default: break;
        }
    }

    private void menuSanPham() {
        menuDanhMuc("SẢN PHẨM", "B", new String[] {
                "Thêm sản phẩm", "Sửa sản phẩm", "Xóa sản phẩm",
                "Tìm theo mã / tên / loại", "Hiển thị danh sách", "Xem tồn kho"
        });
    }

    private void menuKhachHang() {
        menuDanhMuc("KHÁCH HÀNG", "E; lịch sử mua hàng phối hợp C", new String[] {
                "Thêm khách hàng", "Sửa khách hàng", "Xóa khách chưa có giao dịch",
                "Tìm theo mã / tên / số điện thoại", "Hiển thị danh sách", "Xem lịch sử mua hàng"
        });
    }

    private void menuNhanVien() {
        menuDanhMuc("NHÂN VIÊN", "E", new String[] {
                "Thêm nhân viên", "Sửa nhân viên", "Xóa nhân viên chưa có giao dịch",
                "Tìm nhân viên", "Hiển thị danh sách"
        });
    }

    private void menuNhaCungCap() {
        menuDanhMuc("NHÀ CUNG CẤP", "D", new String[] {
                "Thêm nhà cung cấp", "Sửa nhà cung cấp", "Xóa nhà cung cấp chưa có giao dịch",
                "Tìm nhà cung cấp", "Hiển thị danh sách"
        });
    }

    /** Menu tạm cho các danh mục. Khi ghép code, thay choTichHop bằng lời gọi chức năng thật. */
    private void menuDanhMuc(String tieuDe, String thanhVien, String[] muc) {
        while (dangChay) {
            int chon = chonMenu(tieuDe, muc, "Quay lại");
            if (chon == 0) return;
            choTichHop(muc[chon - 1], thanhVien);
        }
    }

    private void menuHoaDon() {
        menuDanhMuc("HÓA ĐƠN", "C", new String[] {
                "Xem danh sách hóa đơn", "Tìm và in lại theo mã",
                "Tra cứu theo khách hàng", "Tra cứu theo khoảng ngày"
        });
    }

    private void menuThongKe() {
        menuDanhMuc("THỐNG KÊ", "B / C / D", new String[] {
                "Doanh thu theo khoảng ngày", "Sản phẩm bán nhiều",
                "Sản phẩm tồn ít / hết hàng", "Tiền nhập hàng theo khoảng ngày"
        });
    }

    private void menuTaiKhoan() {
        while (dangChay) {
            int chon = chonMenu("TÀI KHOẢN", new String[] {
                    "Đăng nhập", "Đổi mật khẩu", "Quản lý tài khoản và quyền", "Đăng xuất"
            }, "Quay lại");
            switch (chon) {
                case 0: return;
                case 1: dangNhap(); break;
                case 2: choTichHop("Đổi mật khẩu", "E"); break;
                case 3: choTichHop("Quản lý tài khoản và quyền", "E"); break;
                case 4: dangXuat(); break;
                default: break;
            }
        }
    }

    private void menuDuLieu() {
        while (dangChay) {
            int chon = chonMenu("DỮ LIỆU", new String[] {
                    "Đọc dữ liệu", "Lưu dữ liệu"
            }, "Quay lại");
            if (chon == 0) return;
            if (chon == 1) docDuLieu();
            else luuDuLieu();
        }
    }

    public void banHang() {
        // CẦN LÀM: kiểm tra đăng nhập và quyền -> tìm khách hàng -> lập hóa đơn tạm.
        // Thêm/sửa/xóa dòng hàng; cộng dồn số lượng cùng mã sản phẩm khi kiểm tra tồn kho.
        // Xác nhận -> kiểm tra lại tồn kho -> cập nhật tồn và hóa đơn -> lưu đầy đủ dữ liệu liên quan.
        // Hủy hóa đơn tạm: không đổi tồn kho. Lỗi lưu: khôi phục dữ liệu, không báo thành công.
        choTichHop("Bán hàng tại quầy", "Phú kết nối B + C + E");
    }

    public void nhapHang() {
        // CẦN LÀM: kiểm tra quyền -> chọn nhà cung cấp -> tạo phiếu tạm -> thêm dòng nhập -> xác nhận.
        // Chỉ tăng tồn kho một lần khi hoàn tất; hủy phiếu tạm không làm thay đổi tồn kho.
        choTichHop("Nhập hàng", "Phú kết nối B + D + E");
    }

    public boolean dangNhap() {
        // CẦN LÀM: gọi dsNhanVien.dangNhap(ten, matKhau), lưu nhân viên vào nhanVienDangNhap.
        choTichHop("Đăng nhập", "E");
        return false; // Chưa xác thực nên trả về false, không báo đăng nhập thành công.
    }

    public void dangXuat() {
        // CẦN LÀM: xóa thông tin nhanVienDangNhap và quay về màn hình đăng nhập.
        choTichHop("Đăng xuất", "E");
    }

    public void docDuLieu() {
        // CẦN LÀM: gọi docFile của từng danh sách; kiểm tra các mã liên kết giữa dữ liệu.
        choTichHop("Đọc dữ liệu", "B + C + D + E");
    }

    public void luuDuLieu() {
        // CẦN LÀM: gọi ghiFile; bắt lỗi IOException, chỉ báo thành công khi ghi đầy đủ.
        choTichHop("Lưu dữ liệu", "B + C + D + E");
    }

    private void choTichHop(String chucNang, String thanhVien) {
        System.out.println("[CHƯA TÍCH HỢP] " + chucNang);
        System.out.println("Phần phụ trách: " + thanhVien + ". Xem docs/QUY_UOC_TICH_HOP.md.");
    }

    private int chonMenu(String tieuDe, String[] muc, String mucSoKhong) {
        System.out.println("\n===== " + tieuDe + " =====");
        for (int i = 0; i < muc.length; i++) {
            System.out.println((i + 1) + ". " + muc[i]);
        }
        System.out.println("0. " + mucSoKhong);
        int chon = nhapSoNguyen(sc, "Chọn: ", 0, muc.length);
        if (chon == -1) {
            dangChay = false; // Hết dữ liệu đầu vào: thoát để tránh lặp vô hạn.
            return 0;
        }
        return chon;
    }

    /** Trả về -1 khi hết dữ liệu đầu vào. Các menu chỉ dùng lựa chọn từ 0 trở lên. */
    public static int nhapSoNguyen(Scanner sc, String thongBao, int min, int max) {
        if (sc == null || min < 0 || max < min) {
            throw new IllegalArgumentException("Khoảng nhập không hợp lệ.");
        }
        while (true) {
            System.out.print(thongBao);
            if (!sc.hasNextLine()) return -1;
            try {
                int so = Integer.parseInt(sc.nextLine().trim());
                if (so >= min && so <= max) return so;
            } catch (NumberFormatException e) {
                // Yêu cầu nhập lại nếu không phải số nguyên hoặc số vượt phạm vi kiểu int.
            }
            System.out.println("Vui lòng nhập số nguyên từ " + min + " đến " + max + ".");
        }
    }
}
