public class Laptop  extends SanPham{
    protected String CPU;
    protected int RAM;
    protected int dungLuongSSD;

    public String getCPU() {
        return CPU;
    }

    public void setCPU(String CPU) {
        this.CPU = CPU;
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

    public void setDungLuongSSD(int dungLuongSSD) {
        this.dungLuongSSD = dungLuongSSD;
    }
    public Laptop(){
        super();
        this.CPU="";
        this.RAM=0;
        this.dungLuongSSD=0;
    }
    public Laptop(String maSP, String tenSP, double donGia, int soLuong, String CPU, int RAM, int dungLuongSSD){
        super(maSP,tenSP,donGia,soLuong);
        this.CPU=CPU;
        this.RAM=RAM;
        this.dungLuongSSD=dungLuongSSD;
    }
    @Override
    public double tinhGiaBan(){
        return getDonGia();
    }
}
