package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class SinhVienCQ extends SinhVien{
    String hinhThucDauVao;
    String truongTN;
    String trinhDoDauVao;
    public SinhVienCQ(){
        this.hinhThucDauVao="";
        this.truongTN="" ;
        this.trinhDoDauVao="";
    }
    public SinhVienCQ(String hinhThucDauVao, String truongTN, String trinhDoDauVao){
        this.trinhDoDauVao=trinhDoDauVao;
        this.truongTN=truongTN;
        this.hinhThucDauVao=hinhThucDauVao;
    }
    public void nhap(Scanner sc){
        super.nhap(sc);

    }
}
