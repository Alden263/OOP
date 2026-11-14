import java.io.*;
public class XuLyFile {
    // Ghi nội dung vào file
    public static void ghiFile(String tenFile, String noiDung) {
        try {
            BufferedWriter bw = new BufferedWriter(
                new FileWriter(tenFile)
            );
            bw.write(noiDung);
            bw.close();
        } catch (IOException e) {
            System.out.println("Loi ghi file: " + e.getMessage());
        }
    }
    // Đọc nội dung từ file
    public static String docFile(String tenFile) {
        String noiDung = "";
        try {
            BufferedReader br = new BufferedReader(
                new FileReader(tenFile)
            );
            String line;
            while ((line = br.readLine()) != null) {
                noiDung += line + "\n";
            }
            br.close();
        } catch (FileNotFoundException e) {
            return "";
        } catch (IOException e) {
            System.out.println("Loi doc file: " + e.getMessage());
        }
        return noiDung;
    }
}
