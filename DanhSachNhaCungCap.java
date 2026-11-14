import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

public class DanhSachNhaCungCap implements IQuanLy, ghiDocFile {
    private static final String FILE_NCC = "NhaCungCap.txt";

    private static final int SUC_CHUA_BAN_DAU = 10;

    private NhaCungCap[] ds = new NhaCungCap[SUC_CHUA_BAN_DAU]; // mảng theo số đồ lớp
    private int soLuong = 0;                                    // số NCC đang có trong mảng
    private Scanner sc; // dùng chung Scanner với Main, không tạo Scanner mới
    private DanhSachPhieuNhap dsPhieuNhap; // để biết NCC nào đang có phiếu nhập, có thể null

    public DanhSachNhaCungCap(Scanner sc) {
        this.sc = sc;
    }

    // DanhSachPhieuNhap tự gọi hàm này trong constructor của nó
    public void setDsPhieuNhap(DanhSachPhieuNhap dsPhieuNhap) {
        this.dsPhieuNhap = dsPhieuNhap;
    }

    public boolean laRong() {
        return soLuong == 0;
    }

    // tìm theo mã, không thấy thì trả về null
    public NhaCungCap timTheoMa(String ma) {
        for (int i = 0; i < soLuong; i++) {
            if (ds[i].getMaNCC().equalsIgnoreCase(ma)) {
                return ds[i];
            }
        }
        return null;
    }

    // sinh mã tự động: lấy số lớn nhất đang có + 1 -> NCC001, NCC002...
    public String sinhMa() {
        int max = 0;
        for (int i = 0; i < soLuong; i++) {
            max = Math.max(max, NhapTuBanPhim.soCuoi(ds[i].getMaNCC(), "NCC"));
        }
        return String.format("NCC%03d", max + 1);
    }

    // ===== 5 HÀM TUÂN THỦ CHUẨN GIAO TIẾP (INTERFACE) IQuanLy =====

    @Override
    public void them(Scanner sc) {
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
    public void sua(String ma, Scanner sc) {
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

    @Override
    public boolean xoa(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            System.out.println("Khong tim thay NCC co ma: " + ma);
            return false;
        }
        // NCC đang có phiếu nhập thì không cho xóa, tránh phiếu trỏ tới NCC không tồn tại
        if (dsPhieuNhap != null && dsPhieuNhap.coPhieuCuaNCC(ncc.getMaNCC())) {
            System.out.println("Khong the xoa: NCC " + ncc.getMaNCC()
                    + " dang co phieu nhap. Hay xoa cac phieu nhap do truoc!");
            return false;
        }
        // tìm vị trí của NCC rồi dồn các phần tử phía sau lên 1 ô
        int viTri = 0;
        while (viTri < soLuong && ds[viTri] != ncc) {
            viTri++;
        }
        for (int i = viTri; i < soLuong - 1; i++) {
            ds[i] = ds[i + 1];
        }
        soLuong--;
        ds[soLuong] = null;
        System.out.println("Xoa thanh cong!");
        return true;
    }

    @Override
    public void timKiem(Scanner sc) {
        System.out.print("Nhap ma NCC can tim: ");
        String ma = sc.nextLine().trim();
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            System.out.println("Khong tim thay NCC co ma: " + ma);
        } else {
            System.out.println(ncc);
        }
    }

    @Override
    public void hienThi() {
        if (soLuong == 0) {
            System.out.println("Danh sach nha cung cap dang trong.");
            return;
        }
        for (int i = 0; i < soLuong; i++) {
            System.out.println((i + 1) + ". " + ds[i]);
        }
    }

    // ===== CÁC HÀM BỔ TRỢ NGOÀI INTERFACE =====

    // nạp chồng (overload) với them(Scanner sc): thêm 1 NCC có sẵn, trả về false nếu null hoặc trùng mã
    public boolean them(NhaCungCap ncc) {
        if (ncc == null) {
            return false;
        }
        if (timTheoMa(ncc.getMaNCC()) != null) {
            System.out.println("Ma NCC " + ncc.getMaNCC() + " da ton tai!");
            return false;
        }
        // mảng đầy thì nới rộng gấp đôi
        if (soLuong == ds.length) {
            ds = Arrays.copyOf(ds, ds.length * 2);
        }
        ds[soLuong] = ncc;
        soLuong++;
        return true;
    }

    // ===== ĐỌC / GHI FILE (interface ghiDocFile) =====
    // mỗi dòng trong file: maNCC;tenNCC;diaChi;soDienThoai

    @Override
    public void ghiFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_NCC;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (int i = 0; i < soLuong; i++) {
                NhaCungCap ncc = ds[i];
                pw.println(ncc.getMaNCC() + ";" + ncc.getTenNCC() + ";"
                        + ncc.getDiaChi() + ";" + ncc.getSoDienThoai());
            }
            System.out.println("Ghi " + soLuong + " nha cung cap vao file " + path + " thanh cong!");
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
        // xóa danh sách cũ trước khi nạp từ file
        ds = new NhaCungCap[SUC_CHUA_BAN_DAU];
        soLuong = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(";");
                if (p.length < 4) {
                    continue; // dòng thiếu dữ liệu thì bỏ qua
                }
                them(new NhaCungCap(p[0], p[1], p[2], p[3]));
            }
            System.out.println("Doc file " + path + " thanh cong! So NCC nap: " + soLuong);
        } catch (IOException e) {
            System.out.println("Loi khi đọc file: " + e.getMessage());
        }
    }

    // ===== MENU =====

    private String hoiMa(String cauHoi) {
        System.out.print(cauHoi + ": ");
        return sc.nextLine().trim();
    }

    // menu quản lý nhà cung cấp, Main chỉ cần gọi hàm này
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
                them(sc);
            } else if (chon == 3) {
                sua(hoiMa("Nhap ma NCC can sua"), sc);
            } else if (chon == 4) {
                xoa(hoiMa("Nhap ma NCC can xoa"));
            } else if (chon == 5) {
                timKiem(sc);
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