/**
 * Một MÁY cụ thể của một sản phẩm, nhận diện bằng số seri riêng.
 * Ví dụ: laptop Dell LT001 có 9 máy thì có 9 đối tượng SeriSanPham, mỗi máy một seri.
 *
 * Lưu trong file SeriSanPham.txt, mỗi dòng một máy, các cột cách nhau bằng dấu ";":
 *   maSeri ; maSP ; trangThai ; maPhieuNhap ; maHoaDon
 * Ví dụ:
 *   SN-DELL3520-0001;LT001;TRONG_KHO;PN002;        (máy còn trong kho, nhập từ phiếu PN002)
 *   SN-DELL3520-0009;LT001;DA_BAN;PN002;HD007      (máy đã bán ở hóa đơn HD007)
 */
public class SeriSanPham {
    // Hai trạng thái của một máy
    public static final String TRONG_KHO = "TRONG_KHO";
    public static final String DA_BAN = "DA_BAN";

    private String maSeri;       // số seri in trên máy
    private String maSP;         // máy này thuộc sản phẩm nào
    private String trangThai;    // TRONG_KHO hoặc DA_BAN
    private String maPhieuNhap;  // nhập từ phiếu nào ("" nếu không rõ)
    private String maHoaDon;     // bán ở hóa đơn nào ("" nếu chưa bán)

    public SeriSanPham() {
        this("", "", TRONG_KHO, "", "");
    }

    /** Máy vừa nhập kho: trạng thái TRONG_KHO, chưa có hóa đơn. */
    public SeriSanPham(String maSeri, String maSP, String maPhieuNhap) {
        this(maSeri, maSP, TRONG_KHO, maPhieuNhap, "");
    }

    /** Đầy đủ thông tin (dùng khi đọc từ file). */
    public SeriSanPham(String maSeri, String maSP, String trangThai, String maPhieuNhap, String maHoaDon) {
        this.maSeri = maSeri;
        this.maSP = maSP;
        this.trangThai = trangThai;
        this.maPhieuNhap = maPhieuNhap;
        this.maHoaDon = maHoaDon;
    }

    /**
     * Seri hợp lệ: 3-30 ký tự, chỉ gồm chữ không dấu, số, dấu gạch ngang (-), gạch dưới (_), dấu chấm (.).
     * Không cho dấu chấm phẩy, khoảng trắng vì ";" là ký tự phân cách trong file.
     */
    public static boolean hopLe(String seri) {
        return seri != null && seri.matches("[A-Za-z0-9_.-]{3,30}");
    }

    /** Máy còn trong kho không? */
    public boolean conTrongKho() {
        return TRONG_KHO.equals(trangThai);
    }

    /** Bán máy này ở hóa đơn maHD: chuyển sang trạng thái DA_BAN. */
    public void ban(String maHD) {
        this.trangThai = DA_BAN;
        this.maHoaDon = maHD;
    }

    /** Tên trạng thái để in ra màn hình. */
    public String tenTrangThai() {
        return conTrongKho() ? "Trong kho" : "Đã bán";
    }

    /** Một dòng để ghi file. */
    public String toFileString() {
        return maSeri + ";" + maSP + ";" + trangThai + ";" + maPhieuNhap + ";" + maHoaDon;
    }

    public String getMaSeri() { return maSeri; }
    public void setMaSeri(String maSeri) { this.maSeri = maSeri; }
    public String getMaSP() { return maSP; }
    public void setMaSP(String maSP) { this.maSP = maSP; }
    public String getTrangThai() { return trangThai; }
    public void setTrangThai(String trangThai) { this.trangThai = trangThai; }
    public String getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(String maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }
    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }
}
