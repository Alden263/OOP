package Chuong2.BaiTapChuong2;

import java.util.Scanner;

public class MenuChinh {
    public static void main (String [] args){
        Scanner sc=new Scanner(System.in);
        int choice=-1;
        do{
            System.out.println("1.Quản lý điểm ");
            System.out.println("2.Quản lý hình chữ nhật");
            System.out.println("3.Quản lý hình tròn");
            System.out.println("4.Quản lý 1 sinh viên");
            System.out.println("5.Kiểm tra số nguyên");
            System.out.println("6.Quản lý mảng số nguyên");
            System.out.println("7.Xử lý chuỗi ký tự");
            System.out.println("8.Quản lý danh sách sinh viên");
            System.out.println("0.Thoát chương trình");
            System.out.println("Mời bạn lựa chọn (0-8) ");
            try{
                choice=Integer.parseInt(sc.nextLine());
            }catch(Exception e){
                System.out.println("Vui lòng nhập so nguyên hợp le");
                continue;
            }
            switch(choice){
                case 1:
                    testDiem(sc);
                    break;
                case 2:
                    testHinhChuNhat(sc);
                    break;
                case 3:
                    testHinhTron(sc);
                    break;
                case 4:
                    testSinhVien(sc);
                    break;
                case 5:
                    testSoNguyen(sc);
                    break;
                case 6:
                    testMangSoNguyen(sc);
                    break;
                case 7:
                    testChuoiKyTu(sc);
                    break;
                case 8:
                    testDanhSachSinhVien(sc);
                    break;
                case 0:
                    System.out.println("Cảm ơn bạn đã sử dụng");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ");
            }

        }while(choice!=0);



    }
    public static void testDiem(Scanner sc){
        Diem A=new Diem();
        A.nhap(sc);
        System.out.println("Điểm vừa nhập là ");
        A.xuat();
        System.out.println("Nhập độ di chuyển theo x");
        double dx=sc.nextDouble();
        System.out.println("Nhập độ di chuyên theo y");
        double dy=sc.nextDouble();
        A.diChuyen(dx,dy);
        System.out.println("Tọa độ điểm sau khi di chuyển là ");
        A.xuat();
    }
    public static void testHinhChuNhat(Scanner sc){
        HinhChuNhat hcn=new HinhChuNhat();
        hcn.nhap(sc);
        sc.nextLine();
        System.out.println("Thông tin hình chữ nhật");
        hcn.xuat();
    }
    public static void testHinhTron(Scanner sc){
        HinhTron ht=new HinhTron();
        ht.nhap(sc);
        sc.nextLine();
        System.out.println("Thông tin hình tròn ");
        ht.xuat();
    }
    public static void testSinhVien(Scanner sc){
        SinhVien sv=new SinhVien();
        sv.nhap(sc);
        System.out.println("Thông tin sinh viên vừa nhập");
        sv.xuat();
    }
    public static void testSoNguyen(Scanner sc){
        System.out.println("Nhập vào một số nguyên");
        int value=Integer.parseInt(sc.nextLine());
        SoNguyen sn=new SoNguyen(value);
        System.out.println("Số vừa nhập: " + sn.getValue());
        System.out.println("- Có phải số chẵn: " + (sn.laSoChan() ? "Có" : "Không (số lẻ)"));
        System.out.println("- Có phải số nguyên tố: " + (sn.laSNT() ? "Có" : "Không"));
        System.out.println("- Có phải số hoàn hảo: " + (sn.laSHH() ? "Có" : "Không"));
    }
    private static void testMangSoNguyen(Scanner sc) {
        MangSoNguyen mang = new MangSoNguyen();
        mang.nhap(sc);
        sc.nextLine();

        System.out.print("Mảng vừa nhập: ");
        mang.xuat();
        System.out.println("Tổng các phần tử: " + mang.tinhTong());



        System.out.print("Nhập vị trí index muốn xóa: ");
        int k = Integer.parseInt(sc.nextLine());
        mang.xoaViTri(k);
        System.out.print("Mảng sau khi xóa: ");
        mang.xuat();
    }


    private static void testChuoiKyTu(Scanner sc) {
        System.out.print("Nhập chuỗi văn bản: ");
        String str = sc.nextLine();
        MyString myStr = new MyString(str);

        System.out.println("Độ dài chuỗi gốc: " + myStr.getDoDai());
        System.out.println(myStr.xoaKhoangTrang());
        System.out.println("Số lượng từ: " + myStr.demSoTu());
        System.out.println( myStr.laDoiXung());
    }


    private static void testDanhSachSinhVien(Scanner sc) {
        DanhSachSinhVien dssv = new DanhSachSinhVien(50);
        dssv.themSinhVien(new SinhVien("SV01", "Nguyen Van An", "CNTT1", 8.0, 7.5, 9.0));
        dssv.themSinhVien(new SinhVien("SV02", "Tran Thi Binh", "CNTT2", 6.0, 5.5, 7.0));

        int subChon = -1;
        do {
            System.out.println("1. In danh sách");
            System.out.println("2. Nhập thêm 1 sinh viên mới");
            System.out.println("3. Xóa sinh viên theo mã");
            System.out.println("4. Tìm kiếm theo mã");
            System.out.println("5. Sắp xếp theo ĐTB giảm dần");
            System.out.println("0. Quay lại Menu chính");
            System.out.print("Chọn thao tác (0-5): ");

            try {
                subChon = Integer.parseInt(sc.nextLine());
            } catch (Exception e) {
                System.out.println("Vui lòng nhập số!");
                continue;
            }

            switch (subChon) {
                case 1:
                    dssv.xuatDanhSach();
                    break;
                case 2:
                    dssv.nhapSinhVien(sc);
                    break;
                case 3:
                    System.out.print("Nhập mã sinh viên cần xóa: ");
                    String maXoa = sc.nextLine();
                    dssv.xoaSinhVien(maXoa);
                    break;
                case 4:
                    System.out.print("Nhập mã sinh viên cần tìm: ");
                    String maTim = sc.nextLine();
                    SinhVien timThay = dssv.timkiemTheoMa(maTim);
                    if (timThay != null) {
                        System.out.println("Tìm thấy sinh viên:");
                        timThay.xuat();
                    } else {
                        System.out.println("Không tìm thấy mã " + maTim);
                    }
                    break;
                case 5:
                    dssv.sapXepDiemTB();
                    dssv.xuatDanhSach();
                    break;
                case 0:
                    System.out.println("Đang quay lại Menu chính...");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ!");
            }
        } while (subChon != 0);
    }
}
