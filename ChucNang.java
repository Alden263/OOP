/**
 * Các chức năng trong menu chính của chương trình.
 * Dùng để trả lời câu hỏi: "quyền này được làm gì ở chức năng này?"
 * (xem bảng phân quyền trong QuyenHan.mucQuyen).
 */
public enum ChucNang {
    BAN_HANG,       // Bán hàng tại quầy (lập hóa đơn)
    SAN_PHAM,       // Quản lý sản phẩm
    KHACH_HANG,     // Quản lý khách hàng
    HOA_DON,        // Xem hóa đơn đã bán
    NHAP_HANG,      // Lập / xem phiếu nhập
    NHA_CUNG_CAP,   // Quản lý nhà cung cấp
    NHAN_VIEN,      // Quản lý nhân viên
    TAI_KHOAN       // Quản lý tài khoản đăng nhập (chỉ Admin)
}
