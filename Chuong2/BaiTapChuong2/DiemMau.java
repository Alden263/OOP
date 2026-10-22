package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class DiemMau extends Diem{
    private String mau;
    public DiemMau(){
        super();
        this.mau=" ";
    }
    public DiemMau(String mau){
        super();
        this.mau=mau;
    }
    public String getMau(){
        return mau;
    }
    public void setMau(String mau){
        this.mau=mau;
    }
    public void nhap(Scanner sc){
        super.nhap(sc);
        sc.nextLine();
        System.out.println("Nhập màu vào ");
        mau=sc.nextLine();
    }
    public void xuat(){
        super.xuat();
        System.out.println("Màu vừa nhập là "+mau);

    }
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        DiemMau A=new DiemMau();
        A.nhap(sc);
        A.xuat();

    }
}
