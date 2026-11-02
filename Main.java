import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        QuanLyCuaHang ql = new QuanLyCuaHang("CỬA HÀNG MÁY TÍNH");
        int chon;
        do {
            System.out.println("\n===== ỨNG DỤNG QUẢN LÝ CỬA HÀNG MÁY TÍNH =====");
            System.out.println("1. Quan ly san pham");
            System.out.println("2. Lap hoa don");
            System.out.println("3. Thong ke doanh thu");
            System.out.println("0. Thoat");
            System.out.print("Nhap lua chon: ");

            try {
                chon = Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap so!");
                chon = -1;
                continue;
            }

            switch (chon) {
                case 1:
                    ql.themSanPham();
                    break;

                case 2:
                    ql.lapHoaDon();
                    break;

                case 3:
                    ql.thongKeDoanhThu();
                    break;

                case 0:
                    System.out.println("Ket thuc chuong trinh!");
                    break;

                default:
                    System.out.println("Lua chon khong hop le!");
            }

        } while (chon != 0);
    }
}
