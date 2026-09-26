package Chuong2;
import java.util.Scanner;

public class Diem {
    private double x;
    private double y;
    public Diem(){
        this.x=0;
        this.y=0;

    }
    public Diem(double x, double y){
        this.x=x;
        this.y=y;
    }
    public double getX(){
        return x;

    }
    public void setX(double x){
        this.x=x;

    }
    public double getY(){

        return y;
    }
    public void setY(double y){

    }
    public void nhap(Scanner sc){
        System.out.println("Nhập tọa độ x ");
        this. x=sc.nextDouble();
        System.out.println("Nhập tọa độ y ");
        this. y =sc.nextDouble();
    }
    public void xuat() {

        System.out.printf("(%.2f, %.2f)\n", this.x, this.y);
    }
    public void diChuyen( double dx, double dy){
        this.x+=dx;
        this.y+=dy;
    }


}
