import java.util.Arrays;

public class CarList {
    public static void main(String[] args) {
        Car[] cars = {
                new Car("C01", "Toyota Camry", 35000),
                new Car("C02", "Honda Civic", 28000),
                new Car("C03", "Mercedes C300", 50000)
        };

        // Sử dụng Arrays.sort có sẵn của Java (hoặc hàm SelectionSort)
        Arrays.sort(cars);

        System.out.println("--- Danh sách xe sau khi sắp xếp theo giá ---");
        for (Car c : cars) {
            System.out.println(c);
        }
    }

}
