package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public abstract class MonHoc {
    public String maMonHoc;
    public String tenMonHoc;

    public String getMaMonHoc() {
        return maMonHoc;
    }

    public void setMaMonHoc(String maMonHoc) {
        this.maMonHoc = maMonHoc;
    }

    public String getTenMonHoc() {
        return tenMonHoc;
    }

    public void setTenMonHoc(String tenMonHoc) {
        this.tenMonHoc = tenMonHoc;
    }

    public int getSoTinChi() {
        return soTinChi;
    }

    public void setSoTinChi(int soTinChi) {
        this.soTinChi = soTinChi;
    }

    public double getDiemTrungBinh() {
        return diemTrungBinh;
    }

    public void setDiemTrungBinh(double diemTrungBinh) {
        this.diemTrungBinh = diemTrungBinh;
    }

    public int soTinChi;
    public double diemTrungBinh;
    public MonHoc(){};
    public MonHoc(String tenMonHoc, String maMonHoc, int soTinChi){
        this.maMonHoc=maMonHoc;
        this.tenMonHoc=tenMonHoc;
        this.soTinChi=soTinChi;
    }
    public abstract double tinhDTB();
    public double getDTB() {
        if (this.diemTrungBinh > 0) {
            return this.diemTrungBinh; // Lấy điểm từ file đọc lên
        }
        return tinhDTB(); // Hoặc tính toán nếu nhập mới từ bàn phím
    }
    public void nhap(Scanner sc){
        System.out.println("Nhập mã môn học: ");
        maMonHoc=sc.nextLine();
        System.out.println("Nhập tên môn học: ");
        tenMonHoc=sc.nextLine();
        System.out.println("Nhập số tín chỉ: ");
        soTinChi=sc.nextInt();
    }
    public String quyDoiDiemChu(){
        double dtb = (this.diemTrungBinh > 0) ? this.diemTrungBinh : tinhDTB();
        if (dtb>=8.5){
            return "A";
        } else if (dtb>=7 ) {
            return "B";
        } else if (dtb>=5.5) {
            return "C";
        } else if (dtb >=4) {
            return "D";
        }else {
            return "F";
        }

    }
}
