import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DanhSachPhieuNhap implements ghiDocFile {
    private static final String FILE_PHIEU_NHAP = "PhieuNhap.txt";

    private ArrayList<PhieuNhap> dsPhieu = new ArrayList<>();
    private DanhSachNhaCungCap dsNCC; // de kiem tra ma NCC co ton tai khi lap phieu

    public DanhSachPhieuNhap(DanhSachNhaCungCap dsNCC) {
        this.dsNCC = dsNCC;
    }

    // ===== tim kiem =====

    // tim phieu theo ma, khong thay thi tra ve null
    public PhieuNhap timTheoMa(String ma) {
        for (PhieuNhap ph : dsPhieu) {
            if (ph.getMaPN().equalsIgnoreCase(ma)) {
                return ph;
            }
        }
        return null;
    }

    // tim 1 dong chi tiet trong phieu, khong thay thi tra ve null
    private ChiTietPhieuNhap timChiTiet(PhieuNhap ph, String maCT) {
        for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
            if (ct.getMaCTPN().equalsIgnoreCase(maCT)) {
                return ct;
            }
        }
        return null;
    }

    // ===== sinh ma tu dong: lay so lon nhat dang co + 1 =====

    public String sinhMaPN() {
        int max = 0;
        for (PhieuNhap ph : dsPhieu) {
            max = Math.max(max, NhapTuBanPhim.soCuoi(ph.getMaPN(), "PN"));
        }
        return String.format("PN%03d", max + 1);
    }

    // ma chi tiet danh so chung cho tat ca phieu
    private String sinhMaCT() {
        int max = 0;
        for (PhieuNhap ph : dsPhieu) {
            for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                max = Math.max(max, NhapTuBanPhim.soCuoi(ct.getMaCTPN(), "CT"));
            }
        }
        return String.format("CT%03d", max + 1);
    }

    // ma nhap lieu cung danh so chung
    private String sinhMaNL() {
        int max = 0;
        for (PhieuNhap ph : dsPhieu) {
            for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                max = Math.max(max, NhapTuBanPhim.soCuoi(ct.getMaNhapLieu(), "NL"));
            }
        }
        return String.format("NL%03d", max + 1);
    }

    // ===== them / sua / xoa phieu =====

    // them 1 phieu, tra ve false neu null, trung ma hoac NCC khong ton tai
    public boolean them(PhieuNhap ph) {
        if (ph == null) {
            return false;
        }
        if (timTheoMa(ph.getMaPN()) != null) {
            System.out.println("Ma phieu " + ph.getMaPN() + " da ton tai!");
            return false;
        }
        if (dsNCC.timTheoMa(ph.getMaNCC()) == null) {
            System.out.println("Nha cung cap " + ph.getMaNCC() + " khong ton tai!");
            return false;
        }
        dsPhieu.add(ph);
        return true;
    }

    // lap phieu moi tu ban phim, kem cac dong chi tiet
    public void nhapVaThem(Scanner sc) {
        if (dsNCC.laRong()) {
            System.out.println("Chua co nha cung cap nao, hay them nha cung cap truoc!");
            return;
        }
        String ma = sinhMaPN();
        System.out.println("Ma phieu nhap (tu sinh): " + ma);
        String ngay = NhapTuBanPhim.docNgay(sc, "Ngay nhap (dd/MM/yyyy)", null);
        dsNCC.hienThi();
        String maNCC = docMaNCC(sc, null);
        String maNV = NhapTuBanPhim.docChuoi(sc, "Ma nhan vien lap phieu", null);

        PhieuNhap ph = new PhieuNhap(ma, ngay, maNCC, maNV);
        them(ph);
        // nhap it nhat 1 dong chi tiet, sau do hoi co nhap them khong
        do {
            themChiTietTuBanPhim(ph, sc);
        } while (NhapTuBanPhim.hoiCoKhong(sc, "Them dong chi tiet nua?"));
        System.out.println("Lap phieu nhap thanh cong! Tong tien: " + ph.tinhTongTien());
    }

    // hoi ma NCC den khi nhap ma co that
    private String docMaNCC(Scanner sc, String cu) {
        while (true) {
            String ma = NhapTuBanPhim.docChuoi(sc, "Ma nha cung cap", cu);
            NhaCungCap ncc = dsNCC.timTheoMa(ma);
            if (ncc != null) {
                return ncc.getMaNCC();
            }
            System.out.println("Khong co nha cung cap nay!");
        }
    }

    // sua thong tin chung cua phieu (ngay, NCC, nhan vien)
    public boolean sua(String maPN, Scanner sc) {
        PhieuNhap ph = timTheoMa(maPN);
        if (ph == null) {
            System.out.println("Khong tim thay phieu nhap co ma: " + maPN);
            return false;
        }
        System.out.println("--- SUA PHIEU NHAP " + ph.getMaPN() + " ---");
        ph.setNgayNhap(NhapTuBanPhim.docNgay(sc, "Ngay nhap moi", ph.getNgayNhap()));
        ph.setMaNCC(docMaNCC(sc, ph.getMaNCC()));
        ph.setMaNV(NhapTuBanPhim.docChuoi(sc, "Ma nhan vien moi", ph.getMaNV()));
        System.out.println("Cap nhat thanh cong!");
        return true;
    }

    public boolean xoa(String maPN) {
        PhieuNhap ph = timTheoMa(maPN);
        if (ph == null) {
            return false;
        }
        dsPhieu.remove(ph);
        return true;
    }

    // ===== chi tiet phieu =====

    // nhap 1 dong chi tiet (ma SP, so luong, gia) roi them vao phieu
    private void themChiTietTuBanPhim(PhieuNhap ph, Scanner sc) {
        String maSP = NhapTuBanPhim.docChuoi(sc, "Ma san pham nhap", null);
        int soLuong = NhapTuBanPhim.docSoNguyenDuong(sc, "So luong nhap", null);
        double giaNhap = NhapTuBanPhim.docSoThucDuong(sc, "Gia nhap", null);

        NhapLieu nl = new NhapLieu(sinhMaNL(), maSP, soLuong, giaNhap);
        ph.themChiTiet(new ChiTietPhieuNhap(sinhMaCT(), ph.getMaPN(), nl));
    }

    public boolean themChiTiet(String maPN, Scanner sc) {
        PhieuNhap ph = timTheoMa(maPN);
        if (ph == null) {
            System.out.println("Khong tim thay phieu nhap co ma: " + maPN);
            return false;
        }
        themChiTietTuBanPhim(ph, sc);
        System.out.println("Them chi tiet thanh cong!");
        return true;
    }

    public boolean suaChiTiet(String maPN, String maCT, Scanner sc) {
        PhieuNhap ph = timTheoMa(maPN);
        if (ph == null) {
            System.out.println("Khong tim thay phieu nhap co ma: " + maPN);
            return false;
        }
        ChiTietPhieuNhap ct = timChiTiet(ph, maCT);
        if (ct == null) {
            System.out.println("Phieu " + ph.getMaPN() + " khong co chi tiet " + maCT);
            return false;
        }
        NhapLieu nl = ct.getNhapLieu();
        System.out.println("--- SUA CHI TIET " + ct.getMaCTPN() + " ---");
        nl.setMaSP(NhapTuBanPhim.docChuoi(sc, "Ma san pham moi", nl.getMaSP()));
        nl.setSoLuongNhap(NhapTuBanPhim.docSoNguyenDuong(sc, "So luong moi", nl.getSoLuongNhap()));
        nl.setGiaNhap(NhapTuBanPhim.docSoThucDuong(sc, "Gia nhap moi", nl.getGiaNhap()));
        System.out.println("Cap nhat thanh cong!");
        return true;
    }

    public boolean xoaChiTiet(String maPN, String maCT) {
        PhieuNhap ph = timTheoMa(maPN);
        if (ph == null) {
            return false;
        }
        ChiTietPhieuNhap ct = timChiTiet(ph, maCT);
        if (ct == null) {
            return false;
        }
        ph.getDsChiTiet().remove(ct);
        return true;
    }

    // ===== hien thi =====

    public void timKiem(String maPN) {
        PhieuNhap ph = timTheoMa(maPN);
        if (ph == null) {
            System.out.println("Khong tim thay phieu nhap co ma: " + maPN);
        } else {
            ph.hienThi();
        }
    }

    public void timTheoNCC(String maNCC) {
        boolean timThay = false;
        for (PhieuNhap ph : dsPhieu) {
            if (ph.getMaNCC().equalsIgnoreCase(maNCC)) {
                ph.hienThi();
                System.out.println("--------------------");
                timThay = true;
            }
        }
        if (!timThay) {
            System.out.println("Khong co phieu nhap nao cua NCC " + maNCC);
        }
    }

    public void hienThi() {
        if (dsPhieu.isEmpty()) {
            System.out.println("Danh sach phieu nhap dang trong.");
            return;
        }
        for (PhieuNhap ph : dsPhieu) {
            ph.hienThi();
            System.out.println("--------------------");
        }
    }

    // ===== doc / ghi file =====
    // dong phieu:     PN;maPN;ngayNhap;maNCC;maNV
    // dong chi tiet:  CT;maCTPN;maPN;maNhapLieu;maSP;soLuong;giaNhap
    // cac dong CT nam ngay duoi dong PN cua no

    @Override
    public void ghiFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_PHIEU_NHAP;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (PhieuNhap ph : dsPhieu) {
                pw.println("PN;" + ph.getMaPN() + ";" + ph.getNgayNhap() + ";"
                        + ph.getMaNCC() + ";" + ph.getMaNV());
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    NhapLieu nl = ct.getNhapLieu();
                    pw.println("CT;" + ct.getMaCTPN() + ";" + ph.getMaPN() + ";" + nl.getMaNhapLieu()
                            + ";" + nl.getMaSP() + ";" + nl.getSoLuongNhap() + ";" + nl.getGiaNhap());
                }
            }
            System.out.println("Ghi " + dsPhieu.size() + " phieu nhap vao file " + path + " thanh cong!");
        } catch (IOException e) {
            System.out.println("Loi khi ghi file: " + e.getMessage());
        }
    }

    @Override
    public void docFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_PHIEU_NHAP;
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("File " + path + " khong ton tai!");
            return;
        }
        dsPhieu.clear(); // xoa danh sach cu truoc khi nap tu file
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(";");
                try {
                    if (p[0].equals("PN") && p.length >= 5) {
                        // dong phieu: tao phieu (chua co chi tiet)
                        if (timTheoMa(p[1]) == null) {
                            dsPhieu.add(new PhieuNhap(p[1], p[2], p[3], p[4]));
                        }
                    } else if (p[0].equals("CT") && p.length >= 7) {
                        // dong chi tiet: gan vao phieu co ma p[2]
                        PhieuNhap ph = timTheoMa(p[2]);
                        NhapLieu nl = new NhapLieu(p[3], p[4], Integer.parseInt(p[5]), Double.parseDouble(p[6]));
                        ph.themChiTiet(new ChiTietPhieuNhap(p[1], p[2], nl));
                    }
                } catch (Exception e) {
                    System.out.println("Bo qua dong bi loi: " + line);
                }
            }
            System.out.println("Doc file " + path + " thanh cong! So phieu nap: " + dsPhieu.size());
        } catch (IOException e) {
            System.out.println("Loi khi doc file: " + e.getMessage());
        }
    }

    // ===== menu =====

    private String hoiMa(Scanner sc, String cauHoi) {
        System.out.print(cauHoi + ": ");
        return sc.nextLine().trim();
    }

    public void menu(Scanner sc) {
        int chon;
        do {
            System.out.println("\n===== QUAN LY PHIEU NHAP =====");
            System.out.println("1. Xem tat ca phieu nhap");
            System.out.println("2. Lap phieu nhap moi");
            System.out.println("3. Tim phieu theo ma");
            System.out.println("4. Tim phieu theo nha cung cap");
            System.out.println("5. Sua phieu (ngay, NCC, nhan vien)");
            System.out.println("6. Them chi tiet vao phieu");
            System.out.println("7. Sua chi tiet");
            System.out.println("8. Xoa chi tiet");
            System.out.println("9. Xoa phieu");
            System.out.println("10. Doc tu file");
            System.out.println("11. Ghi ra file");
            System.out.println("0. Quay lai");
            chon = NhapTuBanPhim.docLuaChon(sc);

            if (chon == 1) {
                hienThi();
            } else if (chon == 2) {
                nhapVaThem(sc);
            } else if (chon == 3) {
                timKiem(hoiMa(sc, "Nhap ma phieu"));
            } else if (chon == 4) {
                timTheoNCC(hoiMa(sc, "Nhap ma NCC"));
            } else if (chon == 5) {
                sua(hoiMa(sc, "Nhap ma phieu can sua"), sc);
            } else if (chon == 6) {
                themChiTiet(hoiMa(sc, "Nhap ma phieu"), sc);
            } else if (chon == 7) {
                String maPN = hoiMa(sc, "Nhap ma phieu");
                String maCT = hoiMa(sc, "Nhap ma chi tiet can sua");
                suaChiTiet(maPN, maCT, sc);
            } else if (chon == 8) {
                String maPN = hoiMa(sc, "Nhap ma phieu");
                String maCT = hoiMa(sc, "Nhap ma chi tiet can xoa");
                if (xoaChiTiet(maPN, maCT)) {
                    System.out.println("Xoa thanh cong!");
                } else {
                    System.out.println("Khong tim thay phieu hoac chi tiet nay!");
                }
            } else if (chon == 9) {
                String maPN = hoiMa(sc, "Nhap ma phieu can xoa");
                if (xoa(maPN)) {
                    System.out.println("Xoa thanh cong!");
                } else {
                    System.out.println("Khong tim thay phieu " + maPN);
                }
            } else if (chon == 10) {
                docFile(FILE_PHIEU_NHAP);
            } else if (chon == 11) {
                ghiFile(FILE_PHIEU_NHAP);
            } else if (chon != 0) {
                System.out.println("Lua chon khong hop le!");
            }
        } while (chon != 0);
    }
}
