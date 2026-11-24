import java.util.List;

/**
 * Các màn hình hiển thị dạng BẢNG của chương trình (vẽ khung bằng lớp Bang).
 * Lớp này CHỈ ĐỌC dữ liệu để in ra, không thay đổi dữ liệu.
 * Tất cả hàm đều là static: gọi thẳng GiaoDien.sanPham(...), không cần tạo đối tượng.
 */
public class GiaoDien {

    /** Lớp tiện ích chỉ có hàm static nên không cho tạo đối tượng. */
    private GiaoDien() {
    }

    // ===================== HÀM PHỤ: tra tên theo mã =====================

    private static String tenKhach(String maKH, DuLieuCuaHang dl) {
        KhachHang kh = dl.khachHang.timTheoMa(maKH);
        return kh == null ? maKH : maKH + " - " + kh.getHoTen();
    }

    private static String tenNhanVien(String maNV, DuLieuCuaHang dl) {
        NhanVien nv = dl.nhanVien.timKiemTheoMa(maNV);
        return nv == null ? maNV : maNV + " - " + nv.getHoTen();
    }

    private static String tenSanPham(String maSP, DuLieuCuaHang dl) {
        SanPham sp = dl.sanPham.searchSanPham(maSP);
        return sp == null ? maSP : sp.getTenSP();
    }

    private static String tenNCC(String maNCC, DuLieuCuaHang dl) {
        NhaCungCap ncc = dl.nhaCungCap.timTheoMa(maNCC);
        return ncc == null ? maNCC : maNCC + " - " + ncc.getTenNCC();
    }

    // ===================== SẢN PHẨM =====================

    /** Tên loại sản phẩm để in. */
    public static String loai(SanPham sp) {
        if (sp instanceof Laptop) return "Laptop";
        if (sp instanceof PC) return "PC";
        return "Linh kiện";
    }

    /** Cấu hình tóm tắt của sản phẩm (mỗi loại có thông tin riêng). */
    public static String cauHinh(SanPham sp) {
        if (sp instanceof Laptop) {
            Laptop lt = (Laptop) sp;
            return lt.getCPU() + ", RAM " + lt.getRAM() + "GB, SSD " + lt.getDungLuongSSD() + "GB";
        }
        if (sp instanceof PC) {
            PC pc = (PC) sp;
            return pc.getCPU() + ", " + pc.getGPU() + ", RAM " + pc.getRAM() + "GB, SSD " + pc.getDungLuongSSD() + "GB";
        }
        LinhKien lk = (LinhKien) sp;
        return lk.getLoaiLinhKien() + ", hãng " + lk.getHangSanXuat();
    }

    /** Bảng danh sách sản phẩm. */
    public static void sanPham(String tenBang, List<SanPham> ds) {
        Bang b = new Bang("STT", "Mã SP", "Loại", "Tên sản phẩm", "Cấu hình", "Giá bán", "Tồn");
        int stt = 1;
        for (SanPham sp : ds) {
            b.themDong(stt++, sp.getMaSP(), loai(sp), sp.getTenSP(), cauHinh(sp),
                    Bang.tien(sp.tinhGiaBan()), sp.getSoLuong());
        }
        b.in(tenBang);
    }

    /**
     * [UX] Bảng sản phẩm GỌN dùng ở màn bán hàng: chỉ Mã, Tên, Giá, Tồn.
     * Sản phẩm tồn 0 bị ẨN vì không bán được. (Bảng đầy đủ có cấu hình xem ở menu Sản phẩm.)
     */
    public static void sanPhamDeBan(List<SanPham> ds) {
        Bang b = new Bang("Mã SP", "Tên sản phẩm", "Giá bán", "Tồn");
        for (SanPham sp : ds) {
            if (sp.getSoLuong() > 0) b.themDong(sp.getMaSP(), sp.getTenSP(), Bang.tien(sp.tinhGiaBan()), sp.getSoLuong());
        }
        b.in("SẢN PHẨM ĐANG BÁN");
    }

    /** Bảng tồn kho. */
    public static void tonKho(List<SanPham> ds) {
        Bang b = new Bang("Mã SP", "Tên sản phẩm", "Tồn kho", "Ghi chú");
        for (SanPham sp : ds) {
            b.themDong(sp.getMaSP(), sp.getTenSP(), sp.getSoLuong(), sp.getSoLuong() == 0 ? "Hết hàng" : "");
        }
        b.in("TỒN KHO");
    }

    // ===================== SERI =====================

    /** Bảng seri các máy: còn trong kho hay đã bán, nhập từ phiếu nào, bán ở hóa đơn nào. */
    public static void seri(String tenBang, List<SeriSanPham> ds) {
        Bang b = new Bang("STT", "Seri", "Mã SP", "Trạng thái", "Phiếu nhập", "Hóa đơn bán");
        int stt = 1;
        for (SeriSanPham s : ds) {
            b.themDong(stt++, s.getMaSeri(), s.getMaSP(), s.tenTrangThai(), s.getMaPhieuNhap(), s.getMaHoaDon());
        }
        b.in(tenBang);
    }

    /** Tra cứu một máy theo seri (bảo hành): thuộc sản phẩm nào, nhập từ đâu, bán cho ai, ngày nào. */
    public static void traCuuSeri(SeriSanPham s, DuLieuCuaHang dl) {
        String ncc = "", khach = "", ngayBan = "";
        PhieuNhap ph = dl.phieuNhap.timTheoMa(s.getMaPhieuNhap());
        if (ph != null) ncc = tenNCC(ph.getMaNCC(), dl);
        HoaDon hd = dl.hoaDon.timHoaDon(s.getMaHoaDon());
        if (hd != null) {
            khach = tenKhach(hd.getMaKH(), dl);
            ngayBan = hd.getNgayLap();
        }
        Bang b = new Bang("Seri", "Sản phẩm", "Trạng thái", "Phiếu nhập", "Nhà cung cấp", "Hóa đơn", "Khách hàng", "Ngày bán");
        b.themDong(s.getMaSeri(), s.getMaSP() + " - " + tenSanPham(s.getMaSP(), dl), s.tenTrangThai(),
                s.getMaPhieuNhap(), ncc, s.getMaHoaDon(), khach, ngayBan);
        b.in("TRA CỨU SERI " + s.getMaSeri());
    }

    // ===================== KHÁCH HÀNG / NHÂN VIÊN / TÀI KHOẢN / NHÀ CUNG CẤP =====================

    /** Bảng khách hàng, có cột Điểm tích lũy (tính từ các hóa đơn, xem lớp DiemTichLuy). Khách lẻ chung hiện "-". */
    public static void khachHang(String tenBang, KhachHang[] ds, DuLieuCuaHang dl) {
        Bang b = new Bang("STT", "Mã KH", "Họ tên", "Số điện thoại", "Địa chỉ", "Điểm");
        int stt = 1;
        for (KhachHang kh : ds) {
            String diem = DiemTichLuy.coTichDiem(kh.getMa()) ? String.valueOf(DiemTichLuy.diemHienCo(kh.getMa(), dl.hoaDon)) : "-";
            b.themDong(stt++, kh.getMa(), kh.getHoTen(), kh.getSoDienThoai(), kh.getDiaChi(), diem);
        }
        b.in(tenBang);
    }

    public static void nhanVien(String tenBang, NhanVien[] ds) {
        Bang b = new Bang("STT", "Mã NV", "Họ tên", "Số điện thoại", "Chức vụ", "Lương", "Tài khoản");
        int stt = 1;
        for (NhanVien nv : ds) {
            String tk = nv.getTaiKhoan() == null ? "(chưa có)" : nv.getTaiKhoan().getTenDangNhap();
            b.themDong(stt++, nv.getMa(), nv.getHoTen(), nv.getSoDienThoai(), nv.getChucVu(), Bang.tien(nv.getLuong()), tk);
        }
        b.in(tenBang);
    }

    public static void taiKhoan(NhanVien[] ds) {
        Bang b = new Bang("Mã NV", "Họ tên", "Tên đăng nhập", "Quyền");
        for (NhanVien nv : ds) {
            TaiKhoan tk = nv.getTaiKhoan();
            if (tk == null) b.themDong(nv.getMa(), nv.getHoTen(), "(chưa có tài khoản)", "");
            else b.themDong(nv.getMa(), nv.getHoTen(), tk.getTenDangNhap(), tk.getQuyenHan().getTenQuyen());
        }
        b.in("DANH SÁCH TÀI KHOẢN");
    }

    public static void nhaCungCap(String tenBang, NhaCungCap[] ds) {
        Bang b = new Bang("STT", "Mã NCC", "Tên nhà cung cấp", "Địa chỉ", "Số điện thoại");
        int stt = 1;
        for (NhaCungCap ncc : ds) b.themDong(stt++, ncc.getMaNCC(), ncc.getTenNCC(), ncc.getDiaChi(), ncc.getSoDienThoai());
        b.in(tenBang);
    }

    // ===================== HÓA ĐƠN =====================

    /**
     * [UX] Tên loại hóa đơn ("Bán lẻ" / "Bán sỉ"). ĐA HÌNH: gọi getLoaiHoaDon() trên biến kiểu HoaDon,
     * mỗi lớp con (HoaDonBanLe, HoaDonBanSi) tự trả về tên của mình, ở đây không cần biết đang là loại nào.
     */
    public static String loaiHoaDon(HoaDon hd) {
        return hd.getLoaiHoaDon();
    }

    /** [UX] Phần trăm gọn: 10.0 -> "10%", 12.5 -> "12.5%". */
    public static String phanTram(double pt) {
        if (pt == Math.floor(pt)) return (long) pt + "%";
        return pt + "%";
    }

    /** [UX] In 3 dòng tiền ở cuối hóa đơn: tổng tiền hàng, giảm giá, thanh toán (giảm giá do từng loại hóa đơn tự tính). */
    private static void tongTien(HoaDon hd) {
        System.out.println("Tổng tiền hàng : " + Bang.tien(hd.tinhTongTienGoc()) + " đ");
        System.out.println("Giảm giá       : " + Bang.tien(hd.tinhGiamGia()) + " đ");
        System.out.println("THANH TOÁN     : " + Bang.tien(hd.getTongTien()) + " đ");
    }

    /**
     * [UX] Giỏ hàng của hóa đơn TẠM: bảng các mặt hàng đang chọn + tổng tiền.
     * Tự in sau mỗi lần Thêm / Đổi số lượng / Bỏ sản phẩm để người bán luôn thấy hóa đơn đang thế nào.
     */
    public static void gioHang(HoaDon hd, DuLieuCuaHang dl) {
        Bang b = new Bang("STT", "Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền");
        int stt = 1;
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            b.themDong(stt++, ct.getMaSP(), tenSanPham(ct.getMaSP(), dl), ct.getSoLuong(),
                    Bang.tien(ct.getDonGia()), Bang.tien(ct.tinhThanhTien()));
        }
        b.in("GIỎ HÀNG - HÓA ĐƠN TẠM " + hd.getMaHD() + " (" + loaiHoaDon(hd) + ")");
        if (hd.getSoLuongChiTiet() == 0) return;   // giỏ trống thì không in tiền
        System.out.println("Tổng tiền hàng : " + Bang.tien(hd.tinhTongTienGoc()) + " đ");
        if (hd.tinhGiamGia() > 0) {   // chỉ in giảm giá khi có (bán sỉ có chiết khấu)
            System.out.println("Giảm giá       : " + Bang.tien(hd.tinhGiamGia()) + " đ");
            System.out.println("THANH TOÁN     : " + Bang.tien(hd.getTongTien()) + " đ");
        }
    }

    /** Bảng danh sách hóa đơn (mỗi hóa đơn một dòng) kèm tổng tiền ở cuối. */
    public static void danhSachHoaDon(String tenBang, List<HoaDon> ds, DuLieuCuaHang dl) {
        Bang b = new Bang("STT", "Mã HĐ", "Ngày lập", "Loại", "Khách hàng", "Nhân viên", "Số mặt hàng", "Thanh toán");
        int stt = 1;
        double tong = 0;
        for (HoaDon hd : ds) {
            b.themDong(stt++, hd.getMaHD(), hd.getNgayLap(), loaiHoaDon(hd), tenKhach(hd.getMaKH(), dl),
                    tenNhanVien(hd.getMaNV(), dl), hd.getSoLuongChiTiet(), Bang.tien(hd.getTongTien()));
            tong += hd.getTongTien();
        }
        b.in(tenBang);
        System.out.println("Tổng cộng: " + ds.size() + " hóa đơn, " + Bang.tien(tong) + " đ");
    }

    /** In MỘT hóa đơn: thông tin chung + bảng các mặt hàng (không in seri; seri xem ở chức năng riêng). */
    public static void hoaDon(HoaDon hd, DuLieuCuaHang dl) {
        System.out.println("\n========== HÓA ĐƠN " + hd.getMaHD() + " (" + loaiHoaDon(hd) + ") ==========");
        System.out.println("Ngày lập   : " + hd.getNgayLap());
        System.out.println("Khách hàng : " + tenKhach(hd.getMaKH(), dl));
        System.out.println("Nhân viên  : " + tenNhanVien(hd.getMaNV(), dl));
        if (hd instanceof HoaDonBanSi) {   // [UX] bán sỉ: in thêm công ty và % chiết khấu
            HoaDonBanSi si = (HoaDonBanSi) hd;
            System.out.println("Công ty    : " + si.getTenCongTy());
            System.out.println("Chiết khấu : " + phanTram(si.getPhanTramChietKhau()));
        }
        Bang b = new Bang("STT", "Mã SP", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền");
        int stt = 1;
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
            b.themDong(stt++, ct.getMaSP(), tenSanPham(ct.getMaSP(), dl), ct.getSoLuong(),
                    Bang.tien(ct.getDonGia()), Bang.tien(ct.tinhThanhTien()));
        }
        b.in("CHI TIẾT HÓA ĐƠN " + hd.getMaHD());
        tongTien(hd);
        // [UX] Tích điểm (chỉ bán lẻ của khách có hồ sơ): điểm đã dùng để giảm giá và điểm được cộng thêm
        int dung = DiemTichLuy.diemDaDung(hd);
        if (dung > 0) System.out.println("Điểm đã dùng  : " + dung + " điểm (giảm " + Bang.tien(dung * DiemTichLuy.TIEN_MOI_DIEM_DUNG) + " đ)");
        int cong = DiemTichLuy.diemKiemDuoc(hd);
        if (cong > 0) System.out.println("Điểm được cộng: +" + cong + " điểm");
    }

    // ===================== PHIẾU NHẬP =====================

    /** Bảng danh sách phiếu nhập (mỗi phiếu một dòng). */
    public static void danhSachPhieuNhap(PhieuNhap[] ds, DuLieuCuaHang dl) {
        Bang b = new Bang("STT", "Mã PN", "Ngày nhập", "Nhà cung cấp", "Nhân viên", "Số mặt hàng", "Tổng tiền");
        int stt = 1;
        for (PhieuNhap ph : ds) {
            b.themDong(stt++, ph.getMaPN(), ph.getNgayNhap(), tenNCC(ph.getMaNCC(), dl), tenNhanVien(ph.getMaNV(), dl),
                    ph.getDsChiTiet().size(), Bang.tien(ph.tinhTongTien()));
        }
        b.in("DANH SÁCH PHIẾU NHẬP");
    }

    /** In MỘT phiếu nhập: thông tin chung + bảng các mặt hàng nhập. */
    public static void phieuNhap(PhieuNhap ph, DuLieuCuaHang dl) {
        System.out.println("\n========== PHIẾU NHẬP " + ph.getMaPN() + " ==========");
        System.out.println("Ngày nhập    : " + ph.getNgayNhap());
        System.out.println("Nhà cung cấp : " + tenNCC(ph.getMaNCC(), dl));
        System.out.println("Nhân viên    : " + tenNhanVien(ph.getMaNV(), dl));
        Bang b = new Bang("STT", "Mã SP", "Tên sản phẩm", "SL", "Giá nhập", "Thành tiền");
        int stt = 1;
        for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
            NhapLieu nl = ct.getNhapLieu();
            b.themDong(stt++, nl.getMaSP(), tenSanPham(nl.getMaSP(), dl), nl.getSoLuongNhap(),
                    Bang.tien(nl.getGiaNhap()), Bang.tien(nl.tinhThanhTien()));
        }
        b.in("CHI TIẾT PHIẾU NHẬP " + ph.getMaPN());
        System.out.println("TỔNG TIỀN : " + Bang.tien(ph.tinhTongTien()) + " đ");
    }
}
