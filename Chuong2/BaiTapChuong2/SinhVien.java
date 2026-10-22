package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class SinhVien {
    private String masv;
    private  String hoten;
    private String lophoc;
    private double diemmon1, diemmon2,diemmon3;
    public SinhVien(){
        this.masv="001";
        this.hoten=" ";
        this.lophoc=" ";
        this.diemmon1=0;
        this.diemmon2=0;
        this.diemmon3=0;
    }
    public SinhVien(String masv,String  hoten,String lophoc, double diemmon1, double diemmon2, double diemmon3){
        this.masv=masv;
        this.hoten=hoten;
        this.lophoc=lophoc;
        this.diemmon1=diemmon1;
        this.diemmon2=diemmon2;
        this.diemmon3=diemmon3;
    }

    public void setMasv(String masv) {

        this.masv = masv;
    }
    public String getMasv() {
        return masv;
    }

    public void setHoten(String hoten) {
        this.hoten = hoten;
    }

    public String getHoten(){
        return hoten;
    }

    public void setLophoc(String lophoc) {
        this.lophoc = lophoc;
    }

    public String getLophoc() {
        return lophoc;
    }

    public void setDiemmon1(double diemmon1) {
        this.diemmon1 = diemmon1;
    }

    public double getDiemmon1() {
        return diemmon1;
    }

    public void setDiemmon2(double diemmon2) {
        this.diemmon2 = diemmon2;
    }

    public double getDiemmon2() {
        return diemmon2;
    }

    public void setDiemmon3(double diemmon3) {
        this.diemmon3 = diemmon3;
    }

    public double getDiemmon3() {
        return diemmon3;
    }

    public void nhap(Scanner sc){
        System.out.println("Nhập mã sinh viên ");
        masv=sc.nextLine();
        System.out.println("Nhập họ tên ");
        hoten=sc.nextLine();
        System.out.println("Nhập lớp học ");
        lophoc=sc.nextLine();
        System.out.println("Nhập điểm môn 1 ");
        diemmon1=sc.nextDouble();
        System.out.println("Nhập điểm môn 2 ");
        diemmon2=sc.nextDouble();
        System.out.println("Nhập điểm môn 3 ");
        diemmon3=sc.nextDouble();
    }
    public void xuat() {
        System.out.printf("Mã: %s | Tên: %-18s | Lớp: %s | ĐTB: %.2f | Xếp loại: %s\n",
                masv, hoten, lophoc, tinhDiemTB(), xepLoai());
    }
    public double tinhDiemTB(){
        return(diemmon1+diemmon2+diemmon3)/3;
    }
    public String xepLoai(){
         double average=tinhDiemTB();
         if(average>=8.5){
             return "Giỏi";
         }
         if(average>=7){
             return "Khá ";

         }
         if(average>=5){
             return "Trung bình";

         }
         return "Yếu";
    }


}
