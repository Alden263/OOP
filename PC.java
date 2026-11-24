
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
    public PC(String maSP, String tenSP, double donGia, int soLuong, String CPU,String GPU, int RAM, int dungLuongSSD){
        super(maSP,tenSP,donGia,soLuong);
        this.CPU=CPU;
        this.GPU=GPU;
        this.RAM=RAM;
        this.dungLuongSSD=dungLuongSSD;
    }
    @Override
    public double tinhGiaBan(){
        return getDonGia();
    }

}
