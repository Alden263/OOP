import java.util.Scanner;

/**
 * Quy định các thao tác chung cho danh sách sản phẩm, khách hàng,
 * nhân viên và nhà cung cấp. Các lớp dùng implements IQuanLy
 * phải viết phần xử lý cho tất cả phương thức bên dưới.
 *
 * Không áp dụng thao tác sửa/xóa tùy ý cho hóa đơn hoặc phiếu nhập đã xác nhận.
 * Scanner được truyền từ QuanLyCuaHang: không đóng Scanner trong lớp thực hiện.
 */
public interface IQuanLy {
    /** Nhập thông tin bằng Scanner dùng chung rồi thêm đối tượng vào danh sách. */
    void them(Scanner sc);

    /** Tìm đối tượng theo mã, sau đó dùng Scanner để nhập thông tin cần sửa. */
    void sua(String ma, Scanner sc);

    /** Trả về true nếu xóa thành công; false nếu không tìm thấy hoặc không được xóa. */
    boolean xoa(String ma);

    /**
     * Nhập điều kiện tìm kiếm và hiển thị kết quả cho người dùng.
     * Không thay thế phương thức timTheoMa trả về đối tượng để xử lý nghiệp vụ.
     */
    void timKiem(Scanner sc);

    /** Hiển thị các đối tượng hiện có trong danh sách. */
    void hienThi();
}
