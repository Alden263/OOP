
import java.util.ArrayList;
import java.io.*;

public class DanhSachHoaDon {
    private ArrayList<HoaDon> ds;

    public DanhSachHoaDon() {
        ds = new ArrayList<>();
    }

    // Them hoa don
    public void themHoaDon(HoaDon hd) {
        if (hd != null && timHoaDon(hd.getMaHD()) == null) {
            ds.add(hd);
            System.out.println("Them hoa don thanh cong!");
        } else {
            System.out.println("Hoa don khong hop le hoac bi trung ma!");
        }
    }

    // Tim hoa don theo ma
    public HoaDon timHoaDon(String ma) {
        for (HoaDon hd : ds) {
            if (hd.getMaHD().equalsIgnoreCase(ma)) {
                return hd;
            }
        }

        return null;
    }

    // Hien thi danh sach hoa don
    public void hienThi() {
        if (ds.isEmpty()) {
            System.out.println("Danh sach hoa don rong!");
            return;
        }

        for (HoaDon hd : ds) {
            hd.inHoaDon();
        }
    }

    // Ghi danh sach ma hoa don va thong tin co ban ra file
    public void ghiFile() {
        try (PrintWriter pw = new PrintWriter(
                new FileWriter("HoaDon.txt"))) {

            for (HoaDon hd : ds) {
                pw.println(
                    hd.getMaHD() + ";" +
                    hd.getNgayLap() + ";" +
                    hd.getMaKH() + ";" +
                    hd.getMaNV() + ";" +
                    hd.getTongTien()
                );
            }

            System.out.println("Ghi file thanh cong!");

        } catch (IOException e) {
            System.out.println("Loi ghi file: " + e.getMessage());
        }
    }

    // Doc danh sach hoa don tu file
    public void docFile() {
        try (BufferedReader br = new BufferedReader(
                new FileReader("HoaDon.txt"))) {

            String line;

            while ((line = br.readLine()) != null) {
                String[] a = line.split(";");

                if (a.length == 5 && timHoaDon(a[0]) == null) {
                    HoaDon hd = new HoaDon(
                        a[0], a[1], a[2], a[3]
                    );

                    // File nay chi luu thong tin co ban
                    // va tong tien, khong luu chi tiet san pham.
                    ds.add(hd);
                }
            }

            System.out.println("Doc file thanh cong!");

        } catch (IOException e) {
            System.out.println("Loi doc file: " + e.getMessage());
        }
    }
}
