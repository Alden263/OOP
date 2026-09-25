public class Circle implements java.lang.Comparable<Circle> {
    private double radius;

    public Circle() {
        this.radius = 1.0;
    }

    public Circle(double radius) {
        this.radius = radius;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public double getArea() {
        return Math.PI * this.radius * this.radius;
    }

    public double getCircumference() {
        return Math.PI * this.radius * 2.0;
    }

    @Override
    public int compareTo(Circle o) {
        if (radius > o.radius)
            return 1;
        if (radius < o.radius)
            return -1;
        return 0;
    }

    @Override
    public String toString() {
        return "Circle{" +
                "radius=" + radius +
                '}';
    }

    // Thuật toán Selection Sort theo slide bài giảng
    public static void selectionSort(Circle[] list) {
        int min;
        Circle temp;
        for (int index = 0; index < list.length - 1; index++) {
            min = index;
            for (int scan = index + 1; scan < list.length; scan++) {
                if (list[scan].compareTo(list[min]) < 0) {
                    min = scan;
                }
            }
            temp = list[min];
            list[min] = list[index];
            list[index] = temp;
        }
    }
}