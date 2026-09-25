package Chuong2;

import java.util.Scanner;

public class SoNguyen {
    private int value;
    public SoNguyen(){
        this.value=0;
    }
    public SoNguyen (int value){
        this.value=value;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
    public boolean laSoChan(){
        return value%2==0;
    }
    public boolean laSNT(){
        if (value <2){
            return false;
        }
        for (int i=2;i*i<value;i++){
            if (value%i==0){
                return false;
            }

        }
        return true;
    }
    public boolean laSHH(){
        if (value<1){
            return false;

        }
        int tong=0;
        for(int i=2; i*i<=value;i++){
            if(value %i==0){
                tong+=i;
                if(i*i!=value){
                    tong+=value/i;
                }
            }
        }
        return tong==value;
    }
    public void nhap(Scanner sc){
        System.out.println("Nhap số nguyen ");
        value=sc.nextInt();
    }
    public void xuat(){
        System.out.println("Số nguyên vừa nhâ là "+value);
    }
}
