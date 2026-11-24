import java.util.ArrayList;
import java.util.Locale;

/**
 * In dữ liệu ra màn hình dạng BẢNG có kẻ khung. Ví dụ:
 *
 *   Bang b = new Bang("Mã SP", "Tên sản phẩm", "Tồn");
 *   b.themDong("LT001", "Dell Inspiron", 9);
 *   b.in("DANH SÁCH SẢN PHẨM");
 *
 *   ===== DANH SÁCH SẢN PHẨM =====
 *   +-------+---------------+-----+
 *   | Mã SP | Tên sản phẩm  | Tồn |
 *   +-------+---------------+-----+
 *   | LT001 | Dell Inspiron | 9   |
 *   +-------+---------------+-----+
 *
 * Độ rộng mỗi cột tự tính theo nội dung dài nhất của cột đó.
 */
public class Bang {
    private String[] tieuDe;                         // dòng tiêu đề các cột
    private ArrayList<String[]> dsDong = new ArrayList<>();   // các dòng dữ liệu

    public Bang(String... tieuDe) {
        this.tieuDe = tieuDe;
    }

    /** Thêm một dòng; mỗi giá trị là một ô (số, chữ... đều được, tự đổi sang chữ). */
    public void themDong(Object... oCot) {
        String[] dong = new String[tieuDe.length];
        for (int i = 0; i < tieuDe.length; i++) {
            dong[i] = (i < oCot.length && oCot[i] != null) ? String.valueOf(oCot[i]) : "";
        }
        dsDong.add(dong);
    }

    /** Số dòng dữ liệu đã thêm. */
    public int soDong() {
        return dsDong.size();
    }

    /** In bảng kèm tên bảng ở trên. Bảng không có dòng nào thì in "(không có dữ liệu)". */
    public void in(String tenBang) {
        System.out.println("\n===== " + tenBang + " =====");
        // 1. Tính độ rộng từng cột = độ dài nội dung dài nhất trong cột (kể cả tiêu đề)
        int[] rong = new int[tieuDe.length];
        for (int i = 0; i < tieuDe.length; i++) rong[i] = tieuDe[i].length();
        for (String[] dong : dsDong) {
            for (int i = 0; i < dong.length; i++) rong[i] = Math.max(rong[i], dong[i].length());
        }
        // 2. In khung trên, tiêu đề, khung giữa, các dòng, khung dưới
        String vien = duongKe(rong);
        System.out.println(vien);
        System.out.println(dongChu(tieuDe, rong));
        System.out.println(vien);
        if (dsDong.isEmpty()) {
            System.out.println("(không có dữ liệu)");
        } else {
            for (String[] dong : dsDong) System.out.println(dongChu(dong, rong));
        }
        System.out.println(vien);
    }

    /** Đường kẻ ngang, ví dụ: +-------+-----+ */
    private static String duongKe(int[] rong) {
        String s = "+";
        for (int r : rong) s += "-".repeat(r + 2) + "+";
        return s;
    }

    /** Một dòng chữ, mỗi ô được đệm khoảng trắng cho đủ độ rộng cột, ví dụ: | LT001 | 9   | */
    private static String dongChu(String[] o, int[] rong) {
        String s = "|";
        for (int i = 0; i < rong.length; i++) {
            s += " " + o[i] + " ".repeat(rong[i] - o[i].length()) + " |";
        }
        return s;
    }

    /** Định dạng tiền có dấu chấm ngăn cách hàng nghìn, ví dụ 14990000 -> "14.990.000". */
    public static String tien(double soTien) {
        return String.format(Locale.ROOT, "%,.0f", soTien).replace(',', '.');
    }
}
