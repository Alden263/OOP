import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DanhSachNhaCungCap implements IQuanLy, ghiDocFile {
    private static final String FILE_NCC = "NhaCungCap.txt";

    private ArrayList<NhaCungCap> dsNCC = new ArrayList<>();
    private Scanner sc; // dung chung Scanner voi Main, khong tao Scanner moi
    private DanhSachPhieuNhap dsPhieuNhap; // de biet NCC nao dang co phieu nhap, co the null

    public DanhSachNhaCungCap(Scanner sc) {
        this.sc = sc;
    }

    // DanhSachPhieuNhap tu goi ham nay trong constructor cua no
    public void setDsPhieuNhap(DanhSachPhieuNhap dsPhieuNhap) {
        this.dsPhieuNhap = dsPhieuNhap;
    }

    public boolean laRong() {
        return dsNCC.isEmpty();
    }

    // tim theo ma, khong thay thi tra ve null
    public NhaCungCap timTheoMa(String ma) {
        for (NhaCungCap ncc : dsNCC) {
            if (ncc.getMaNCC().equalsIgnoreCase(ma)) {
                return ncc;
            }
        }
        return null;
    }

    // sinh ma tu dong: lay so lon nhat dang co + 1 -> NCC001, NCC002...
    public String sinhMa() {
        int max = 0;
        for (NhaCungCap ncc : dsNCC) {
            max = Math.max(max, NhapTuBanPhim.soCuoi(ncc.getMaNCC(), "NCC"));
        }
        return String.format("NCC%03d", max + 1);
    }

    // ===== 4 ham cua interface IQuanLy =====

    // nhap thong tin tu ban phim roi them vao danh sach
    @Override
    public void them() {
        String ma = sinhMa();
        System.out.println("Ma NCC (tu sinh): " + ma);
        String ten = NhapTuBanPhim.docChuoi(sc, "Ten NCC", null);
        String diaChi = NhapTuBanPhim.docChuoi(sc, "Dia chi", null);
        String sdt = NhapTuBanPhim.docSoDienThoai(sc, "So dien thoai", null);
        if (them(new NhaCungCap(ma, ten, diaChi, sdt))) {
            System.out.println("Them nha cung cap thanh cong!");
        }
    }

    @Override
    public void xoa(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            System.out.println("Khong tim thay NCC co ma: " + ma);
            return;
        }
        // NCC dang co phieu nhap thi khong cho xoa, tranh phieu tro toi NCC khong ton tai
        if (dsPhieuNhap != null && dsPhieuNhap.coPhieuCuaNCC(ncc.getMaNCC())) {
            System.out.println("Khong the xoa: NCC " + ncc.getMaNCC()
                    + " dang co phieu nhap. Hay xoa cac phieu nhap do truoc!");
            return;
        }
        dsNCC.remove(ncc);
        System.out.println("Xoa thanh cong!");
    }

    @Override
    public void timKiem(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            System.out.println("Khong tim thay NCC co ma: " + ma);
        } else {
            System.out.println(ncc);
        }
    }

    @Override
    public void hienThi() {
        if (dsNCC.isEmpty()) {
            System.out.println("Danh sach nha cung cap dang trong.");
            return;
        }
        for (int i = 0; i < dsNCC.size(); i++) {
            System.out.println((i + 1) + ". " + dsNCC.get(i));
        }
    }

    // ===== cac ham them ngoai interface =====

    // nap chong (overload) voi them(): them 1 NCC co san, tra ve false neu null hoac trung ma
    public boolean them(NhaCungCap ncc) {
        if (ncc == null) {
            return false;
        }
        if (timTheoMa(ncc.getMaNCC()) != null) {
            System.out.println("Ma NCC " + ncc.getMaNCC() + " da ton tai!");
            return false;
        }
        dsNCC.add(ncc);
        return true;
    }

    public void sua(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            System.out.println("Khong tim thay NCC co ma: " + ma);
            return;
        }
        System.out.println("--- SUA NHA CUNG CAP " + ncc.getMaNCC() + " ---");
        ncc.setTenNCC(NhapTuBanPhim.docChuoi(sc, "Ten moi", ncc.getTenNCC()));
        ncc.setDiaChi(NhapTuBanPhim.docChuoi(sc, "Dia chi moi", ncc.getDiaChi()));
        ncc.setSoDienThoai(NhapTuBanPhim.docSoDienThoai(sc, "SDT moi", ncc.getSoDienThoai()));
        System.out.println("Cap nhat thanh cong!");
    }

    // ===== doc / ghi file (interface ghiDocFile) =====
    // moi dong trong file: maNCC;tenNCC;diaChi;soDienThoai

    @Override
    public void ghiFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_NCC;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (NhaCungCap ncc : dsNCC) {
                pw.println(ncc.getMaNCC() + ";" + ncc.getTenNCC() + ";"
                        + ncc.getDiaChi() + ";" + ncc.getSoDienThoai());
            }
            System.out.println("Ghi " + dsNCC.size() + " nha cung cap vao file " + path + " thanh cong!");
        } catch (IOException e) {
            System.out.println("Loi khi ghi file: " + e.getMessage());
        }
    }

    @Override
    public void docFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_NCC;
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("File " + path + " khong ton tai!");
            return;
        }
        dsNCC.clear(); // xoa danh sach cu truoc khi nap tu file
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(";");
                if (p.length < 4) {
                    continue; // dong thieu du lieu thi bo qua
                }
                them(new NhaCungCap(p[0], p[1], p[2], p[3]));
            }
            System.out.println("Doc file " + path + " thanh cong! So NCC nap: " + dsNCC.size());
        } catch (IOException e) {
            System.out.println("Loi khi doc file: " + e.getMessage());
        }
    }

    // ===== menu =====

    private String hoiMa(String cauHoi) {
        System.out.print(cauHoi + ": ");
        return sc.nextLine().trim();
    }

    // menu quan ly nha cung cap, Main chi can goi ham nay
    public void menu() {
        int chon;
        do {
            System.out.println("\n===== QUAN LY NHA CUNG CAP =====");
            System.out.println("1. Xem danh sach");
            System.out.println("2. Them nha cung cap");
            System.out.println("3. Sua nha cung cap");
            System.out.println("4. Xoa nha cung cap");
            System.out.println("5. Tim theo ma");
            System.out.println("6. Doc tu file");
            System.out.println("7. Ghi ra file");
            System.out.println("0. Quay lai");
            chon = NhapTuBanPhim.docLuaChon(sc);

            if (chon == 1) {
                hienThi();
            } else if (chon == 2) {
                them();
            } else if (chon == 3) {
                sua(hoiMa("Nhap ma NCC can sua"));
            } else if (chon == 4) {
                xoa(hoiMa("Nhap ma NCC can xoa"));
            } else if (chon == 5) {
                timKiem(hoiMa("Nhap ma NCC can tim"));
            } else if (chon == 6) {
                docFile(FILE_NCC);
            } else if (chon == 7) {
                ghiFile(FILE_NCC);
            } else if (chon != 0) {
                System.out.println("Lua chon khong hop le!");
            }
        } while (chon != 0);
    }
}
