package Chuong2;

public class MyString {
    private String content;
    public MyString(){
        this.content="";
    }
    public MyString(String s){
        this.content=s;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    public int getDoDai(){
        return content.length();
    }
    public String xoaKhoangTrang(){
        return content.trim().replaceAll("\\s+", " ");

    }
    public int demSoTu(){
        String noiDung=xoaKhoangTrang();
        if(noiDung.isEmpty()){
            return 0;
        }
        return noiDung.split(" ").length;
    }
    public boolean laDoiXung(){
        String s=content.replaceAll("\\s+","").toLowerCase();
        int l=0, r=content.length()-1;
        while( l<r){
            if(s.charAt(l++)!=s.charAt(r--)){
                return false;
            }
        }
        return true;
    }
}
