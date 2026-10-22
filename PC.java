public class PC extends SanPham{
    protected String CPU;
    protected String GPU;
    protected int RAM;
    protected int dungluongSSD;
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

    public int getDungluongSSD() {
        return dungluongSSD;
    }

    public void setDungluongSSD(int dungluongSSD) {
        this.dungluongSSD = dungluongSSD;
    }
    @Override
    public double tinhGiaBan(){
        return getDonGia();
    }



}
