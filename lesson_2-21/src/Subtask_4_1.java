import java.util.Scanner;

public class Subtask_4_1 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Введите строку");
        String str = in.nextLine();
        System.out.println("Введите подстроку");
        String subStr = in.nextLine();

        int counter = 0;
        int fromIndex = 0;
        int lenSubStr = subStr.length();
        while(str.indexOf(subStr, fromIndex) != -1) {
            fromIndex = str.indexOf(subStr, fromIndex);
            fromIndex += lenSubStr;
            counter++;
        }

        System.out.println("Подстрока '" + subStr + "' встречается " + counter + " раза");
        in.close();
    }
}