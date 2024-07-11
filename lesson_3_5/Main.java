package lesson_3_5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        ArrayList<User> arrayUsers = new ArrayList<>();

        String name;
        int age;
        User user;
        for(int i = 0; i < 5; i++) {
            System.out.println("Введите имя пользователя " + (i + 1));
            name = in.nextLine();

            System.out.println("Введите возраст пользователя " + (i + 1));
            age = in.nextInt();
            in.nextLine();

            user = new User(name, age);
            arrayUsers.add(user);
        }

        Collections.sort(arrayUsers, new Comparator<User>() {
            public int compare(User u1, User u2) {
                return u1.getAge().compareTo(u2.getAge());
            }
        });

        System.out.println();
        for(User u : arrayUsers) {
            System.out.println(u);
        }

        in.close();
    }
}
