import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/** Điểm bắt đầu chương trình. Các lớp trong dự án không khai báo package. */
public class Main {
    public static void main(String[] args) {
        batTiengViet();
        QuanLyCuaHang cuaHang = new QuanLyCuaHang("CỬA HÀNG MÁY TÍNH");
        cuaHang.chay();
    }

    /** Chuyển console sang UTF-8 để in và nhập được tiếng Việt có dấu (nhất là trên Windows). */
    static void batTiengViet() {
        if (System.getProperty("os.name", "").toLowerCase().contains("win")) {
            try {
                // Đổi code page của cửa sổ console hiện tại sang UTF-8 (65001).
                new ProcessBuilder("cmd", "/c", "chcp 65001 > nul").inheritIO().start().waitFor();
            } catch (Exception e) {
                // Không đổi được thì vẫn chạy tiếp, chỉ có thể bị lỗi hiển thị dấu.
            }
        }
        System.setOut(new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8));
        System.setErr(new PrintStream(new FileOutputStream(FileDescriptor.err), true, StandardCharsets.UTF_8));
    }
}
