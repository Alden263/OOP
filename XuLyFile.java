import java.io.*;
public class XuLyFile {
    // Ghi nội dung vào file
    public static void ghiFile(String tenFile, String noiDung) {
        try {
            BufferedWriter bw = new BufferedWriter(
                    new FileWriter(tenFile, java.nio.charset.StandardCharsets.UTF_8)
            );
            bw.write(noiDung);
            bw.close();
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
    }
    // Đọc nội dung từ file
    public static String docFile(String tenFile) {
        String noiDung = "";
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader(tenFile, java.nio.charset.StandardCharsets.UTF_8)
            );
            String line;
            while ((line = br.readLine()) != null) {
                noiDung += line + "\n";
            }
            br.close();
        } catch (FileNotFoundException e) {
            return "";
        } catch (Exception e) {
            throw new IllegalStateException("Không thể đọc/ghi dữ liệu", e);
        }
        return noiDung;
    }
}
