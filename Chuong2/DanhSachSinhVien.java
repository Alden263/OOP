package Chuong2;

import java.util.Scanner;

public class DanhSachSinhVien {
    private SinhVien[] sv;
    private int siSo;
    private static final int capacity=100;
    public DanhSachSinhVien(){
        this.sv=new SinhVien[capacity];
        this.siSo=0;
    }
    public DanhSachSinhVien(int n){
        this.sv=new SinhVien[n];
        this.siSo=0;
    }
    public int getSiSo(){
        return siSo;
    }
    public boolean themSinhVien(SinhVien ssv){
        if(siSo>=sv.length){
            System.out.println("Danh sách đã đầy ");
            return false;
        }
        sv[siSo]=ssv;
        siSo++;
        return true;
    }
    public void nhapSinhVien(Scanner sc){
        System.out.println("Nhập một sinh viên ");
        SinhVien sv=new SinhVien();
        sv.nhap(sc);
        if(themSinhVien(sv)){
            System.out.println("Thêm sinh viên thành công");
        }
    }
    public void xuatDanhSach(){
        if(siSo==0){
            System.out.println("Danh sách hiện đang rỗng ");
            return;
        }
        System.out.println("Danh sách sinh viên bao gồm "+siSo+" sinnh viên");
        for(int i=0;i<siSo;i++){
            System.out.println((i+1)+".");
            sv[i].xuat();
        }
    }


}
