package Chuong2;

import java.util.Scanner;

public class HinhChuNhat {
    private double chieudai;
    private double chieurong;
    public HinhChuNhat(){
        this.chieudai=0;
        this.chieurong=0;
    }
    public HinhChuNhat(int chieudai, int chieurong){
        this.chieudai=chieudai;
        this.chieurong=chieurong;
    }
    public void setChieudai(int chieudai){
        this.chieudai=chieudai;

    }
    public void setChieurong( int chieurong){
        this.chieurong=chieurong;
    }

    public double getChieudai() {
        return chieudai;
    }

    public double getChieurong() {
        return chieurong;
    }
    public void nhap(Scanner sc){
        System.out.println("Nhập vào chiều dài ");
        chieudai=sc.nextDouble();
        System.out.println("Nhập vào chiều rộng ");
        chieurong=sc.nextDouble();
    }
    public void xuat() {
        System.out.printf("Dài: %.2f | Rộng: %.2f | Chu vi: %.2f | Diện tích: %.2f\n",
                chieudai, chieurong, tinhChuVi(), tinhDienTich());
    }
    public double tinhChuVi(){
        return (chieudai+chieurong)*2;

    }
    public double tinhDienTich(){
        return chieudai*chieurong;
    }
}
