package lesson_3_4;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Введите имя первого пользователя");
        String name1 = in.nextLine();

        System.out.println("Введите возраст первого пользователя");
        Integer age1 = in.nextInt();
        in.nextLine();

        System.out.println("Введите имя второго пользователя");
        String name2 = in.nextLine();

        System.out.println("Введите возраст второго пользователя");
        Integer age2 = in.nextInt();

        User user1 = new User(name1, age1);
        User user2 = new User(name2, age2);

        if (user1.getAge() > user2.getAge()) {
            System.out.println(user2);
        }
        else {
            System.out.println(user1);
        }

        in.close();
    }
}
