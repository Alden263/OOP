import java.util.ArrayList;

/**
 * Danh sách seri của TẤT CẢ các máy (còn trong kho và đã bán), lưu riêng trong file SeriSanPham.txt.
 * Dùng ArrayList (mảng động) vì số máy tăng khi nhập hàng.
 *
 * Quy tắc: số máy TRONG_KHO của một sản phẩm luôn bằng số lượng tồn kho của sản phẩm đó
 * (được kiểm tra mỗi khi mở dữ liệu, xem DuLieuCuaHang.kiemTraSoLuong).
 */
public class DanhSachSeri implements ghiDocFile {
    private ArrayList<SeriSanPham> ds;

    public DanhSachSeri() {
        ds = new ArrayList<>();
    }

    /** Thêm một máy vào danh sách. */
    public void them(SeriSanPham s) {
        ds.add(s);
    }

    /** Tìm một máy theo seri (không phân biệt hoa thường, cả máy đã bán). Không có thì trả về null. */
    public SeriSanPham tim(String maSeri) {
        for (SeriSanPham s : ds) {
            if (s.getMaSeri().equalsIgnoreCase(maSeri)) return s;
        }
        return null;
    }

    /** Tất cả máy (còn trong kho và đã bán) của một sản phẩm. */
    public ArrayList<SeriSanPham> theoSanPham(String maSP) {
        ArrayList<SeriSanPham> kq = new ArrayList<>();
        for (SeriSanPham s : ds) {
            if (s.getMaSP().equalsIgnoreCase(maSP)) kq.add(s);
        }
        return kq;
    }

    /** Các máy CÒN TRONG KHO của một sản phẩm. */
    public ArrayList<SeriSanPham> trongKho(String maSP) {
        ArrayList<SeriSanPham> kq = new ArrayList<>();
        for (SeriSanPham s : theoSanPham(maSP)) {
            if (s.conTrongKho()) kq.add(s);
        }
        return kq;
    }

    /** Số máy còn trong kho của một sản phẩm. */
    public int demTrongKho(String maSP) {
        return trongKho(maSP).size();
    }

    /** Tổng số máy trong danh sách. */
    public int soLuong() {
        return ds.size();
    }

    /** Bản sao danh sách (bên ngoài sửa bản sao không ảnh hưởng danh sách gốc). */
    public ArrayList<SeriSanPham> layDanhSach() {
        return new ArrayList<>(ds);
    }

    /** Ghi tất cả máy ra file, mỗi máy một dòng. */
    @Override
    public void ghiFile(String duongDan) {
        String noiDung = "";
        for (SeriSanPham s : ds) {
            noiDung += s.toFileString() + "\n";
        }
        XuLyFile.ghiFile(duongDan, noiDung);
    }

    /** Đọc danh sách máy từ file. Dòng sai định dạng / seri trùng thì báo lỗi để không mở nhầm dữ liệu hỏng. */
    @Override
    public void docFile(String duongDan) {
        ds.clear();
        String noiDung = XuLyFile.docFile(duongDan);
        for (String dong : noiDung.split("\n")) {
            if (dong.trim().isEmpty()) continue;
            String[] tt = dong.split(";", -1);   // -1: giữ cột trống ở cuối (máy chưa bán chưa có mã hóa đơn)
            if (tt.length != 5) throw new IllegalArgumentException("Sai số cột trong file seri: " + dong);
            if (!SeriSanPham.hopLe(tt[0])) throw new IllegalArgumentException("Seri không hợp lệ: " + dong);
            if (!tt[2].equals(SeriSanPham.TRONG_KHO) && !tt[2].equals(SeriSanPham.DA_BAN)) {
                throw new IllegalArgumentException("Trạng thái seri không hợp lệ: " + dong);
            }
            if (tim(tt[0]) != null) throw new IllegalArgumentException("Trùng seri: " + dong);
            ds.add(new SeriSanPham(tt[0], tt[1], tt[2], tt[3], tt[4]));
        }
    }
}
