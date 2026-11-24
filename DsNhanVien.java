import java.util.ArrayList;

/**
 * Danh sách nhân viên của cửa hàng (kèm tài khoản đăng nhập của từng người).
 *
 * Mỗi nhân viên là một dòng trong file NhanVien.txt, các cột cách nhau bằng dấu ";":
 *   mã ; họ tên ; số điện thoại ; chức vụ ; lương ; tên đăng nhập ; mật khẩu đã băm ; mã quyền
 * Nhân viên chưa có tài khoản thì 3 cột cuối để trống.
 * File cũ chỉ có 5 cột (chưa có tài khoản) vẫn đọc được bình thường.
 */
public class DsNhanVien {
    private ArrayList<NhanVien> danhSach;
    private boolean tuDongLuu = true;

    public DsNhanVien() throws Exception {
        this(true);
    }

    public DsNhanVien(boolean tuDongLuu) throws Exception {
        this.tuDongLuu = tuDongLuu;
        danhSach = new ArrayList<>();
        if (tuDongLuu) docFile();
    }

    public void themNhanVien(NhanVien nv) {
        danhSach.add(nv);
        if (tuDongLuu) ghiFile();
    }

    public void xoaNhanVien(NhanVien nv) {
        danhSach.remove(nv);
        if (tuDongLuu) ghiFile();
    }

    public void xoaNhanVienTheoMa(String maNV) {
        NhanVien nv = timKiemTheoMa(maNV);
        if (nv != null) {
            danhSach.remove(nv);
            if (tuDongLuu) ghiFile();
        }
    }

    public NhanVien timKiemTheoMa(String maNV) {
        for (NhanVien nv : danhSach) {
            if (nv.getMa().equals(maNV)) {
                return nv;
            }
        }
        return null;
    }

    public ArrayList<NhanVien> timKiemTheoTen(String ten) {
        ArrayList<NhanVien> ketQua = new ArrayList<>();
        for (NhanVien nv : danhSach) {
            if (nv.getHoTen().equalsIgnoreCase(ten)) {
                ketQua.add(nv);
            }
        }
        return ketQua;
    }

    // ===================== TÀI KHOẢN / ĐĂNG NHẬP =====================

    /** Tìm nhân viên theo tên đăng nhập (không phân biệt hoa thường). Không có thì trả về null. */
    public NhanVien timTheoTenDangNhap(String tenDangNhap) {
        for (NhanVien nv : danhSach) {
            TaiKhoan tk = nv.getTaiKhoan();
            if (tk != null && tk.getTenDangNhap().equalsIgnoreCase(tenDangNhap)) {
                return nv;
            }
        }
        return null;
    }

    /** Kiểm tra đăng nhập. Đúng thì trả về nhân viên đó, sai thì trả về null. */
    public NhanVien dangNhap(String tenDangNhap, String matKhau) {
        for (NhanVien nv : danhSach) {
            TaiKhoan tk = nv.getTaiKhoan();
            if (tk != null && tk.dangNhap(tenDangNhap, matKhau)) {
                return nv;
            }
        }
        return null;
    }

    /** Đã có ít nhất một nhân viên có tài khoản chưa? */
    public boolean coTaiKhoan() {
        for (NhanVien nv : danhSach) {
            if (nv.getTaiKhoan() != null) return true;
        }
        return false;
    }

    // ===================== GHI / ĐỌC FILE =====================

    public void ghiFile() {
        ghiFile("NhanVien.txt");
    }

    /** Ghi cả danh sách ra file, mỗi nhân viên một dòng (định dạng xem ở đầu file này). */
    public void ghiFile(String duongDan) {
        String noiDung = "";
        for (NhanVien nv : danhSach) {
            TaiKhoan tk = nv.getTaiKhoan();
            // 3 cột cuối: có tài khoản thì ghi đủ, chưa có thì để trống (";;" + dấu ";" ngay trước đó).
            String cotTaiKhoan = (tk == null)
                    ? ";;"
                    : tk.getTenDangNhap() + ";" + tk.getMatKhauBam() + ";" + tk.getQuyenHan().getMaQuyen();
            noiDung += nv.getMa() + ";"
                    + nv.getHoTen() + ";"
                    + nv.getSoDienThoai() + ";"
                    + nv.getChucVu() + ";"
                    + nv.getLuong() + ";"
                    + cotTaiKhoan + "\n";
        }
        XuLyFile.ghiFile(duongDan, noiDung);
    }

    public void docFile() throws Exception {
        docFile("NhanVien.txt");
    }

    /** Đọc danh sách nhân viên từ file (dòng 5 cột hoặc 8 cột). */
    public void docFile(String duongDan) throws Exception {
        danhSach.clear();
        String noiDung = XuLyFile.docFile(duongDan);
        String[] dong = noiDung.split("\n");
        for (String s : dong) {
            if (!s.trim().isEmpty()) {
                // Tham số -1: giữ lại cả các cột trống ở cuối dòng (nhân viên chưa có tài khoản).
                String[] tt = s.split(";", -1);
                if (tt.length == 5 || tt.length == 8) {
                    NhanVien nv = new NhanVien(
                            tt[0],
                            tt[1],
                            tt[2],
                            tt[3],
                            Double.parseDouble(tt[4]),
                            docTaiKhoan(tt)
                    );
                    danhSach.add(nv);
                }
            }
        }
    }

    /** Dựng tài khoản từ 3 cột cuối của dòng. Dòng 5 cột hoặc cột tên đăng nhập trống nghĩa là chưa có tài khoản (null). */
    private TaiKhoan docTaiKhoan(String[] tt) {
        if (tt.length < 8 || tt[5].isEmpty()) return null;
        QuyenHan quyen = QuyenHan.timTheoMa(tt[7]);
        if (quyen == null) {
            throw new IllegalArgumentException("Mã quyền không hợp lệ trong file nhân viên: " + tt[7]);
        }
        if (timTheoTenDangNhap(tt[5]) != null) {
            throw new IllegalArgumentException("Trùng tên đăng nhập trong file nhân viên: " + tt[5]);
        }
        return TaiKhoan.tuFile(tt[5], tt[6], quyen);
    }

    public NhanVien[] layDanhSach() {
        return danhSach.toArray(new NhanVien[0]);
    }
}
