import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Điều phối menu 1–7; dùng chung danh sách và lưu sau mỗi thay đổi thành công. */
public class QuanLyCuaHang {
    private final String tenCuaHang;
    private final Scanner sc;
    private final Path thuMucDuLieu;
    private DuLieuCuaHang duLieu;
    private boolean dangChay;

    public QuanLyCuaHang(String tenCuaHang) {
        this(tenCuaHang, new Scanner(System.in));
    }
    public QuanLyCuaHang(String tenCuaHang, Scanner sc) {
        this(tenCuaHang, sc, Paths.get("data"));
    }
    public QuanLyCuaHang(String tenCuaHang, Scanner sc, Path thuMucDuLieu) {
        if (tenCuaHang == null || tenCuaHang.isBlank() || sc == null || thuMucDuLieu == null)
            throw new IllegalArgumentException("Tên cửa hàng, Scanner và đường dẫn không được rỗng.");
        this.tenCuaHang = tenCuaHang;
        this.sc = sc;
        this.thuMucDuLieu = thuMucDuLieu;
    }

    public void chay() {
        try {
            napDuLieu();
            // Lần đầu nhập các tệp cũ vào một phiên bản đồng bộ, không ghi đè tệp gốc.
            if (!Files.exists(thuMucDuLieu.resolve("CURRENT"))) duLieu.luu();
        } catch (Exception e) {
            System.out.println("Không thể mở dữ liệu. Chưa thay đổi tệp gốc: " + lyDo(e));
            return;
        }
        dangChay = true;
        System.out.println("\n===== " + tenCuaHang + " =====");
        System.out.println("Bán trực tiếp tại quầy. Mỗi giao dịch cần chọn nhân viên lập.");
        System.out.println("Dữ liệu: " + thuMucDuLieu.toAbsolutePath());
        while (dangChay) {
            try { menuChinh(); }
            catch (NoSuchElementException e) { dangChay = false; }
            catch (Exception e) { System.out.println("Không thể thực hiện: " + lyDo(e)); }
        }
        System.out.println("Đã thoát chương trình.");
    }

    private void napDuLieu() throws Exception {
        DuLieuCuaHang moi = new DuLieuCuaHang(sc, thuMucDuLieu);
        moi.doc();
        duLieu = moi; // Chỉ thay dữ liệu đang dùng khi đọc xong toàn bộ.
    }

    private void menuChinh() {
        switch (chonMenu("MENU CHÍNH", "Thoát", "Bán hàng tại quầy", "Quản lý sản phẩm",
                "Quản lý khách hàng", "Quản lý hóa đơn", "Nhập hàng",
                "Quản lý nhà cung cấp", "Quản lý nhân viên")) {
            case 1: banHang(); break;
            case 2: menuSanPham(); break;
            case 3: menuKhachHang(); break;
            case 4: menuHoaDon(); break;
            case 5: menuNhapHang(); break;
            case 6: menuNhaCungCap(); break;
            case 7: menuNhanVien(); break;
            case 0: dangChay = false; break;
            default: break;
        }
    }

    private void menuSanPham() {
        while (dangChay) {
            int chon = chonMenu("SẢN PHẨM", "Quay lại", "Thêm sản phẩm mới (tồn 0)",
                    "Sửa thông tin / giá bán", "Xóa sản phẩm chưa có giao dịch",
                    "Tìm theo mã / tên / loại", "Hiển thị danh sách", "Xem tồn kho");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> {
                int loai = soNguyen("Loại: 1 Laptop, 2 PC, 3 Linh kiện: ", 1, 3);
                String ma = maMoi(loai == 1 ? "LT" : loai == 2 ? "PC" : "LK");
                String ten = chuoi("Tên sản phẩm: ");
                double gia = soThuc("Giá bán: ", 0, 1e15);
                SanPham sp;
                if (loai == 1) sp = new Laptop(ma, ten, gia, 0, chuoi("CPU: "),
                        soNguyen("RAM (GB): ", 1, 100000), soNguyen("SSD (GB): ", 1, 10000000));
                else if (loai == 2) sp = new PC(ma, ten, gia, 0, chuoi("CPU: "), chuoi("GPU: "),
                        soNguyen("RAM (GB): ", 1, 100000), soNguyen("SSD (GB): ", 1, 10000000));
                else sp = new LinhKien(ma, ten, gia, 0, chuoi("Loại linh kiện: "), chuoi("Hãng sản xuất: "));
                duLieu.sanPham.themSanPham(sp);
                return true;
            });
            else if (chon == 2) thayDoi(() -> {
                SanPham sp = timSanPham();
                if (sp == null) return false;
                String ten = chuoi("Tên mới: ");
                double gia = soThuc("Giá bán mới: ", 0, 1e15);
                sp.setTenSP(ten); sp.setDonGia(gia);
                return true; // Không sửa mã/tồn kho trực tiếp.
            });
            else if (chon == 3) thayDoi(() -> {
                SanPham sp = timSanPham();
                if (sp == null) return false;
                if (coLichSu("SP", sp.getMaSP()) || sp.getSoLuong() != 0) {
                    System.out.println("Không xóa sản phẩm có giao dịch hoặc còn tồn kho."); return false;
                }
                return duLieu.sanPham.xoaSanPham(sp.getMaSP());
            });
            else if (chon == 4) {
                String tu = chuoi("Nhập mã / tên / Laptop / PC / LinhKien: ").toLowerCase(java.util.Locale.ROOT);
                boolean thay = false;
                for (SanPham sp : duLieu.sanPham.getDsSanPham()) {
                    if ((sp.getMaSP() + " " + sp.getTenSP() + " " + sp.getClass().getSimpleName())
                            .toLowerCase(java.util.Locale.ROOT).contains(tu)) { sp.hienthi(); thay = true; }
                }
                if (!thay) System.out.println("Không tìm thấy sản phẩm.");
            } else if (chon == 5) duLieu.sanPham.hienThiDanhSach();
            else for (SanPham sp : duLieu.sanPham.getDsSanPham())
                    System.out.println(sp.getMaSP() + " | " + sp.getTenSP() + " | Tồn: " + sp.getSoLuong());
        }
    }

    private void menuKhachHang() {
        while (dangChay) {
            int chon = chonMenu("KHÁCH HÀNG", "Quay lại", "Thêm khách", "Sửa khách",
                    "Xóa khách chưa có giao dịch", "Tìm theo mã / tên / số điện thoại", "Xem danh sách", "Lịch sử mua hàng");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> { themKhach(); return true; });
            else if (chon == 2) thayDoi(() -> {
                KhachHang kh = duLieu.khachHang.timTheoMa(chuoi("Mã khách: "));
                if (kh == null) { System.out.println("Không tìm thấy khách."); return false; }
                if (kh.getMa().equalsIgnoreCase("KHLE")) { System.out.println("Không sửa khách lẻ chung."); return false; }
                String ten = chuoi("Họ tên mới: "); String sdt = dienThoai(); String diaChi = chuoi("Địa chỉ mới: ");
                KhachHang trung = duLieu.khachHang.timTheoSoDienThoai(sdt);
                if (trung != null && trung != kh) throw new IllegalArgumentException("Số điện thoại đã có hồ sơ.");
                kh.setHoTen(ten); kh.setSoDienThoai(sdt); kh.setDiaChi(diaChi); return true;
            });
            else if (chon == 3) thayDoi(() -> {
                String ma = chuoi("Mã khách: ");
                if (ma.equalsIgnoreCase("KHLE") || coLichSu("KH", ma)) {
                    System.out.println("Không xóa khách lẻ chung hoặc khách đã mua hàng."); return false;
                }
                return duLieu.khachHang.xoaKhachHang(ma);
            });
            else if (chon == 4) {
                String tu = chuoi("Mã / tên / số điện thoại: ").toLowerCase(java.util.Locale.ROOT);
                boolean thay = false;
                for (KhachHang kh : duLieu.khachHang.layDanhSach())
                    if ((kh.getMa()+" "+kh.getHoTen()+" "+kh.getSoDienThoai()).toLowerCase(java.util.Locale.ROOT).contains(tu)) {
                        kh.hienThi(); thay = true;
                    }
                if (!thay) System.out.println("Không tìm thấy khách.");
            } else if (chon == 5) duLieu.khachHang.hienThi();
            else duLieu.hoaDon.timKiemTheoMaKH(chuoi("Mã khách: "));
        }
    }

    private KhachHang themKhach() {
        String ten = chuoi("Họ tên khách: "); String sdt = dienThoai();
        if (duLieu.khachHang.timTheoSoDienThoai(sdt) != null)
            throw new IllegalArgumentException("Số điện thoại đã có hồ sơ khách hàng.");
        KhachHang kh = new KhachHang(maMoi("KH"), ten, sdt, chuoi("Địa chỉ: "));
        duLieu.khachHang.themKhachHang(kh); System.out.println("Mã khách: " + kh.getMa()); return kh;
    }

    private void menuNhanVien() {
        while (dangChay) {
            int chon = chonMenu("NHÂN VIÊN", "Quay lại", "Thêm nhân viên", "Sửa nhân viên",
                    "Xóa nhân viên chưa có giao dịch", "Tìm nhân viên", "Xem danh sách");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> {
                NhanVien nv = new NhanVien(maMoi("NV"), chuoi("Họ tên: "), dienThoai(),
                        chuoi("Chức vụ: "), soThuc("Lương: ",0,1e15), null);
                duLieu.nhanVien.themNhanVien(nv); System.out.println("Mã nhân viên: " + nv.getMa()); return true;
            });
            else if (chon == 2) thayDoi(() -> {
                NhanVien nv = timNhanVien(); if (nv == null) return false;
                String ten = chuoi("Họ tên mới: "); String sdt = dienThoai();
                String chucVu = chuoi("Chức vụ mới: "); double luong = soThuc("Lương mới: ",0,1e15);
                nv.setHoTen(ten); nv.setSoDienThoai(sdt); nv.setChucVu(chucVu); nv.setLuong(luong); return true;
            });
            else if (chon == 3) thayDoi(() -> {
                NhanVien nv = timNhanVien(); if (nv == null) return false;
                if (coLichSu("NV", nv.getMa())) { System.out.println("Nhân viên đã có giao dịch, không xóa."); return false; }
                duLieu.nhanVien.xoaNhanVien(nv); return true;
            });
            else if (chon == 4) {
                String tu = chuoi("Mã hoặc tên nhân viên: ").toLowerCase(java.util.Locale.ROOT);
                boolean thay = false;
                for (NhanVien nv : duLieu.nhanVien.layDanhSach())
                    if ((nv.getMa()+" "+nv.getHoTen()).toLowerCase(java.util.Locale.ROOT).contains(tu)) {
                        nv.hienThi(); thay = true;
                    }
                if (!thay) System.out.println("Không tìm thấy nhân viên.");
            } else duLieu.nhanVien.hienThi();
        }
    }

    private void menuNhaCungCap() {
        while (dangChay) {
            int chon = chonMenu("NHÀ CUNG CẤP", "Quay lại", "Thêm", "Sửa", "Xóa nếu chưa có phiếu nhập", "Tìm", "Xem danh sách");
            if (chon == 0) return;
            if (chon == 1) thayDoi(() -> duLieu.nhaCungCap.them(new NhaCungCap(
                    duLieu.nhaCungCap.sinhMa(), chuoi("Tên nhà cung cấp: "), chuoi("Địa chỉ: "), dienThoai())));
            else if (chon == 2) thayDoi(() -> {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                if (ncc == null) { System.out.println("Không tìm thấy nhà cung cấp."); return false; }
                String ten = chuoi("Tên mới: "); String diaChi = chuoi("Địa chỉ mới: "); String sdt = dienThoai();
                ncc.setTenNCC(ten); ncc.setDiaChi(diaChi); ncc.setSoDienThoai(sdt); return true;
            });
            else if (chon == 3) thayDoi(() -> duLieu.nhaCungCap.xoa(chuoi("Mã nhà cung cấp: ")));
            else if (chon == 4) {
                NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
                System.out.println(ncc == null ? "Không tìm thấy nhà cung cấp." : ncc);
            } else duLieu.nhaCungCap.hienThi();
        }
    }

    private void menuHoaDon() {
        while (dangChay) {
            int chon = chonMenu("HÓA ĐƠN ĐÃ BÁN", "Quay lại", "Xem danh sách", "Tìm và in lại theo mã", "Lịch sử theo khách hàng");
            if (chon == 0) return;
            if (chon == 1) duLieu.hoaDon.hienThi();
            else if (chon == 2) {
                HoaDon hd = duLieu.hoaDon.timHoaDon(chuoi("Mã hóa đơn: "));
                if (hd == null) System.out.println("Không tìm thấy hóa đơn."); else hd.inHoaDon();
            } else duLieu.hoaDon.timKiemTheoMaKH(chuoi("Mã khách: "));
        }
    }

    public void banHang() { thayDoi(this::lapHoaDon); }

    private boolean lapHoaDon() throws Exception {
        if (duLieu.sanPham.getDsSanPham().isEmpty() || duLieu.nhanVien.layDanhSach().length == 0) {
            System.out.println("Cần có sản phẩm và nhân viên trước khi bán."); return false;
        }
        NhanVien nv = timNhanVien(); if (nv == null) return false;
        int luaChonKH = soNguyen("Khách: 1 Có hồ sơ, 2 Thêm mới, 3 Khách lẻ: ",1,3);
        KhachHang kh;
        if (luaChonKH == 1) {
            String tu = chuoi("Mã khách hoặc số điện thoại: ");
            kh = duLieu.khachHang.timTheoMa(tu);
            if (kh == null) kh = duLieu.khachHang.timTheoSoDienThoai(tu);
            if (kh == null) { System.out.println("Không tìm thấy khách."); return false; }
        } else if (luaChonKH == 2) kh = themKhach();
        else {
            kh = duLieu.khachHang.timTheoMa("KHLE");
            if (kh == null) {
                kh = new KhachHang("KHLE","Khách lẻ","0000000000","Tại quầy");
                duLieu.khachHang.themKhachHang(kh);
            }
        }
        // Chưa có cơ chế tích điểm đồng bộ: bán lẻ dùng 0 điểm để tránh giảm giá tùy ý.
        HoaDon hd = new HoaDonBanLe(maMoi("HD"), ngayHomNay(), kh.getMa(), nv.getMa(), 0);
        while (true) {
            int chon = chonMenu("HÓA ĐƠN TẠM " + hd.getMaHD(), "Hủy hóa đơn", "Thêm sản phẩm", "Đổi số lượng", "Bỏ sản phẩm", "Xem hóa đơn", "Xác nhận thanh toán");
            if (chon == 0) { System.out.println("Đã hủy, tồn kho không thay đổi."); return false; }
            if (chon == 1) {
                duLieu.sanPham.hienThiDanhSach();
                SanPham sp = timSanPham(); if (sp == null) continue;
                int daChon = 0;
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(sp.getMaSP())) daChon = ct.getSoLuong();
                int conLai = sp.getSoLuong() - daChon;
                if (conLai <= 0) { System.out.println("Không đủ tồn kho để thêm."); continue; }
                int sl = soNguyen("Số lượng thêm: ",1,conLai);
                hd.themChiTiet(new ChiTietHoaDon(hd.getMaHD(),sp.getMaSP(),sl,sp.tinhGiaBan()));
            } else if (chon == 2) {
                String ma = chuoi("Mã sản phẩm đã chọn: ");
                ChiTietHoaDon ct = chiTiet(hd, ma);
                if (ct == null) { System.out.println("Sản phẩm không có trong hóa đơn."); continue; }
                SanPham sp = duLieu.sanPham.searchSanPham(ma);
                if (sp.getSoLuong() == 0) { System.out.println("Sản phẩm hết hàng."); continue; }
                ct.setSoLuong(soNguyen("Số lượng mới: ",1,sp.getSoLuong()));
            } else if (chon == 3) {
                if (!hd.xoaChiTiet(chuoi("Mã sản phẩm cần bỏ: "))) System.out.println("Không có sản phẩm trong hóa đơn.");
            } else if (chon == 4) hd.inHoaDon();
            else {
                if (hd.getSoLuongChiTiet() == 0) { System.out.println("Hóa đơn phải có sản phẩm."); continue; }
                hd.inHoaDon();
                if (!xacNhan()) return false;
                // Kiểm tra toàn bộ trước khi giảm bất kỳ sản phẩm nào.
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getMaSP());
                    if (sp == null || sp.getSoLuong() < ct.getSoLuong()) throw new IllegalStateException("Không đủ hàng.");
                }
                for (ChiTietHoaDon ct : hd.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getMaSP());
                    sp.setSoLuong(sp.getSoLuong()-ct.getSoLuong());
                }
                if (!duLieu.hoaDon.themHoaDon(hd)) throw new IllegalStateException("Không thêm được hóa đơn.");
                return true;
            }
        }
    }

    private ChiTietHoaDon chiTiet(HoaDon hd, String ma) {
        for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(ma)) return ct;
        return null;
    }

    private void menuNhapHang() {
        while (dangChay) {
            int chon = chonMenu("NHẬP HÀNG", "Quay lại", "Lập phiếu nhập và tăng tồn", "Xem phiếu đã nhập", "Tìm phiếu theo mã");
            if (chon == 0) return;
            if (chon == 1) nhapHang();
            else if (chon == 2) duLieu.phieuNhap.hienThi();
            else {
                PhieuNhap ph = duLieu.phieuNhap.timTheoMa(chuoi("Mã phiếu: "));
                if (ph == null) System.out.println("Không tìm thấy phiếu."); else ph.hienThi();
            }
        }
    }

    public void nhapHang() { thayDoi(this::lapPhieuNhap); }

    private boolean lapPhieuNhap() {
        if (duLieu.nhaCungCap.laRong() || duLieu.sanPham.getDsSanPham().isEmpty() || duLieu.nhanVien.layDanhSach().length == 0) {
            System.out.println("Cần có nhà cung cấp, sản phẩm và nhân viên trước khi nhập."); return false;
        }
        duLieu.nhaCungCap.hienThi();
        NhaCungCap ncc = duLieu.nhaCungCap.timTheoMa(chuoi("Mã nhà cung cấp: "));
        if (ncc == null) { System.out.println("Không tìm thấy nhà cung cấp."); return false; }
        NhanVien nv = timNhanVien(); if (nv == null) return false;
        PhieuNhap ph = new PhieuNhap(duLieu.phieuNhap.sinhMaPN(), ngayHomNay(), ncc.getMaNCC(),nv.getMa());
        while (true) {
            int chon = chonMenu("PHIẾU NHẬP TẠM " + ph.getMaPN(), "Hủy phiếu", "Thêm sản phẩm", "Xem phiếu", "Xác nhận nhập kho");
            if (chon == 0) { System.out.println("Đã hủy phiếu, tồn kho không thay đổi."); return false; }
            if (chon == 1) {
                SanPham sp = timSanPham(); if (sp == null) continue;
                boolean trung = false;
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) if (ct.getNhapLieu().getMaSP().equalsIgnoreCase(sp.getMaSP())) trung = true;
                if (trung) { System.out.println("Sản phẩm đã có trong phiếu. Hủy và lập lại nếu cần đổi số lượng."); continue; }
                if (sp.getSoLuong() == Integer.MAX_VALUE) { System.out.println("Tồn kho đã đạt giới hạn."); continue; }
                int sl = soNguyen("Số lượng nhập: ",1,Integer.MAX_VALUE-sp.getSoLuong());
                double gia = soThuc("Giá nhập: ",0,1e15);
                String maCT = ph.getMaPN()+"-CT"+(ph.getDsChiTiet().size()+1);
                ph.themChiTiet(new ChiTietPhieuNhap(maCT,ph.getMaPN(),new NhapLieu(maCT+"-NL",sp.getMaSP(),sl,gia)));
            } else if (chon == 2) ph.hienThi();
            else {
                if (ph.getDsChiTiet().isEmpty()) { System.out.println("Phiếu cần ít nhất một sản phẩm."); continue; }
                ph.hienThi(); if (!xacNhan()) return false;
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getNhapLieu().getMaSP());
                    Math.addExact(sp.getSoLuong(),ct.getNhapLieu().getSoLuongNhap());
                }
                for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) {
                    SanPham sp = duLieu.sanPham.searchSanPham(ct.getNhapLieu().getMaSP());
                    sp.setSoLuong(sp.getSoLuong()+ct.getNhapLieu().getSoLuongNhap());
                }
                if (!duLieu.phieuNhap.them(ph)) throw new IllegalStateException("Không thêm được phiếu nhập.");
                return true;
            }
        }
    }

    /** Không cho xóa danh mục mà hóa đơn/phiếu nhập đang tham chiếu. */
    private boolean coLichSu(String loai, String ma) {
        for (HoaDon hd : duLieu.hoaDon.getDs()) {
            if (loai.equals("KH") && hd.getMaKH().equalsIgnoreCase(ma)) return true;
            if (loai.equals("NV") && hd.getMaNV().equalsIgnoreCase(ma)) return true;
            if (loai.equals("SP")) for (ChiTietHoaDon ct : hd.getDsChiTiet()) if (ct.getMaSP().equalsIgnoreCase(ma)) return true;
        }
        for (PhieuNhap ph : duLieu.phieuNhap.layDanhSach()) {
            if (loai.equals("NV") && ph.getMaNV().equalsIgnoreCase(ma)) return true;
            if (loai.equals("SP")) for (ChiTietPhieuNhap ct : ph.getDsChiTiet()) if (ct.getNhapLieu().getMaSP().equalsIgnoreCase(ma)) return true;
        }
        return false;
    }

    private SanPham timSanPham() {
        SanPham sp = duLieu.sanPham.searchSanPham(chuoi("Mã sản phẩm: "));
        if (sp == null) System.out.println("Không tìm thấy sản phẩm."); return sp;
    }
    private NhanVien timNhanVien() {
        duLieu.nhanVien.hienThi();
        String ma = chuoi("Mã nhân viên lập: ");
        for (NhanVien nv : duLieu.nhanVien.layDanhSach()) if (nv.getMa().equalsIgnoreCase(ma)) return nv;
        System.out.println("Không tìm thấy nhân viên."); return null;
    }

    /** Sinh mã dựa trên dữ liệu đã nạp, không phụ thuộc bộ đếm static khi mở lại. */
    private String maMoi(String tienTo) {
        long i = 1;
        while (true) {
            String ma = String.format(java.util.Locale.ROOT,"%s%03d",tienTo,i++);
            boolean tonTai = switch (tienTo) {
                case "KH" -> duLieu.khachHang.timTheoMa(ma) != null;
                case "NV" -> java.util.Arrays.stream(duLieu.nhanVien.layDanhSach()).anyMatch(nv -> nv.getMa().equalsIgnoreCase(ma));
                case "HD" -> duLieu.hoaDon.timHoaDon(ma) != null;
                default -> duLieu.sanPham.searchSanPham(ma) != null;
            };
            if (!tonTai) return ma;
        }
    }

    @FunctionalInterface
    private interface ThaoTac { boolean thucHien() throws Exception; }

    private void thayDoi(ThaoTac thaoTac) {
        try {
            if (!thaoTac.thucHien()) { napDuLieu(); return; }
            duLieu.luu();
            System.out.println("ĐÃ HOÀN TẤT VÀ LƯU DỮ LIỆU.");
        } catch (Exception e) {
            System.out.println("Thao tác chưa hoàn tất: " + lyDo(e));
            try { napDuLieu(); }
            catch (Exception docLoi) {
                System.out.println("Không thể nạp lại phiên bản đã lưu. Dừng chương trình: " + lyDo(docLoi));
                dangChay = false;
            }
            if (e instanceof NoSuchElementException) dangChay = false;
        }
    }

    private int chonMenu(String tieuDe, String mucSoKhong, String... muc) {
        System.out.println("\n===== " + tieuDe + " =====");
        for (int i=0;i<muc.length;i++) System.out.println((i+1)+". "+muc[i]);
        System.out.println("0. "+mucSoKhong);
        return soNguyen("Chọn: ",0,muc.length);
    }
    public static int nhapSoNguyen(Scanner sc, String thongBao, int min, int max) {
        if (sc == null || min < 0 || max < min) throw new IllegalArgumentException("Khoảng nhập không hợp lệ.");
        while (true) {
            System.out.print(thongBao);
            if (!sc.hasNextLine()) return -1;
            try { int n = Integer.parseInt(sc.nextLine().trim()); if(n>=min && n<=max) return n; }
            catch (NumberFormatException e) { /* Nhập lại nếu không phải số nguyên. */ }
            System.out.println("Vui lòng nhập số nguyên từ "+min+" đến "+max+".");
        }
    }
    private int soNguyen(String thongBao, int min, int max) {
        int n=nhapSoNguyen(sc,thongBao,min,max);
        if(n==-1) throw new NoSuchElementException("Hết dữ liệu nhập."); return n;
    }
    private double soThuc(String thongBao, double min, double max) {
        while(true) {
            String s=chuoi(thongBao);
            try { double n=Double.parseDouble(s); if(Double.isFinite(n)&&n>=min&&n<=max) return n; }
            catch(NumberFormatException e) { /* Tiếp tục nhập. */ }
            System.out.println("Nhập số hữu hạn từ "+min+" đến "+max+".");
        }
    }
    private String chuoi(String thongBao) {
        while(true) {
            System.out.print(thongBao);
            if(!sc.hasNextLine()) throw new NoSuchElementException("Hết dữ liệu nhập.");
            String s=sc.nextLine().trim();
            if(!s.isEmpty()&&!s.contains(";")) return s;
            System.out.println("Không để trống hoặc chứa dấu chấm phẩy (;).");
        }
    }
    private String dienThoai() {
        while(true) {
            String s=chuoi("Số điện thoại (10 chữ số, bắt đầu bằng 0): ");
            if(s.matches("0[0-9]{9}")) return s;
            System.out.println("Số điện thoại không đúng định dạng.");
        }
    }
    private boolean xacNhan() { return soNguyen("Xác nhận: 1 Đồng ý, 0 Hủy: ",0,1)==1; }
    private String ngayHomNay() { return LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")); }
    private String lyDo(Throwable e) {
        while(e.getCause()!=null) e=e.getCause();
        return e.getMessage()==null ? e.getClass().getSimpleName() : e.getMessage();
    }
}
