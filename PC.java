import java.util.Scanner;

public class PC extends SanPham{
    protected String CPU;
    protected String GPU;
    protected int RAM;
    protected int dungLuongSSD;
    public String getCPU() {
        return CPU;
    }

    public void setCPU(String CPU) {
        this.CPU = CPU;
    }

    public String getGPU() {
        return GPU;
    }

    public void setGPU(String GPU) {
        this.GPU = GPU;
    }

    public int getRAM() {
        return RAM;
    }

    public void setRAM(int RAM) {
        this.RAM = RAM;
    }

    public int getDungLuongSSD() {
        return dungLuongSSD;
    }

    public void setDungLuongSSD(int dungluongSSD) {
        this.dungLuongSSD = dungluongSSD;
    }
    public PC(){
        super();
        this.CPU="";
        this.GPU="";
        this.RAM=0;
        this.dungLuongSSD=0;
    }
    public PC(String maSP, String tenSP, double donGia, int soLuong, String CPU, int RAM, int dungLuongSSD){
        super(maSP,tenSP,donGia,soLuong);
        this.CPU=CPU;
        this.RAM=RAM;
        this.dungLuongSSD=dungLuongSSD;
    }
    @Override
    public double tinhGiaBan(){
        return getDonGia();
    }
    public void hienthi(){
        super.hienthi();
        System.out.println("  [PC] CPU: " + CPU + " | RAM: " + RAM + "GB | SSD: " + dungLuongSSD + "GB");
    }
    public void nhap(Scanner sc){
        super.nhap(sc);
        System.out.println("Nhập thông tin CPU");
        CPU=sc.nextLine();
        System.out.println("Nhập thông tin GPU");
        GPU=sc.nextLine();
        System.out.println("Nhập thông tin RAM");
        RAM=Integer.parseInt(sc.nextLine());
        System.out.println("Nhập dung lượng SSD");
        dungLuongSSD=Integer.parseInt(sc.nextLine());
    }


}
