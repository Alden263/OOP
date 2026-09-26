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
        if(timkiemTheoMa(ssv.getMasv())!=null){
            System.out.println("Sinh viên đã tồn tại "+ssv.getMasv());
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
    public SinhVien timkiemTheoMa(String maSV){
        for( int i =0;i<siSo;i++){
            if(sv[i].getMasv().equalsIgnoreCase(maSV)){
                return sv[i];
            }
        }
        return null;
    }
    public void timKiemTheoTen(String name){
        boolean timThay=false;
        System.out.println("Kết quả tìm kiếm sinh viên có tên "+name);
        for(int i=0;i<siSo;i++){
            if(sv[i].getHoten().toLowerCase().contains(name.toLowerCase())){
                sv[i].xuat();
                timThay=true;
            }
        }
        if(!timThay){
            System.out.println("Không có sinh viên nào trùng với tên vừa nhập");
        }

    }
    public boolean xoaSinhVien(String maSV){
        int index=-1;
        for(int i=0; i<siSo;i++){
            if(sv[i].getMasv().equalsIgnoreCase(maSV)){
                index=i;
                break;
            }
        }
        if(index==-1){
            System.out.println("Không tìm thấy sinh viên có mã sinh viên "+maSV);
            return false;
        }
        for(int i=index;i<siSo;i++){
            sv[i]=sv[i+1];

        }
        sv[siSo-1]=null;
        siSo--;
        System.out.println("Đã xóa thành công sinh viên có mã "+maSV);
        return true;
    }
    public void sapXepDiemTB(){
        for(int i=0;i<siSo-1;i++){
            for(int j=i+1;j<siSo;j++){
                if(sv[i].tinhDiemTB()<sv[j].tinhDiemTB()){
                    SinhVien temp=sv[i];
                    sv[i]=sv[j];
                    sv[j]=temp;
                }
            }
        }
        System.out.println("Đã sắp xếp xong danh sách điểm trung bình giảm dần ");
    }


}
