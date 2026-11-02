
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DsKhachHang dsKh = new DsKhachHang(100); // danh sách khách hàng
        DsNhanVien dsNv = new DsNhanVien();      // danh sách nhân viên

        TaiKhoan currentUser = null;

        // Đăng nhập
        while (currentUser == null) {
            System.out.println("===== ĐĂNG NHẬP =====");
            System.out.print("Tên đăng nhập: ");
            String user = sc.nextLine();
            System.out.print("Mật khẩu: ");
            String pass = sc.nextLine();

            if (TaiKhoan.tk_Nhanvien.dangNhap(user, pass)) {
                currentUser = TaiKhoan.tk_Nhanvien;
            } else if (TaiKhoan.tk_Quanly.dangNhap(user, pass)) {
                currentUser = TaiKhoan.tk_Quanly;
            } else if (TaiKhoan.tk_Admin.dangNhap(user, pass)) {
                currentUser = TaiKhoan.tk_Admin;
            } else {
                System.out.println("Sai tài khoản hoặc mật khẩu, vui lòng thử lại!");
            }
        }

        // Menu theo quyền
        if (currentUser == TaiKhoan.tk_Nhanvien) {
            nhanVienMenu(sc, dsKh);
        } else if (currentUser == TaiKhoan.tk_Quanly) {
            quanLyMenu(sc, dsKh, dsNv);
        } else if (currentUser == TaiKhoan.tk_Admin) {
            adminMenu(sc);
        }
    }

    // Menu cho Nhân viên
    private static void nhanVienMenu(Scanner sc, DsKhachHang dsKh) {
        int choice;
        do {
            System.out.println("===== MENU NHÂN VIÊN =====");
            System.out.println("1. Thêm khách hàng");
            System.out.println("2. Xóa khách hàng theo mã");
            System.out.println("3. Tìm khách hàng theo tên");
            System.out.println("4. Hiển thị danh sách khách hàng");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Nhập mã KH: ");
                    String ma = sc.nextLine();
                    System.out.print("Nhập họ tên: ");
                    String ten = sc.nextLine();
                    System.out.print("Nhập số điện thoại: ");
                    String sdt = sc.nextLine();
                    System.out.print("Nhập địa chỉ: ");
                    String dc = sc.nextLine();
                    dsKh.themKhachHang(new KhachHang(ma, ten, sdt, dc));
                    break;
                case 2:
                    System.out.print("Nhập mã KH cần xóa: ");
                    String maXoa = sc.nextLine();
                    KhachHang kh = dsKh.timTheoMa(maXoa);
                    if (kh != null) {
                        // chưa có phương thức xóa trong DsKhachHang, bạn có thể bổ sung
                        System.out.println("Đã tìm thấy KH, cần bổ sung hàm xóa!");
                    } else {
                        System.out.println("Không tìm thấy KH!");
                    }
                    break;
                case 3:
                    System.out.print("Nhập tên KH: ");
                    dsKh.timTheoTen(sc.nextLine());
                    break;
                case 4:
                    dsKh.hienThi();
                    break;
            }
        } while (choice != 0);
    }

    // Menu cho Quản lý
    private static void quanLyMenu(Scanner sc, DsKhachHang dsKh, DsNhanVien dsNv) {
        int choice;
        do {
            System.out.println("===== MENU QUẢN LÝ =====");
            System.out.println("1. Xem danh sách khách hàng");
            System.out.println("2. Thêm nhân viên");
            System.out.println("3. Xóa nhân viên theo mã");
            System.out.println("4. Tìm nhân viên theo tên");
            System.out.println("5. Hiển thị danh sách nhân viên");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    dsKh.hienThi();
                    break;
                case 2:
                    System.out.print("Nhập mã NV: ");
                    String ma = sc.nextLine();
                    System.out.print("Nhập họ tên: ");
                    String ten = sc.nextLine();
                    System.out.print("Nhập số điện thoại: ");
                    String sdt = sc.nextLine();
                    System.out.print("Nhập chức vụ: ");
                    String cv = sc.nextLine();
                    System.out.print("Nhập lương: ");
                    double luong = Double.parseDouble(sc.nextLine());
                    dsNv.themNhanVien(new NhanVien(ma, ten, sdt, cv, luong, null));
                    break;
                case 3:
                    System.out.print("Nhập mã NV cần xóa: ");
                    dsNv.xoaNhanVienTheoMa(sc.nextLine());
                    break;
                case 4:
                    System.out.print("Nhập tên NV: ");
                    for (NhanVien nv : dsNv.timKiemTheoTen(sc.nextLine())) {
                        nv.hienThi();
                    }
                    break;
                case 5:
                    dsNv.hienThi();
                    break;
            }
        } while (choice != 0);
    }

    // Menu cho Admin
    private static void adminMenu(Scanner sc) {
        int choice;
        do {
            System.out.println("===== MENU ADMIN =====");
            System.out.println("1. Thêm tài khoản nhân viên");
            System.out.println("2. Thêm tài khoản quản lý");
            System.out.println("0. Thoát");
            System.out.print("Chọn: ");
            choice = Integer.parseInt(sc.nextLine());

            switch (choice) {
                case 1:
                    System.out.print("Tên đăng nhập: ");
                    String userNV = sc.nextLine();
                    System.out.print("Mật khẩu: ");
                    String passNV = sc.nextLine();
                    TaiKhoan tkNV = new TaiKhoan(userNV, passNV, QuyenHan.Nhan_vien);
                    System.out.println("Đã tạo tài khoản nhân viên!");
                    tkNV.hienThi();
                    break;
                case 2:
                    System.out.print("Tên đăng nhập: ");
                    String userQL = sc.nextLine();
                    System.out.print("Mật khẩu: ");
                    String passQL = sc.nextLine();
                    TaiKhoan tkQL = new TaiKhoan(userQL, passQL, QuyenHan.Quan_ly);
                    System.out.println("Đã tạo tài khoản quản lý!");
                    tkQL.hienThi();
                    break;
            }
        } while (choice != 0);
    }
}
