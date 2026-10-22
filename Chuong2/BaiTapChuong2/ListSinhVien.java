package Chuong2.BaiTapChuong2;

import java.util.ArrayList;

public class ListSinhVien {
    private ArrayList<SinhVien> ds;
    public ListSinhVien(){
        this.ds= new ArrayList<SinhVien>();

    }
    public void themSinhVien( SinhVien sv){
        if(timkiemTheoMa(sv.getMasv())==null){
            ds.add(sv);
            System.out.println("Thêm sinh viên thành công");
        }else {
            System.out.println("Sinh viên đã tồn tại");
        }
    }
    public SinhVien timkiemTheoMa(String masv){
        for(SinhVien sv: ds){
            if(sv.getMasv().equalsIgnoreCase(masv)){
                return sv;
            }

        }
        return null;
    }
}
