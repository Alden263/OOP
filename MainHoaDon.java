import java.util.Scanner;

public class MainHoaDon {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        DanhSachHoaDon qlhd = new DanhSachHoaDon();

        // Nap san du lieu mau ban dau de giang vien co the test ngay
        khoiTaoDuLieuMau(qlhd);

        int luaChon = -1;
        do {
            System.out.println("\n========================================================");
            System.out.println("          CHUONG TRINH QUAN LY HOA DON (OOP)            ");
            System.out.println("========================================================");
            System.out.println("  1. Xem danh sach tat ca hoa don");
            System.out.println("  2. Them hoa don moi (Ban le / Ban si)");
            System.out.println("  3. Them chi tiet san pham vao mot hoa don");
            System.out.println("  4. Xoa mot chi tiet san pham trong hoa don");
            System.out.println("  5. Xoa hoa don theo ma");
            System.out.println("  6. Sua thong tin hoa don theo ma");
            System.out.println("  7. Tim kiem hoa don theo ma");
            System.out.println("  8. Tim kiem hoa don theo ma khach hang");
            System.out.println("  9. Thong ke doanh thu & Hoa don gia tri cao nhat");
            System.out.println(" 10. Sap xep hoa don theo tong tien giam dan");
            System.out.println(" 11. Ghi danh sach hoa don ra file text");
            System.out.println(" 12. Doc danh sach hoa don tu file text");
            System.out.println("  0. Thoat chuong trinh");
            System.out.println("========================================================");
            System.out.print("Moi ban chon chuc nang (0-12): ");

            try {
                luaChon = Integer.parseInt(sc.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Vui long nhap so tu 0 den 12!");
                continue;
            }

            switch (luaChon) {
                case 1:
                    qlhd.hienThi();
                    break;

                case 2:
                    themHoaDonMoi(qlhd, sc);
                    break;

                case 3:
                    themChiTietVaoHoaDon(qlhd, sc);
                    break;

                case 4:
                    xoaChiTietTrongHoaDon(qlhd, sc);
                    break;

                case 5:
                    System.out.print("Nhap ma hoa don can xoa: ");
                    String maXoa = sc.nextLine().trim();
                    qlhd.xoaHoaDon(maXoa);
                    break;

                case 6:
                    System.out.print("Nhap ma hoa don can sua: ");
                    String maSua = sc.nextLine().trim();
                    qlhd.suaHoaDon(maSua, sc);
                    break;

                case 7:
                    System.out.print("Nhap ma hoa don can tim: ");
                    String maTim = sc.nextLine().trim();
                    HoaDon hdTim = qlhd.timHoaDon(maTim);
                    if (hdTim != null) {
                        hdTim.inHoaDon();
                    } else {
                        System.out.println("Khong tim thay hoa don voi ma: " + maTim);
                    }
                    break;

                case 8:
                    System.out.print("Nhap ma khach hang can tim hoa don: ");
                    String maKH = sc.nextLine().trim();
                    qlhd.timKiemTheoMaKH(maKH);
                    break;

                case 9:
                    System.out.printf("\n>>> TONG DOANH THU TOAN HE THONG: %,.2f VND\n", qlhd.tinhTongDoanhThu());
                    HoaDon maxHD = qlhd.timHoaDonCaoNhat();
                    if (maxHD != null) {
                        System.out.println(">>> HOA DON CO GIA TRI CAO NHAT:");
                        maxHD.inHoaDon();
                    }
                    break;

                case 10:
                    qlhd.sapXepTheoTongTienGiamDan();
                    qlhd.hienThi();
                    break;

                case 11:
                    qlhd.ghiFile();
                    break;

                case 12:
                    qlhd.docFile();
                    break;

                case 0:
                    System.out.println("Cam on ban da su dung chuong trinh! Tam biet.");
                    break;

                default:
                    System.out.println("Lua chon khong hop le! Vui long chon tu 0 den 12.");
            }

            if (luaChon != 0) {
                System.out.print("\nNhan Enter de tiep tuc...");
                sc.nextLine();
            }
        } while (luaChon != 0);
    }

    private static void themHoaDonMoi(DanhSachHoaDon qlhd, Scanner sc) {
        System.out.println("\n--- CHON LOAI HOA DON ---");
        System.out.println("1. Hoa don ban le");
        System.out.println("2. Hoa don ban si (Dai ly co chiet khau)");
        System.out.print("Chon (1 hoac 2): ");
        int loai;
        try {
            loai = Integer.parseInt(sc.nextLine().trim());
        } catch (Exception e) {
            System.out.println("Nhap sai! Mac dinh chon Ban le.");
            loai = 1;
        }

        HoaDon hd;
        if (loai == 2) {
            hd = new HoaDonBanSi();
        } else {
            hd = new HoaDonBanLe();
        }

        hd.nhap(sc);

        // Nhap luon chi tiet cho hoa don moi
        System.out.print("Ban co muon nhap chi tiet san pham cho hoa don nay ngay khong? (y/n): ");
        String rep = sc.nextLine().trim();
        while (rep.equalsIgnoreCase("y")) {
            ChiTietHoaDon ct = new ChiTietHoaDon();
            ct.nhap(sc, hd.getMaHD());
            hd.themChiTiet(ct);

            System.out.print("Tiep tuc them chi tiet san pham khac? (y/n): ");
            rep = sc.nextLine().trim();
        }

        qlhd.themHoaDon(hd);
    }

    private static void themChiTietVaoHoaDon(DanhSachHoaDon qlhd, Scanner sc) {
        System.out.print("Nhap ma hoa don can them chi tiet: ");
        String maHD = sc.nextLine().trim();
        HoaDon hd = qlhd.timHoaDon(maHD);
        if (hd == null) {
            System.out.println("Khong tim thay hoa don voi ma: " + maHD);
            return;
        }

        ChiTietHoaDon ct = new ChiTietHoaDon();
        ct.nhap(sc, hd.getMaHD());
        hd.themChiTiet(ct);
        System.out.println("Da them chi tiet san pham vao hoa don " + maHD + "!");
        hd.inHoaDon();
    }

    private static void xoaChiTietTrongHoaDon(DanhSachHoaDon qlhd, Scanner sc) {
        System.out.print("Nhap ma hoa don: ");
        String maHD = sc.nextLine().trim();
        HoaDon hd = qlhd.timHoaDon(maHD);
        if (hd == null) {
            System.out.println("Khong tim thay hoa don voi ma: " + maHD);
            return;
        }

        System.out.print("Nhap ma san pham can xoa khoi hoa don: ");
        String maSP = sc.nextLine().trim();
        boolean kq = hd.xoaChiTiet(maSP);
        if (kq) {
            System.out.println("Da xoa san pham " + maSP + " khoi hoa don " + maHD + "!");
            hd.inHoaDon();
        } else {
            System.out.println("Khong tim thay san pham " + maSP + " trong hoa don nay.");
        }
    }

    private static void khoiTaoDuLieuMau(DanhSachHoaDon qlhd) {
        // 1. Tao hoa don ban le mau
        HoaDonBanLe hd1 = new HoaDonBanLe("HD001", "01/11/2026", "KH01", "NV01", 10);
        hd1.themChiTiet(new ChiTietHoaDon("HD001", "SP01", 2, 150000.0));
        hd1.themChiTiet(new ChiTietHoaDon("HD001", "SP02", 1, 500000.0));
        qlhd.themHoaDon(hd1);

        // 2. Tao hoa don ban si mau
        HoaDonBanSi hd2 = new HoaDonBanSi("HD002", "02/11/2026", "KH02", "NV02", 10.0, "Cong ty Phuong Nam");
        hd2.themChiTiet(new ChiTietHoaDon("HD002", "SP03", 20, 200000.0));
        hd2.themChiTiet(new ChiTietHoaDon("HD002", "SP04", 15, 120000.0));
        qlhd.themHoaDon(hd2);
    }
}
