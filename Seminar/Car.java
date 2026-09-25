import java.util.Arrays;

public class Car implements java.lang.Comparable<Car> {
    private String id;
    private String name;
    private double price;

    public Car() {
        id = null;
        name = null;
        price = 0;
    }

    public Car(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCar(Car car) {
        this.setId(car.getId());
        this.setName(car.getName());
        this.setPrice(car.getPrice());
    }

    @Override
    public int compareTo(Car o) {
        if (price > o.price)
            return 1;
        if (price < o.price)
            return -1;
        return 0;
    }

    @Override
    public String toString() {
        return "Car{" + "id='" + id + '\'' + ", name='"
                + name + '\'' + ", price=" + price + '}';
    }
}