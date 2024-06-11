import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CircleArea {
    public static void main(String[] args) {
        double radius = 3;
        System.out.println("Area circle for radius " + radius + ": " + calcAreaCircle(radius));
        float num1 = 2.15f;
        float num2 = 1.10f;
        System.out.println(num1-num2);
        BigDecimal numBigDecimal1 = new BigDecimal("2.15");
        BigDecimal numBigDecimal2 = new BigDecimal("1.10");
        System.out.println(numBigDecimal1.subtract(numBigDecimal2));
        NumberFormat moneyFormatter = NumberFormat.getCurrencyInstance();
        System.out.println(moneyFormatter.format(num1-num2));
        NumberFormat moneyFormatterUS = NumberFormat.getCurrencyInstance(Locale.UK);
        System.out.println(moneyFormatterUS.format(num1-num2));
    }

    /**
     * This method calculate the area of a circle
     * @param radius
     * @return
     */
    public static double calcAreaCircle(double radius) {
        return Math.PI * Math.pow(radius, 2);
    }
}
