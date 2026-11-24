/**
 * Hàm tiện ích về MÃ (chỉ có hàm static): dùng khi tự sinh mã mới, ví dụ NCC012 -> NCC013.
 * (Trước đây hàm này nằm chung với các hàm nhập liệu từ bàn phím trong lớp NhapTuBanPhim;
 * các hàm nhập đó không còn dùng nên đã bỏ, chỉ giữ lại hàm này.)
 */
public class TienIchMa {

    /** Chỉ có hàm static nên không cho tạo đối tượng. */
    private TienIchMa() {
    }

    // lay phan so o cuoi ma, vd soCuoi("NCC012", "NCC") = 12. Sai dinh dang thi tra 0
    public static int soCuoi(String ma, String tienTo) {
        if (ma == null || !ma.startsWith(tienTo)) {
            return 0;
        }
        try {
            return Integer.parseInt(ma.substring(tienTo.length()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
