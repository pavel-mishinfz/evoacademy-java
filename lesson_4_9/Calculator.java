package lesson_4_9;

public class Calculator {
    private Operation operation;

    public Calculator(Operation operation) {
        this.operation = operation;
    }

    public void calc(double a, double b) {
        System.out.println(operation.getResult(a, b));
    }
}
