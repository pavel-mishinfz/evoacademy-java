import java.util.Scanner;

public class Subtask_4_2 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        final String BAD_WORD_KAKA = "кака";
        final String BAD_WORD_BYAKA = "бяка";
        final String CENSOR = "вырезано цензурой";

        System.out.println("Введите строку");
        String str = in.nextLine();
        String censorStr = str.replace(BAD_WORD_KAKA, CENSOR).replace(BAD_WORD_BYAKA, CENSOR);
        System.out.println(censorStr);

        in.close();
    }
}
