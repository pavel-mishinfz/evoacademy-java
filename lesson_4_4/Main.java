package lesson_4_4;

public class Main {
    public static void main(String[] args) {
        Student student = new Student();
        StudentWithClock studentWithClock = new StudentWithClock(student);
        studentWithClock.learn();
    }
}
