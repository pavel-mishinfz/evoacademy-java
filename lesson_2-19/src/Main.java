import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите 3 числа");
        int a = in.nextInt();
        int b = in.nextInt();
        int c = in.nextInt();

        if(a % 5 == 0 && b % 5 == 0 && c % 5 == 0) {
            System.out.printf("a=%d, b=%d, c=%d\n", a, b, c);
        }
        else if(a % 5 == 0 && b % 5 == 0) {
            System.out.printf("a=%d, b=%d\n", a, b);
        }
        else if(a % 5 == 0 && c % 5 == 0) {
            System.out.printf("a=%d, c=%d\n", a, c);
        }
        else if(b % 5 == 0 && c % 5 == 0) {
            System.out.printf("b=%d, c=%d\n", b, c);
        }
        else if(a % 5 == 0) {
            System.out.printf("a=%d\n", a);
        }
        else if(b % 5 == 0) {
            System.out.printf("b=%d\n", b);
        }
        else if(c % 5 == 0) {
            System.out.printf("c=%d\n", c);
        }
        else {
            System.out.println("нет значений, кратных 5");
        }

        System.out.printf("Результат целочисленного деления a на b: %d\n", a / b);
        double resultDbl = (double)a / b;
        System.out.printf("Результат деления a на b: %.12f\n", resultDbl);
        System.out.printf("Результат деления a на b с округлением в большую сторону: %d\n", (int)Math.ceil(resultDbl));
        System.out.printf("Результат деления a на b с округлением в меньшую сторону: %d\n", (int)Math.floor(resultDbl));
        System.out.printf("Результат деления a на b с математическим округлением: %d\n", (int)Math.round(resultDbl));
        System.out.printf("Остаток от деления b на c: %d\n", b % c);
        System.out.printf("Наименьшее значение из a и b: %d\n",Math.min(a, b));
        System.out.printf("Наибольшее значение из b и c: %d\n", Math.max(b, c));

        in.close();
    }
}