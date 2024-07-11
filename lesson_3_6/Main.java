package lesson_3_6;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        HashMap<Integer, List<User>> mapUsers = new HashMap<>();
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
            if(mapUsers.containsKey(age)) {
                mapUsers.get(age).add(user);
            }
            else {
                ArrayList<User> newArrayListUsers = new ArrayList<>();
                newArrayListUsers.add(user);
                mapUsers.put(age, newArrayListUsers);
            }
        }

        System.out.println("\nВведите требуемый возраст");
        int targetAge = in.nextInt();

        if(mapUsers.containsKey(targetAge)) {
            mapUsers.get(targetAge).sort(new Comparator<User>() {
                public int compare(User u1, User u2) {
                    return u1.getName().compareTo(u2.getName());
                }
            });

            for(User u : mapUsers.get(targetAge)) {
                System.out.println(u);
            }
        }
        else {
            System.out.println("Пользователь с возрастом '" + targetAge + "' не найден");
        }

        in.close();
    }
}
