package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class LyThuyet extends MonHoc{
    private double diemChuyenCan;
    private double diemGiuaKy;
    private double diemCuoiKy;
    public LyThuyet(){
        super();
        this.diemChuyenCan=0;
        this.diemCuoiKy=0;
        this.diemGiuaKy=0;
    }
    public LyThuyet(String maMonHoc, String tenMonHoc, int soTinChi, double diemChuyenCan, double diemGiuaKy, double diemCuoiKy){
        super(maMonHoc,tenMonHoc,soTinChi);
        this.diemChuyenCan=diemChuyenCan;
        this.diemGiuaKy=diemGiuaKy;
        this.diemCuoiKy=diemCuoiKy;
    }
    @Override
    public double tinhDTB(){
        return (diemChuyenCan*0.1)+(diemGiuaKy*0.3)+(diemCuoiKy*0.6);
    }
    @Override
    public void nhap(Scanner sc){
        super.nhap(sc);
        System.out.println("Nhập điểm chuyên cần ");
        diemChuyenCan=sc.nextDouble();
        System.out.println("Nhập điểm giữa kỳ: ");
        diemGiuaKy=sc.nextDouble();
        System.out.println("Nhập điểm cuối kỳ: ");
        diemCuoiKy=sc.nextDouble();

    }
}
