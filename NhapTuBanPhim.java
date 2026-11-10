import java.time.LocalDate;
import java.util.Scanner;

// cac ham nhap du lieu tu ban phim co kiem tra, hoi lai den khi nhap dung
// tham so "cu": khi sua thi truyen gia tri cu vao, bam Enter la giu nguyen
//               khi them moi thi truyen null
public class NhapTuBanPhim {

    // in cau hoi, doc 1 dong. Neu bam Enter va co gia tri cu thi tra ve gia tri cu
    private static String hoi(Scanner sc, String cauHoi, String cu) {
        if (cu == null) {
            System.out.print(cauHoi + ": ");
        } else {
            System.out.print(cauHoi + " (Enter de giu nguyen [" + cu + "]): ");
        }
        String s = sc.nextLine().trim();
        if (s.isEmpty() && cu != null) {
            return cu;
        }
        return s;
    }

    // chuoi khong rong, khong chua dau ; (vi file dung dau ; de phan cach)
    public static String docChuoi(Scanner sc, String cauHoi, String cu) {
        String s = hoi(sc, cauHoi, cu);
        while (s.isEmpty() || s.contains(";")) {
            System.out.println("Khong duoc de trong va khong duoc chua dau ;");
            s = hoi(sc, cauHoi, cu);
        }
        return s;
    }

    // so dien thoai: 9 den 11 chu so
    public static String docSoDienThoai(Scanner sc, String cauHoi, String cu) {
        String s = hoi(sc, cauHoi, cu);
        while (!s.matches("[0-9]{9,11}")) {
            System.out.println("So dien thoai phai gom 9-11 chu so!");
            s = hoi(sc, cauHoi, cu);
        }
        return s;
    }

    // so nguyen > 0
    public static int docSoNguyenDuong(Scanner sc, String cauHoi, Integer cu) {
        String cuChuoi = (cu == null) ? null : String.valueOf(cu);
        while (true) {
            String s = hoi(sc, cauHoi, cuChuoi);
            try {
                int so = Integer.parseInt(s);
                if (so > 0) {
                    return so;
                }
            } catch (NumberFormatException e) {
                // nhap khong phai so thi roi xuong thong bao loi ben duoi
            }
            System.out.println("Phai la so nguyen duong!");
        }
    }

    // so thuc > 0
    public static double docSoThucDuong(Scanner sc, String cauHoi, Double cu) {
        String cuChuoi = (cu == null) ? null : String.valueOf(cu);
        while (true) {
            String s = hoi(sc, cauHoi, cuChuoi);
            try {
                double so = Double.parseDouble(s);
                if (so > 0 && !Double.isInfinite(so)) {
                    return so;
                }
            } catch (NumberFormatException e) {
                // nhap khong phai so thi roi xuong thong bao loi ben duoi
            }
            System.out.println("Phai la so duong!");
        }
    }

    // ngay dang dd/MM/yyyy
    public static String docNgay(Scanner sc, String cauHoi, String cu) {
        String s = hoi(sc, cauHoi, cu);
        while (!laNgayHopLe(s)) {
            System.out.println("Ngay khong hop le, nhap dang dd/MM/yyyy!");
            s = hoi(sc, cauHoi, cu);
        }
        return s;
    }

    // kiem tra dd/MM/yyyy, LocalDate.of se bao loi neu ngay khong co that (vd 31/02)
    public static boolean laNgayHopLe(String s) {
        String[] p = s.split("/");
        if (p.length != 3 || p[2].length() != 4) {
            return false;
        }
        try {
            LocalDate.of(Integer.parseInt(p[2]), Integer.parseInt(p[1]), Integer.parseInt(p[0]));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // hoi co/khong
    public static boolean hoiCoKhong(Scanner sc, String cauHoi) {
        while (true) {
            System.out.print(cauHoi + " (c/k): ");
            String s = sc.nextLine().trim();
            if (s.equalsIgnoreCase("c")) {
                return true;
            }
            if (s.equalsIgnoreCase("k")) {
                return false;
            }
            System.out.println("Chi nhap c hoac k!");
        }
    }

    // doc lua chon menu, nhap sai thi tra -1
    public static int docLuaChon(Scanner sc) {
        System.out.print("Nhap lua chon: ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Vui long nhap so!");
            return -1;
        }
    }

    // lay phan so o cuoi ma, vd soCuoi("NCC012", "NCC") = 12. Sai dinh dang thi tra 0
    public static int soCuoi(String ma, String tienTo) {
        if (ma == null || !ma.startsWith(tienTo)) {
            return 0;
        }
        try {
            return Integer.parseInt(ma.substring(tienTo.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
