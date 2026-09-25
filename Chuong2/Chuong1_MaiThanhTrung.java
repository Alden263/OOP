import java.util.Arrays;
import java.util.Scanner;

public class Chuong1_MaiThanhTrung {
    public static void main( String[] args){
        bai1();
        bai2();
        bai3();
        bai4();
        bai5();
        bai6();
        bai7();
        bai8();
        bai9();


    }
    public static void bai1(){
        Scanner sc =new Scanner(System.in);
        System.out.println("Nhập so nguyen thứ nhất ");
        float a= sc.nextFloat();
        System.out.println("Nhập số nguyên thứ hai ");
        float b=sc.nextFloat();
        float tong=a+b;
        float hieu=a-b;
        float tich=a*b;
        if (b<0){
            System.out.println("Lỗi không chia được");
        }
        float thuong=a/b;
        System.out.println(tong);
        System.out.println(hieu);
        System.out.println(tich);
        System.out.println(thuong);


    }
    public static void bai2(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhập vào chiều dài hình chữ nhật ");
        float cd= sc.nextFloat();
        System.out.println("Nhập vào chiều rộng hình chữ nhật ");
        float cr= sc.nextFloat();
        float chuvi=(cd+cr)/2;
        float dientich=cd*cr;
        System.out.println(chuvi);
        System.out.println(dientich);
    }
    public static void bai3(){
        Scanner sc= new Scanner(System.in);
        System.out.println("Nhập vào bans kính hình tròn ");
        double r=sc.nextDouble();
        double chuvi= r*2*(3.14);
        double dientich=r*r*3.14;
        System.out.println(chuvi);
        System.out.println(dientich);

    }
    public static void bai4(){
        Scanner sc= new Scanner(System.in);
        System.out.println("Nhập vào số nguyên N ");
        int n =sc.nextInt();
        if (n%2 == 0){
            System.out.println("Số"+n+"là số chẵn");
        }else{
            System.out.println("So"+n+" là số lẻ");
        }
    }
    public static void bai5(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhập vào số nguyên N ");
        int n=sc.nextInt();
        if (n==0){
            System.out.println("Số"+n+"la zero");
        }else if(n<0){
            System.out.println("Số"+n+"là số âm");
        }else {
            System.out.println("Số" + n + "là số dương");
        }

    }
    public static void bai6(){
        Scanner sc= new Scanner(System.in);
        System.out.println("Nhập vào tự nhiên N");
        int n=sc.nextInt();
       if(laSNT(n)){
           System.out.println("Số"+n+" là số nguyên tố");
       }else{
           System.out.println("Số"+n+" không là số nguyên tố");
       }
    }
    public static boolean laSNT(int n){
        boolean isPrime= false;
        if (n<2){
            return false;
        }else{
            for (int i =2;i*i<=n;i++){
                if(n%i ==0){
                    return false;

                }

            }
            return true;
        }
    }
    public static void bai7(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhập số tự nhiên N ");
        int n=sc.nextInt();
        double tong=0;
        double tongchan=0;
        double tongle=0;
        double tongSNT=0;
        int count=0;
        int num=1;
        for(int i=1;i<=n;i++){
            System.out.println("Các số bé hơn N là "+i);
            tong+=i;
        }
        System.out.println("Tổng các số bé hơn N là "+tong);
        for (int j=1;j<=n;j++){
            if(j%2==0){
                System.out.println("Các số chẵn bé hơn N là "+j);
                tongchan+=j;
            }
        }
        System.out.println("Tổng các số chẵn bé hơn N là "+tongchan);
        for(int k=1;k<=n;k++){
            if(k%2!=0){
                System.out.println("Cac số lẻ bé hơn N là "+k);
                tongle+=k;
            }
        }
        System.out.println("Tong các số lẻ bé hơn N là "+tongle);
        for(int r=1;r<=n;r++){
            if(laSNT(r)){
                System.out.println("Các số nguyên tố bé hơn N là "+r);
                tongSNT+=r;
            }
        }
        System.out.println("Tổng cac so nguyen tố bé hơn N là "+tongSNT);
        while (count<n){
            if(laSNT(num)){
                System.out.println("N số nguyên tố đầu tiên là "+num);
                count++;
            }
            num++;
        }


    }
    public static void bai8(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhập vào số lượng phần tu của mảng " );
        int n= sc.nextInt();
        int [] a=new int[n];
        for (int i=0;i<n;i++){
            System.out.println("Nhập vào phần tử a["+i+"]=" );
            a[i]= sc.nextInt();
        }
        System.out.println("Mảng vừa nhập là ");
        inMang(a);
        int tong=0;
        for(int i :a){
            tong+=i;
        }
        System.out.println("Tổng của mảng là "+tong);
        int tongChan=0;
        for(int i:a){
            if(i%2==0){
                tongChan+=i;
            }
        }
        System.out.println("Tổng các số chẵn trong mảng là "+tongChan);
        int tongLe=0;
        for(int i:a){
            if(i%2!=0){
                tongLe+=i;
            }
        }
        System.out.println("Tổng các số lẻ trong mảng là "+tongLe);
        int tongSNT=0;
        for (int i :a){
            if(laSNT(i)){
                tongSNT+=i;
            }
        }
        System.out.println("Tổng các số nguyên tố trong mảng là "+tongSNT);
        System.out.println("Nhập giá trị muốn thêm ");
        int value = sc.nextInt();
        System.out.println("Nhập vị trí muốn thêm vào ");
        int position=sc.nextInt();
        if(position>=0 && position<=n){
            int [] newa=new int[n+1];
            for(int i=0, j=0; i<=n;i++){
                if (i==position){
                    newa[i]=value;
                }else{
                    newa[i]=a[j++];
                }
            }
            a=newa;
            n++;
            System.out.println("Mảng sau khi thêm ");
            inMang(a);

        }else{
            System.out.println("Vị trí khong hợp lệ");
        }
        System.out.println("Nhập vị trí muốn xóa ");
        int k=sc.nextInt();
        if(k>= 0&& k<n){
            int [] newarr=new int[n-1];
            for(int i=0,j=0;i<n;i++){
                if(i!=k){
                    newarr[j++]=a[i];
                }
            }
            a=newarr;
            n--;
            System.out.println("Mảng sau khi xóa là ");
            inMang(a);
        }else{
            System.out.println("Vị trí K không hợp lệ");
        }
        System.out.println("Nhập giá trị muốn tìm ");
        int x=sc.nextInt();
        boolean timThay=false;
        System.out.println("Kết quả tìm được là ");
        for(int i=0;i<n;i++){
            if(x==a[i]){
                if(!timThay){
                    System.out.print("Số "+x+" tìm thấy ở vị trí ");

                }
                System.out.print(i+" ");
                timThay=true;
            }
        }
        if (!timThay) {
            System.out.println("Không tìm thấy " + x + " trong mảng.");
        } else {
            System.out.println();
        }
    }
    public static void inMang(int [] arr){
        System.out.println("Mảng của bạn là "+ Arrays.toString(arr));

    }
    public static void bai9(){
        Scanner sc=new Scanner(System.in);
        System.out.println("Nhập vào chuỗi s ");
        String s= sc.nextLine();
        System.out.println("Độ dài của chuoi là "+s.length());
        String strXoaKhoangTrang=xoaKhoangTrang(s);
        System.out.println("Chuỗi s sau khi xóa khoảng trắng là "+strXoaKhoangTrang);
        System.out.println("Các từ trong chuỗi là ");
        if(strXoaKhoangTrang.isEmpty()){
            System.out.println("Số từ bằng 0");
        }else{
            String [] word= strXoaKhoangTrang.split(" ");
            System.out.println(" Số lượng từ "+word.length);
            for(int i=0;i < word.length;i++ ){
                System.out.println("Từ "+(i+1)+": "+ word[i]);
            }
        }
        System.out.println("Nhập vào số k ");
        int k= sc.nextInt();
        int len=s.length();
        if(k<0){
            System.out.println("Gia trị k không hợp lệ ");

        } else if (k>len) {
            System.out.println("k lon hơn độ dài chuỗi");


        }else{
            String trai=s.substring(0,k);
            String phai=s.substring(len-k);
            System.out.println(" "+k+" kí tự bên trái là "+trai);
            System.out.println(" "+k+" kí tự bên phải là "+phai);
        }
        System.out.println("Nhập vào vị trí ");
        int position=sc.nextInt();
        System.out.println("Nhập số luượng kí tự muon lấy ");
        int quantity=sc.nextInt();
        if(position<0 || position>len || quantity<0){
            System.out.println("Số lượng hoặc vị trí không hop lệ");
        }else{
            int endpoint=Math.min(position+quantity,len);
            String result=s.substring(position,endpoint);
            System.out.println("Số lượng từ lay được là "+result);
            if(position+quantity>len){
                System.out.println("So lượng từ còn lai là "+(len-position));
            }
        }


    }
    public static String xoaKhoangTrang(String s){
        if(s==null){
            return " ";

        }
        return s.trim().replaceAll("\\s+"," ");
    }
}
