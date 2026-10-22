package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class ThucHanh extends MonHoc{
    private double diemChuyenCan;
    private double diemGiuaKy;
    private double diemCuoiKy;
    public ThucHanh(){
        super();
        this.diemChuyenCan=0;
        this.diemGiuaKy=0;
        this.diemCuoiKy=0;
    }
    public ThucHanh(String maMonHoc, String tenMonHoc, int soTinChi, double diemChuyenCan, double diemGiuaKy, double diemCuoiKy){
        super(maMonHoc,tenMonHoc,soTinChi);
        this. diemChuyenCan=diemChuyenCan;
        this. diemGiuaKy=diemGiuaKy;
        this.diemCuoiKy=diemCuoiKy;
    }
    @Override
    public double tinhDTB(){
        return( diemChuyenCan*0.1)+(diemGiuaKy*0.4)+(diemCuoiKy*0.5);
    }
    @Override
    public void nhap(Scanner sc){
        super.nhap(sc);
        System.out.println("Nhập điểm chuyên cần: ");
        diemChuyenCan=sc.nextDouble();
        System.out.println("Nhập điểm giữa kỳ: ");
        diemGiuaKy=sc.nextDouble();
        System.out.println("Nhập điểm cuoi kì: ");
        diemCuoiKy=sc.nextDouble();

    }
}
