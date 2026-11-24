public abstract class SanPham {
    protected String maSP;
    protected String tenSP;
    protected double donGia;
    protected int soLuong;
    private static int demLT = 0;
    private static int demPC = 0;
    private static int demLK = 0;
    public static String sinhMa(String loai) {
        if (loai.equalsIgnoreCase("LT")) {
            return String.format("LT%03d", ++demLT);
        } else if (loai.equalsIgnoreCase("PC")) {
            return String.format("PC%03d", ++demPC);
        } else if (loai.equalsIgnoreCase("LK")) {
            return String.format("LK%03d", ++demLK);
        }
        return String.format("SP%03d", ++demLT);
    }

    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public String getTenSP() {
        return tenSP;
    }

    public void setTenSP(String tenSP) {
        this.tenSP = tenSP;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        this.donGia = donGia;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public SanPham(){
        this.maSP="";
        this.tenSP="";
        this.donGia=0;
        this.soLuong=0;
    }
    public SanPham(String maSP, String tenSP, double donGia, int soLuong){
        this.maSP=maSP;
        this.tenSP=tenSP;
        this.donGia=donGia;
        this.soLuong=soLuong;
    }
    public abstract double tinhGiaBan();





}
