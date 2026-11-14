import java.util.Scanner;

public class ChiTietHoaDon {
    private String maHD;
    private String maSP;
    private int soLuong;
    private double donGia;

    // Constructor mac dinh
    public ChiTietHoaDon() {
        this.maHD = "";
        this.maSP = "";
        this.soLuong = 0;
        this.donGia = 0.0;
    }

    // Constructor co tham so
    public ChiTietHoaDon(String maHD, String maSP, int soLuong, double donGia) {
        this.maHD = maHD;
        this.maSP = maSP;
        this.soLuong = Math.max(0, soLuong);
        this.donGia = Math.max(0.0, donGia);
    }

    // Copy constructor (ham thiet lap sao chep)
    public ChiTietHoaDon(ChiTietHoaDon other) {
        if (other != null) {
            this.maHD = other.maHD;
            this.maSP = other.maSP;
            this.soLuong = other.soLuong;
            this.donGia = other.donGia;
        }
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong > 0) {
            this.soLuong = soLuong;
        }
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia >= 0) {
            this.donGia = donGia;
        }
    }

    public double tinhThanhTien() {
        return soLuong * donGia;
    }

    public void nhap(Scanner sc, String maHDMacDinh) {
        this.maHD = maHDMacDinh;
        System.out.print("Nhap ma san pham: ");
        this.maSP = sc.nextLine().trim();

        do {
            try {
                System.out.print("Nhap so luong (> 0): ");
                this.soLuong = Integer.parseInt(sc.nextLine().trim());
                if (this.soLuong <= 0) {
                    System.out.println("So luong phai lon hon 0! Vui long nhap lai.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Gia tri khong hop le! Vui long nhap so nguyen.");
                this.soLuong = 0;
            }
        } while (this.soLuong <= 0);

        do {
            try {
                System.out.print("Nhap don gia (>= 0): ");
                this.donGia = Double.parseDouble(sc.nextLine().trim());
                if (this.donGia < 0) {
                    System.out.println("Don gia khong duoc am! Vui long nhap lai.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Gia tri khong hop le! Vui long nhap so thuc.");
                this.donGia = -1;
            }
        } while (this.donGia < 0);
    }

    public void hienThi() {
        System.out.printf(
            "   | Ma SP: %-8s | So luong: %-4d | Don gia: %-12.2f | Thanh tien: %-12.2f |\n",
            maSP, soLuong, donGia, tinhThanhTien()
        );
    }

    public String toFileString() {
        return maHD + ";" + maSP + ";" + soLuong + ";" + donGia;
    }
}
