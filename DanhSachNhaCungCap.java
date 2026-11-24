import java.io.*;
import java.util.Arrays;

public class DanhSachNhaCungCap implements ghiDocFile {
    private static final String FILE_NCC = "NhaCungCap.txt";

    private static final int SUC_CHUA_BAN_DAU = 10;

    private NhaCungCap[] ds = new NhaCungCap[SUC_CHUA_BAN_DAU]; // mảng theo số đồ lớp
    private int soLuong = 0;                                    // số NCC đang có trong mảng
    private DanhSachPhieuNhap dsPhieuNhap; // để biết NCC nào đang có phiếu nhập, có thể null

    public DanhSachNhaCungCap() {
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
            max = Math.max(max, TienIchMa.soCuoi(ds[i].getMaNCC(), "NCC"));
        }
        return String.format("NCC%03d", max + 1);
    }

    // xóa NCC theo mã; trả về true nếu xóa được, false nếu không có NCC đó hoặc NCC đang có phiếu nhập
    public boolean xoa(String ma) {
        NhaCungCap ncc = timTheoMa(ma);
        if (ncc == null) {
            return false;
        }
        // NCC đang có phiếu nhập thì không cho xóa, tránh phiếu trỏ tới NCC không tồn tại
        if (dsPhieuNhap != null && dsPhieuNhap.coPhieuCuaNCC(ncc.getMaNCC())) {
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
        return true;
    }

    // thêm 1 NCC có sẵn, trả về false nếu null hoặc trùng mã
    public boolean them(NhaCungCap ncc) {
        if (ncc == null) {
            return false;
        }
        if (timTheoMa(ncc.getMaNCC()) != null) {
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
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, java.nio.charset.StandardCharsets.UTF_8))) {
            for (int i = 0; i < soLuong; i++) {
                NhaCungCap ncc = ds[i];
                pw.println(ncc.getMaNCC() + ";" + ncc.getTenNCC() + ";"
                        + ncc.getDiaChi() + ";" + ncc.getSoDienThoai());
            }
            if (pw.checkError()) throw new IOException("Không thể ghi đầy đủ dữ liệu");
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
    }

    @Override
    public void docFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_NCC;
        File f = new File(path);
        if (!f.exists()) {
            return;
        }
        // xóa danh sách cũ trước khi nạp từ file
        ds = new NhaCungCap[SUC_CHUA_BAN_DAU];
        soLuong = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(f, java.nio.charset.StandardCharsets.UTF_8))) {
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
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
    }

    public NhaCungCap[] layDanhSach() { return Arrays.copyOf(ds, soLuong); }
}
