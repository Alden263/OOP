package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class SinhVienLT extends SinhVien {
    String trinhDoDauVao;
    String truongTN;
    String hinhThucDauVao;
    public SinhVienLT(){
        super();
        this.trinhDoDauVao=" ";
        this.truongTN=" ";
        this.hinhThucDauVao=" ";
    }
    public SinhVienLT(String trinhDoDauVao,String truongTN, String hinhThucDauVao){
        super();
        this.trinhDoDauVao=trinhDoDauVao;
        this.truongTN=truongTN;
        this.hinhThucDauVao=hinhThucDauVao;
    }
    public void nhap(Scanner sc){
        super.nhap(sc);
        sc.nextLine();
        System.out.println("Nhập trình độ đầu vào ");
        this.trinhDoDauVao=sc.nextLine();
        System.out.println("Nhập trường tốt nghiệp ");
        this.truongTN=sc.nextLine();
        System.out.println("Nhập hình tức đầu vào ");
        this.hinhThucDauVao=sc.nextLine();

    }
    public void xuat(){
        super.xuat();
        System.out.println("-> Chi tiết LT: [Trình độ: " + trinhDoDauVao
                + " | Trường TN: " + truongTN
                + " | Hình thức: " + hinhThucDauVao + "]");
    }
    public static void main(String []args){
        Scanner sc=new Scanner(System.in);
        SinhVienLT A= new SinhVienLT();
        A.nhap(sc);
        A.xuat();
    }
}
