public class CircleList {
    public static void main(String[] args) {
        Circle[] circles = {
                new Circle(7.5),
                new Circle(2.1),
                new Circle(5.0),
                new Circle(1.2)
        };

        // Tiến hành sắp xếp danh sách hình tròn theo bán kính tăng dần
        Circle.selectionSort(circles);

        System.out.println("--- Danh sách hình tròn sau khi sắp xếp ---");
        for (Circle c : circles) {
            System.out.println(c);
        }
    }
}