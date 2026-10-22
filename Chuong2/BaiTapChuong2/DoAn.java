package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class DoAn extends MonHoc{
    private double diemChuyenCan;
    private double diemDoAn1;
    private double diemDoAn2;
    public DoAn(){
        super();
        this.diemChuyenCan=0;
        this.diemDoAn1=0;
        this.diemDoAn2=0;

    }
    public DoAn(String maMonHoc, String tenMonHoc, int soTinChi, double diemChuyenCan, double diemDoAn1, double diemDoAn2){
        super(maMonHoc,tenMonHoc,soTinChi);
        this.diemChuyenCan=diemChuyenCan;
        this.diemDoAn1=diemDoAn1;
        this.diemDoAn2=diemDoAn2;
    }
    @Override
    public double tinhDTB(){
        return (diemChuyenCan*0.1)+(diemDoAn1*0.2)+(diemDoAn2*0.7);
    }
    @Override
    public void nhap(Scanner sc){
        super.nhap(sc);
        System.out.println("Nhập điêm chuyên cần: ");
        diemChuyenCan=sc.nextDouble();
        System.out.println("Nhập điểm đồ án 1: ");
        diemDoAn1=sc.nextDouble();
        System.out.println("Nhập điểm đồ án 2: ");
        diemDoAn2=sc.nextDouble();
    }
}
