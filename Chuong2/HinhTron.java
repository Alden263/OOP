package Chuong2;

import java.util.Scanner;

public class HinhTron {
    private double banKinh;
    public HinhTron(){
        this.banKinh=1;
    }
    public HinhTron(double banKinh){
        this.banKinh=banKinh;
    }

    public void setBanKinh(double banKinh) {
        this.banKinh = banKinh;
    }

    public double getBanKinh() {
        return banKinh;
    }
    public void nhap(Scanner sc){
        System.out.println("Nhập vào bán kính của hình tròn ");
        banKinh=sc.nextDouble();

    }
    public double tinhChuvi(){
        return 2*3.14*banKinh;
    }
    public double tinhDientich(){
        return banKinh*banKinh*3.14;
    }
    public void xuat(){
        System.out.printf("Bán kính: %.2f | Chu vi: %.2f | Diện tích: %.2f\n",
                banKinh, tinhChuvi(), tinhDientich());
    }
}
