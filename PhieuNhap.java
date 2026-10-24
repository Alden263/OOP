import java.util.ArrayList;
import java.util.List;

public class PhieuNhap {
    private String maPN;
    private String ngayNhap;
    private String maNCC;   // ma nha cung cap
    private String maNV;    // ma nhan vien lap phieu

    // danh sach chi tiet cua phieu (composition trong so do)
    private List<ChiTietPhieuNhap> dsChiTiet = new ArrayList<>();

    // constructor, luc dau chua co chi tiet
    public PhieuNhap(String maPN, String ngayNhap, String maNCC, String maNV) {
        this.maPN = maPN;
        this.ngayNhap = ngayNhap;
        this.maNCC = maNCC;
        this.maNV = maNV;
    }

    // getter setter
    public String getMaPN() { return maPN; }
    public void setMaPN(String maPN) { this.maPN = maPN; }

    public String getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(String ngayNhap) { this.ngayNhap = ngayNhap; }

    public String getMaNCC() { return maNCC; }
    public void setMaNCC(String maNCC) { this.maNCC = maNCC; }

    public String getMaNV() { return maNV; }
    public void setMaNV(String maNV) { this.maNV = maNV; }

    public List<ChiTietPhieuNhap> getDsChiTiet() { return dsChiTiet; }

    // them 1 dong chi tiet vao phieu
    public void themChiTiet(ChiTietPhieuNhap ct) {
        if (ct == null) {
            throw new IllegalArgumentException("Chi tiet khong duoc null");
        }
        // check dung ma phieu, tranh them nham chi tiet cua phieu khac
        if (!this.maPN.equals(ct.getMaPN())) {
            throw new IllegalArgumentException("Chi tiet thuoc phieu " + ct.getMaPN()
                    + ", khong phai phieu " + this.maPN);
        }
        dsChiTiet.add(ct);
    }

    // tong tien = cong thanh tien tat ca chi tiet
    public double tinhTongTien() {
        double tong = 0;
        for (ChiTietPhieuNhap ct : dsChiTiet) {
            tong += ct.tinhThanhTien();
        }
        return tong;
    }

    // in phieu nhap ra man hinh
    public void hienThi() {
        System.out.println("Phieu nhap: " + maPN + " - Ngay: " + ngayNhap
                + " - NCC: " + maNCC + " - NV: " + maNV);
        for (ChiTietPhieuNhap ct : dsChiTiet) {
            NhapLieu nl = ct.getNhapLieu();
            System.out.println("  - " + ct.getMaCTPN() + " | SP: " + nl.getMaSP()
                    + " | SL: " + nl.getSoLuongNhap() + " | Gia: " + nl.getGiaNhap()
                    + " | Thanh tien: " + ct.tinhThanhTien());
        }
        System.out.println("Tong tien: " + tinhTongTien());
    }

    @Override
    public String toString() {
        return "PhieuNhap{maPN=" + maPN + ", ngayNhap=" + ngayNhap
                + ", maNCC=" + maNCC + ", maNV=" + maNV
                + ", tongTien=" + tinhTongTien() + "}";
    }
}