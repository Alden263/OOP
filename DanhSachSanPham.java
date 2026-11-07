import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DanhSachSanPham implements ghiDocFile {
    protected int soLuong;
    private static final String FILE_SAN_PHAM = "SanPham.txt";

    public ArrayList<SanPham> getDsSanPham() {
        return dsSanPham;
    }

    public void setDsSanPham(ArrayList<SanPham> dsSanPham) {
        this.dsSanPham = dsSanPham;
    }

    private ArrayList <SanPham> dsSanPham;
    public DanhSachSanPham(int soLuong, ArrayList<SanPham> dsSanPham) {
        this.soLuong = soLuong;
        this.dsSanPham = dsSanPham;
    }
    public DanhSachSanPham(){
        this.soLuong=0;
        this.dsSanPham=new ArrayList<>();
    }
    public void themSanPham(SanPham sp){
        dsSanPham.add(sp);
        System.out.println("✔ Thêm sản phẩm thành công vào danh sách!");
    }
    public void hienThiDanhSach(){
        if(dsSanPham.isEmpty()){
            System.out.println("Danh sách sản phẩm hiện đang trống");
        }
        for(int i=0; i<= dsSanPham.size();i++){
            System.out.print((i + 1) + ". ");
            dsSanPham.get(i).hienthi(); // Gọi phương thức hiển thị của từng sản phẩm
            // Gọi phương thức abstract tinhGiaBan() đã được override ở lớp con
            System.out.println("   ➔ Giá bán thực tế: " + dsSanPham.get(i).tinhGiaBan());
            System.out.println("-----------------------------------------------------");
        }
    }
    public SanPham searchSanPham(String maSP){
        for(SanPham sp:dsSanPham){
            if(sp.getMaSP().equalsIgnoreCase(maSP)){
                return sp;
            }
        }
        return null;
    }
    public boolean xoaSanPham(String maSP){
        SanPham sp=searchSanPham(maSP);
        if(sp!=null){
            dsSanPham.remove(sp);
            return true;
        }
        return false;

    }
    public boolean suaSanPham(String maSP, Scanner sc) {
        SanPham sp = searchSanPham(maSP);
        if (sp == null) {
            System.out.println(" Không tìm thấy sản phẩm có mã: " + maSP);
            return false;
        }

        System.out.println("\n--- SỬA THÔNG TIN SẢN PHẨM " + maSP + " ---");
        System.out.print("Nhập tên mới (Enter để giữ nguyên [" + sp.getTenSP() + "]): ");
        String ten = sc.nextLine().trim();
        if (!ten.isEmpty()) sp.setTenSP(ten);

        System.out.print("Nhập đơn giá mới (Enter để giữ nguyên [" + sp.getDonGia() + "]): ");
        String giaStr = sc.nextLine().trim();
        if (!giaStr.isEmpty()) {
            try {
                sp.setDonGia(Double.parseDouble(giaStr));
            } catch (NumberFormatException ignored) {}
        }

        System.out.print("Nhập số lượng mới (Enter để giữ nguyên [" + sp.getSoLuong() + "]): ");
        String slStr = sc.nextLine().trim();
        if (!slStr.isEmpty()) {
            try {
                sp.setSoLuong(Integer.parseInt(slStr));
            } catch (NumberFormatException ignored) {}
        }

        System.out.println("Cập nhật thông tin sản phẩm thành công!");
        return true;
    }
    @Override
    public void ghiFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_SAN_PHAM;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path))) {
            for (SanPham sp : dsSanPham) {
                // Định dạng dòng ghi file tùy thuộc vào loại đối tượng (Laptop, PC, LinhKien)
                // Giả sử ta lưu theo định dạng: LOAI;MaSP;TenSP;DonGia;SoLuong;ThuocTinhRieng1;ThuocTinhRieng2...
                String line = "";
                if (sp instanceof Laptop) {
                    Laptop lt = (Laptop) sp;
                    // Format: LAPTOP;ma;ten;gia;sl;ram;baohanh
                    line = "LAPTOP;" + lt.getMaSP() + ";" + lt.getTenSP() + ";" + lt.getDonGia() + ";" + lt.getSoLuong()
                            + ";" + lt.getRAM() + ";" ;
                } else if (sp instanceof PC) {
                    PC pc = (PC) sp;
                    // Format: PC;ma;ten;gia;sl;cpu;gpu
                    line = "PC;" + pc.getMaSP() + ";" + pc.getTenSP() + ";" + pc.getDonGia() + ";" + pc.getSoLuong()
                            + ";" + pc.getCPU() + ";" + pc.getGPU();
                } else if (sp instanceof LinhKien) {
                    LinhKien lk = (LinhKien) sp;
                    // Format: LINHKIEN;ma;ten;gia;sl;thuonghieu
                    line = "LINHKIEN;" + lk.getMaSP() + ";" + lk.getTenSP() + ";" + lk.getDonGia() + ";" + lk.getSoLuong()
                            + ";" + lk.getHangSanXuat();
                }
                pw.println(line);
            }
            System.out.println(" Ghi danh sách sản phẩm vào file " + path + " thành công!");
        } catch (IOException e) {
            System.out.println(" Lỗi khi ghi file: " + e.getMessage());
        }
    }


    // 7. ĐỌC FILE (Cài đặt từ Interface IFileIO)

    @Override
    public void docFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_SAN_PHAM;
        File f = new File(path);
        if (!f.exists()) {
            System.out.println(" File " + path + " không tồn tại!");
            return;
        }

        // Xóa danh sách cũ trước khi nạp dữ liệu từ file
        dsSanPham.clear();

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(";");
                if (p.length < 5) continue;

                String loai = p[0];
                String ma = p[1];
                String ten = p[2];
                double gia = Double.parseDouble(p[3]);
                int sl = Integer.parseInt(p[4]);

                // 1. LAPTOP: [LAPTOP, ma, ten, gia, sl, cpu, ram, ssd] -> cần ít nhất 8 phần tử (index từ 0 -> 7)
                if (loai.equalsIgnoreCase("LAPTOP") && p.length >= 8) {
                    String cpu = p[5];
                    int ram = Integer.parseInt(p[6]);
                    int ssd = Integer.parseInt(p[7]);
                    dsSanPham.add(new Laptop(ma, ten, gia, sl, cpu, ram, ssd));
                }
                // 2. PC: [PC, ma, ten, gia, sl, cpu, gpu] -> cần ít nhất 7 phần tử (index từ 0 -> 6)
                else if (loai.equalsIgnoreCase("PC") && p.length >= 9) {
                    String cpu = p[5];
                    String gpu = p[6];
                    int ram = Integer.parseInt(p[7]);
                    int ssd = Integer.parseInt(p[8]);
                    dsSanPham.add(new PC(ma, ten, gia, sl, cpu, gpu, ram, ssd));
                }
                // 3. LINHKIEN: [LINHKIEN, ma, ten, gia, sl, thuongHieu] -> cần ít nhất 6 phần tử (index từ 0 -> 5)
                else if (loai.equalsIgnoreCase("LINHKIEN") && p.length >= 7) {
                    String loaiLK = p[5];
                    String hangSX = p[6];
                    dsSanPham.add(new LinhKien(ma, ten, gia, sl, loaiLK, hangSX));
                }
            }
            System.out.println(" Đọc file " + path + " thành công! Tổng số sản phẩm nạp: " + dsSanPham.size());
        } catch (Exception e) {
            System.out.println(" Lỗi khi đọc file: " + e.getMessage());
        }
    }



}
