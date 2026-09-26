package Chuong2.BaiTapChuong2;

import java.util.Arrays;
import java.util.Scanner;

public class MangSoNguyen {
    private int[] a;
    private int n;
    public MangSoNguyen(){
        this.a=new int[0];
        this.n=0;
    }
    public MangSoNguyen( int n){
        this.n=n;
        this.a =new int[n];
    }
    public void nhap(Scanner sc){
        System.out.println("Nhập vào kích thước mảng ");
        n=sc.nextInt();
        a=new int [n];
        for (int i =0; i<n;i++){
            System.out.print("a["+i+"] = " );
            a[i]=sc.nextInt();
        }

    }
    public void xuat(){
        System.out.println(Arrays.toString(a));
    }
    public int tinhTong(){
        int tong=0;
        for(int x :a){
            tong+=x;
        }
        return tong;
    }
    public void xoaViTri(int k){
        if(k<0 || k>n){
            return;
        }
        int [] newarr= new int [n-1];
        for(int i=0,j=0;i<n;i++){
            if(i!=k){
                newarr[j++]=a[i];
            }
        }
        this.a=newarr;
        n--;
    }
}
