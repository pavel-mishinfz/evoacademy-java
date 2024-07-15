package lesson_4_5;

import java.util.stream.LongStream;

public class Main {
    public long getArithmeticProgressionSum(int a, int b) {
        return LongStream.range(a, b).sum();
    }

    public static void main(String[] args) {
        Main main = new Main();
        System.out.println(main.getArithmeticProgressionSum(10000000, 1000000000));
    }
}
