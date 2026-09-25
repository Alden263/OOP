import java.util.Scanner;

public class SinhVien {
    private String masv;
    private String hoten;
    private String diachi;
    private String sodt;
    private static int demSV=0;
    public SinhVien(){
        this.masv=null;
        this.hoten=null;
        this.diachi=null;
        this.sodt=null;
        demSV++;
    }
    public SinhVien(String masv,String hoten, String diachi, String sodt){
        this.masv=masv;
        this.diachi=diachi;
        this.hoten=hoten;
        this.sodt=sodt;
        demSV++;
    }
    public SinhVien(SinhVien sv){
        masv=sv.masv;
        diachi=sv.diachi;
        hoten=sv.hoten;
        sodt=sv.sodt;
        demSV++;
    }
    public void setMasv(String masv){
        this.masv=masv;

    }
    public String getMasv(){
        return masv;
    }
    public void setHoten(String hoten){
        this.hoten=hoten;
    }
    public String getHoten(){
        return hoten;
    }
    public void setDiachi(String diachi){
        this.diachi=diachi;

    }
    public String getDiachi(){
        return diachi;
    }
    public void setSodt(String sodt){
        this.sodt=sodt;
    }
    public String getSodt(){
        return sodt;
    }
    public void nhapSV(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhap vao ma sinh vien ");
        masv=sc.nextLine();
        System.out.println("Nhap vao ho ten ");
        hoten=sc.nextLine();
        System.out.println("Nhap vao dia chi ");
        diachi=sc.nextLine();
        System.out.println("Nhap vao so dien thoai ");
        sodt=sc.nextLine();
    }
    public void xuatSV(){
        System.out.println("Ma SV "+masv);
        System.out.println("Ho ten "+hoten);
        System.out.println("Dia chi "+diachi);
        System.out.println("So dien thoai "+sodt);
    }
    public static int getDemSV(){
        return demSV;
    }
    public static void main(String [] args){
        SinhVien sv1=new SinhVien();
        SinhVien sv2=new SinhVien("001","Nguyen Toan Thang","892938","8e7328732874");
        SinhVien sv3=new SinhVien(sv2);
        sv1.masv="2323";
        sv1.nhapSV();
        sv1.xuatSV();
        sv2.xuatSV();
        sv3.xuatSV();
        System.out.println("Tong so sinh vien "+SinhVien.getDemSV());
        System.out.println("Ma SV cua sinh vien 1 la "+sv1.getMasv());

    }
}
