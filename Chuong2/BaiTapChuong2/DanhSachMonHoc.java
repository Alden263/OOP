package Chuong2.BaiTapChuong2;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DanhSachMonHoc {
    private ArrayList <MonHoc> dsMonHoc;
    public DanhSachMonHoc(){
        this.dsMonHoc=new ArrayList<>();

    }
    public void themMonHoc(MonHoc mh){
        dsMonHoc.add(mh);
        System.out.println("Đã thêm môn học thành công");
    }
    public void hienThiDanhSach(){
        if(dsMonHoc.isEmpty()){
            System.out.println("Danh sách môn học đang trống");
            return;
        }
        System.out.println("DANH SÁCH MÔN HỌC");
        for(MonHoc mh: dsMonHoc){
            double dtb = (mh.diemTrungBinh > 0) ? mh.diemTrungBinh : mh.tinhDTB();
            System.out.printf("Mã MH: %-8s | Tên MH: %-20s | Số Tín Chỉ: %-2d | ĐTB: %-5.2f | Điểm Chữ: %s\n",
                    mh.maMonHoc,
                    mh.tenMonHoc,
                    mh.soTinChi,
                    dtb,

                    mh.quyDoiDiemChu());
        }
    }
    public MonHoc timKiemMonHoc(String maMH){
        for(MonHoc mh: dsMonHoc){
            if(mh.maMonHoc.equalsIgnoreCase(maMH)){
                return mh;
            }
        }
        return null;
    }
    public void ghiFile(String filename){
        try(BufferedWriter bw=new BufferedWriter(new FileWriter(filename))){
            for(MonHoc mh:dsMonHoc){
                String loai="";
                if(mh instanceof LyThuyet){
                    loai="Lý Thuyết";
                }else if(mh instanceof ThucHanh){
                    loai="Thực Hành";
                }else if(mh instanceof DoAn){
                    loai="Đồ Án";
                }
                bw.write(loai + ";" + mh.maMonHoc + ";" + mh.tenMonHoc + ";" + mh.soTinChi + ";" + mh.tinhDTB());
                bw.newLine();
            }
            System.out.println("Ghi file thành công");


        }catch (IOException e){
            System.out.println("Lỗi ghi File"+e.getMessage());
        }
    }
    public void docFile(String filename){
        File file=new File(filename);
        if(!file.exists()){
            System.out.println("File không tồn tại");
            return;
        }
        try(BufferedReader br=new BufferedReader(new FileReader(filename))){
            String line;
            dsMonHoc.clear();
            System.out.println("Dữ liệu đọc được từ file");
            while((line=br.readLine())!=null){
                System.out.println(line);
                String[] part=line.split(";");
                if(part.length>=5) {
                    String loai = part[0];
                    String maMonHoc = part[1];
                    String tenMonHoc = part[2];
                    int soTinChi = Integer.parseInt(part[3]);
                    double diemTrungBinh=Double.parseDouble(part[4]);
                    MonHoc mh = null;
                    if (loai.equalsIgnoreCase("Lý Thuyết")) {
                        mh = new LyThuyet();
                    } else if (loai.equalsIgnoreCase("Thực Hành")) {
                        mh = new ThucHanh();
                    } else if (loai.equalsIgnoreCase("Đồ Án")) {
                        mh = new DoAn();
                    }

                    if (mh != null) {
                        mh.maMonHoc = maMonHoc;
                        mh.tenMonHoc = tenMonHoc;
                        mh.soTinChi = soTinChi;
                        mh.diemTrungBinh=diemTrungBinh;
                        // Nạp vào mảng động quản lý
                        dsMonHoc.add(mh);
                    }
                }
            }
        }catch (IOException e){
            System.out.println("Lỗi đọc file "+e.getMessage());
        }
    }
    public void nhapThemMonHoc(Scanner sc){
        System.out.println("\n--- CHỌN LOẠI MÔN HỌC ---");
        System.out.println("1. Môn Lý Thuyết");
        System.out.println("2. Môn Thực Hành");
        System.out.println("3. Môn Đồ Án");
        System.out.print("Nhập lựa chọn loại môn (1-3): ");
        int choice;
        try{
            choice=Integer.parseInt(sc.nextLine());

        }catch (NumberFormatException e){
            System.out.println("Vui lòng nhập đúng định dạng"+e.getMessage());
            return;
        }
        MonHoc mh=null;
        switch(choice){
            case 1:
                mh=new LyThuyet();
                break;
            case 2:
                mh=new ThucHanh();
                break;
            case 3:
                mh=new DoAn();
                break;
            default:
                System.out.println("Lựa chọn không hợp lệ");
                return;

        }
        if(mh != null){
            mh.nhap(sc);
            themMonHoc(mh);
        }
    }
    public void menu() {
        Scanner scanner = new Scanner(System.in);
        int luaChon = 0;

        do {
            System.out.println("\n================ QUẢN LÝ MÔN HỌC ================");
            System.out.println("1. Thêm môn học mới");
            System.out.println("2. Hiển thị danh sách môn học");
            System.out.println("3. Tìm kiếm môn học theo mã");
            System.out.println("4. Ghi danh sách ra file text");
            System.out.println("5. Đọc danh sách từ file text");
            System.out.println("0. Thoát chương trình");
            System.out.print("👉 Nhập lựa chọn của bạn (0-5): ");

            try {
                luaChon = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("⚠️ Vui lòng chỉ nhập số từ 0 đến 5!");
                continue;
            }

            switch (luaChon) {
                case 1:
                    nhapThemMonHoc(scanner);
                    break;
                case 2:
                    hienThiDanhSach();
                    break;
                case 3:
                    System.out.print("Nhập mã môn học cần tìm: ");
                    String ma = scanner.nextLine();
                    timKiemMonHoc(ma);
                    break;
                case 4:
                    System.out.print("Nhập tên file muốn ghi (ví dụ: dsmonhoc.txt): ");
                    String fnWrite = scanner.nextLine();
                    ghiFile(fnWrite);
                    break;
                case 5:
                    System.out.print("Nhập tên file muốn đọc (ví dụ: dsmonhoc.txt): ");
                    String fnRead = scanner.nextLine();
                    docFile(fnRead);
                    break;
                case 0:
                    System.out.println("👋 Đang thoát chương trình. Tạm biệt!");
                    break;
                default:
                    System.out.println("⚠️ Lựa chọn không hợp lệ! Vui lòng chọn lại.");
            }
        } while (luaChon != 0);
    }
}
