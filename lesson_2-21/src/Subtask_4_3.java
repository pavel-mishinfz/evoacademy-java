import java.util.Scanner;

public class Subtask_4_3 {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Введите дату в формате 'дд.мм.гггг'");
        String date = in.next();
        String[] parseDate = date.split("\\.");
        StringBuilder formatedDate = new StringBuilder();
        for(int i = parseDate.length - 1; i >= 0; i--) {
            formatedDate.append(parseDate[i]).append('-');
        }
        formatedDate.deleteCharAt(formatedDate.length() - 1);
        System.out.println(formatedDate);

        in.close();
    }
}
