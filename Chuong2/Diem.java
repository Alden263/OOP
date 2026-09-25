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

    public static void main(String [] args){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhập vào tọa độ điểm A ");
        Diem A=new Diem();
        A.nhap(sc);
        System.out.println("Điểm vừa nhập ");
        A.xuat();
        System.out.println("Nhập độ dời dx ");
        double dx=sc.nextDouble();
        System.out.println("Nhập độ dời dy ");
        double dy=sc.nextDouble();
        A.diChuyen(dx,dy);
        System.out.println("Tọa độ đểm A sau khi di chuyển là ");
        A.xuat();
    }
}
