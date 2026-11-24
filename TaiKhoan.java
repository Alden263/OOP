import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Tài khoản đăng nhập của một nhân viên: tên đăng nhập + mật khẩu + quyền.
 *
 * Mật khẩu KHÔNG được lưu dạng chữ thường. Ta lưu bản "băm" SHA-256 (chuỗi 64 ký tự),
 * nên có mở file NhanVien.txt cũng không đọc ra được mật khẩu.
 * Khi đăng nhập, ta băm mật khẩu người dùng vừa gõ rồi so sánh với bản băm đã lưu.
 */
public class TaiKhoan {
    /** Mật khẩu mặc định khi cấp tài khoản mới hoặc đặt lại mật khẩu. */
    public static final String MAT_KHAU_MAC_DINH = "123";

    private String tenDangNhap;
    private String matKhauBam;   // mật khẩu đã băm SHA-256, dạng chuỗi hex 64 ký tự
    private QuyenHan quyenHan;

    /** Tạo tài khoản mới. matKhau là mật khẩu chữ thường, hàm sẽ tự băm trước khi lưu. */
    public TaiKhoan(String tenDangNhap, String matKhau, QuyenHan quyenHan) {
        setTenDangNhap(tenDangNhap);
        doiMatKhau(matKhau);
        setQuyenHan(quyenHan);
    }

    /** Dựng lại tài khoản từ file: mật khẩu trong file đã là bản băm nên giữ nguyên, không băm lần nữa. */
    public static TaiKhoan tuFile(String tenDangNhap, String matKhauBam, QuyenHan quyenHan) {
        if (matKhauBam == null || !matKhauBam.matches("[0-9a-f]{64}")) {
            throw new IllegalArgumentException("Mật khẩu lưu trong file nhân viên không hợp lệ.");
        }
        TaiKhoan tk = new TaiKhoan(tenDangNhap, MAT_KHAU_MAC_DINH, quyenHan); // tạo tạm...
        tk.matKhauBam = matKhauBam;                                            // ...rồi thay bằng bản băm trong file
        return tk;
    }

    /** Tên đăng nhập hợp lệ: 3-20 ký tự, chỉ gồm chữ không dấu, số, dấu gạch dưới (_) và dấu chấm (.). */
    public static boolean hopLeTenDangNhap(String ten) {
        return ten != null && ten.matches("[A-Za-z0-9_.]{3,20}");
    }

    /** Băm mật khẩu bằng SHA-256 rồi đổi thành chuỗi hex 64 ký tự. */
    private static String bam(String matKhau) {
        try {
            byte[] kq = MessageDigest.getInstance("SHA-256").digest(matKhau.getBytes(StandardCharsets.UTF_8));
            return String.format("%064x", new BigInteger(1, kq)); // %064x: hex, đệm số 0 cho đủ 64 ký tự
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Máy không hỗ trợ SHA-256", e);
        }
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        if (!hopLeTenDangNhap(tenDangNhap)) {
            throw new IllegalArgumentException("Tên đăng nhập gồm 3-20 ký tự: chữ không dấu, số, _ hoặc .");
        }
        this.tenDangNhap = tenDangNhap;
    }

    /** Bản băm của mật khẩu (dùng khi ghi file). Không phải mật khẩu gốc. */
    public String getMatKhauBam() {
        return matKhauBam;
    }

    /** Đổi mật khẩu. matKhauMoi là chữ thường, hàm tự băm rồi lưu. */
    public void doiMatKhau(String matKhauMoi) {
        if (matKhauMoi == null || matKhauMoi.length() < 3) {
            throw new IllegalArgumentException("Mật khẩu tối thiểu 3 ký tự.");
        }
        this.matKhauBam = bam(matKhauMoi);
    }

    /** Mật khẩu người dùng vừa gõ có đúng không? */
    public boolean kiemTraMatKhau(String matKhau) {
        return matKhau != null && matKhauBam.equals(bam(matKhau));
    }

    public QuyenHan getQuyenHan() {
        return quyenHan;
    }

    public void setQuyenHan(QuyenHan quyenHan) {
        if (quyenHan == null) {
            throw new IllegalArgumentException("Tài khoản phải có quyền.");
        }
        this.quyenHan = quyenHan;
    }

    /** Đăng nhập đúng khi tên đăng nhập khớp (không phân biệt hoa thường) và mật khẩu đúng. */
    public boolean dangNhap(String tenDangNhap, String matKhau) {
        return this.tenDangNhap.equalsIgnoreCase(tenDangNhap) && kiemTraMatKhau(matKhau);
    }
}
