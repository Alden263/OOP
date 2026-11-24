/**
 * [UX] Quy tắc TÍCH ĐIỂM khách hàng (chỉ có hàm static, chỉ ĐỌC dữ liệu, không sửa gì).
 *
 *   - Tích điểm: mỗi hóa đơn BÁN LẺ của khách có hồ sơ, cứ 100.000 đ thanh toán được 1 điểm (làm tròn xuống).
 *   - Dùng điểm: 1 điểm = giảm 1.000 đ, tối đa 20% tiền hàng (khớp với HoaDonBanLe.tinhGiamGia).
 *   - Khách lẻ (mã KHLE) và hóa đơn BÁN SỈ không tích điểm (bán sỉ đã có chiết khấu riêng).
 *
 * Điểm của khách KHÔNG lưu thành một cột riêng mà được TÍNH LẠI từ các hóa đơn:
 *   điểm hiện có = (tổng điểm kiếm được ở mọi hóa đơn) - (tổng điểm đã dùng ở mọi hóa đơn).
 * Nhờ vậy điểm luôn khớp với hóa đơn, không cần đổi định dạng file và không thể bị lệch.
 * (Cột "điểm tích lũy sử dụng" của hóa đơn bán lẻ đã có sẵn trong file HoaDon.txt.)
 */
public class DiemTichLuy {
    /** Mã khách lẻ chung: không tích điểm. */
    public static final String KHACH_LE = "KHLE";
    /** Mua mỗi 100.000 đ được 1 điểm. */
    public static final double TIEN_MOI_DIEM_TICH = 100000;
    /** Mỗi điểm khi dùng giảm 1.000 đ (khớp HoaDonBanLe). */
    public static final double TIEN_MOI_DIEM_DUNG = 1000;
    /** Dùng điểm giảm tối đa 20% tiền hàng (khớp HoaDonBanLe). */
    public static final double TI_LE_GIAM_TOI_DA = 0.20;

    /** Chỉ có hàm static nên không cho tạo đối tượng. */
    private DiemTichLuy() {
    }

    /** Khách này có được tích điểm không? (khách có hồ sơ, không phải khách lẻ chung). */
    public static boolean coTichDiem(String maKH) {
        return maKH != null && !maKH.equalsIgnoreCase(KHACH_LE);
    }

    /** Số điểm hóa đơn này TÍCH cho khách. Chỉ hóa đơn bán lẻ của khách có hồ sơ mới có điểm. */
    public static int diemKiemDuoc(HoaDon hd) {
        if (!(hd instanceof HoaDonBanLe)) return 0;           // bán sỉ không tích điểm
        if (!coTichDiem(hd.getMaKH())) return 0;              // khách lẻ không tích điểm
        return (int) (hd.getTongTien() / TIEN_MOI_DIEM_TICH); // làm tròn xuống
    }

    /** Số điểm hóa đơn này đã DÙNG để giảm giá. */
    public static int diemDaDung(HoaDon hd) {
        if (hd instanceof HoaDonBanLe) return ((HoaDonBanLe) hd).getDiemTichLuySuDung();
        return 0;
    }

    /** Điểm khách đang có = tổng điểm kiếm được - tổng điểm đã dùng, tính trên mọi hóa đơn của khách. */
    public static int diemHienCo(String maKH, DanhSachHoaDon dsHoaDon) {
        if (!coTichDiem(maKH)) return 0;
        int diem = 0;
        for (HoaDon hd : dsHoaDon.getDs()) {
            if (hd.getMaKH().equalsIgnoreCase(maKH)) diem += diemKiemDuoc(hd) - diemDaDung(hd);
        }
        return Math.max(0, diem);
    }

    /** Số điểm TỐI ĐA được dùng cho hóa đơn hd: không quá điểm khách có và không quá 20% tiền hàng. */
    public static int diemToiDaDung(HoaDon hd, int diemKhachCo) {
        int theoHoaDon = (int) (hd.tinhTongTienGoc() * TI_LE_GIAM_TOI_DA / TIEN_MOI_DIEM_DUNG);
        return Math.min(diemKhachCo, theoHoaDon);
    }
}
