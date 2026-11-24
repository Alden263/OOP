import java.io.*;
import java.util.ArrayList;

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
    @Override
    public void ghiFile(String duongDan) {
        String path = (duongDan != null && !duongDan.isEmpty()) ? duongDan : FILE_SAN_PHAM;
        try (PrintWriter pw = new PrintWriter(new FileWriter(path, java.nio.charset.StandardCharsets.UTF_8))) {
            for (SanPham sp : dsSanPham) {
                // Định dạng dòng ghi file tùy thuộc vào loại đối tượng (Laptop, PC, LinhKien)
                // Giả sử ta lưu theo định dạng: LOAI;MaSP;TenSP;DonGia;SoLuong;ThuocTinhRieng1;ThuocTinhRieng2...
                String line = "";
                if (sp instanceof Laptop) {
                    Laptop lt = (Laptop) sp;
                    // Format: LAPTOP;ma;ten;gia;sl;ram;baohanh
                    line = "LAPTOP;" + lt.getMaSP() + ";" + lt.getTenSP() + ";" + lt.getDonGia() + ";" + lt.getSoLuong()
                            + ";" + lt.getCPU() + ";" + lt.getRAM() + ";" + lt.getDungLuongSSD();
                } else if (sp instanceof PC) {
                    PC pc = (PC) sp;
                    // Format: PC;ma;ten;gia;sl;cpu;gpu
                    line = "PC;" + pc.getMaSP() + ";" + pc.getTenSP() + ";" + pc.getDonGia() + ";" + pc.getSoLuong()
                            + ";" + pc.getCPU() + ";" + pc.getGPU() + ";" + pc.getRAM() + ";" + pc.getDungLuongSSD();
                } else if (sp instanceof LinhKien) {
                    LinhKien lk = (LinhKien) sp;
                    // Format: LINHKIEN;ma;ten;gia;sl;thuonghieu
                    line = "LINHKIEN;" + lk.getMaSP() + ";" + lk.getTenSP() + ";" + lk.getDonGia() + ";" + lk.getSoLuong()
                            + ";" + lk.getLoaiLinhKien() + ";" + lk.getHangSanXuat();
                }
                pw.println(line);
            }
            if (pw.checkError()) throw new IOException("Không thể ghi đầy đủ dữ liệu");
            System.out.println(" Ghi danh sách sản phẩm vào file " + path + " thành công!");
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
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

        try (BufferedReader br = new BufferedReader(new FileReader(f, java.nio.charset.StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] p = line.split(";", -1);
                if (p.length < 5) throw new IOException("Thiếu cột sản phẩm: " + line);

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
                else { throw new IOException("Sai loại hoặc thiếu thuộc tính sản phẩm: " + line); }
            }
            System.out.println(" Đọc file " + path + " thành công! Tổng số sản phẩm nạp: " + dsSanPham.size());
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
    }



}
